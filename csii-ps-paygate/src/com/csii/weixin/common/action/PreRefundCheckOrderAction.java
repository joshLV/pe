package com.csii.weixin.common.action;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.model.User;
import com.csii.pp.order.Order;
import com.csii.pp.util.BCrypt;
import com.csii.pp.util.MiscUtil;

public class PreRefundCheckOrderAction extends AbstractAction {

	private static Logger logger = LoggerFactory.getLogger(PreRefundCheckOrderAction.class);

	// 检查退款周期
	private int drawMonth = 12;

	@Override
	public void execute(Context ctx) throws PeException {

		// 订单
		Order order = (Order) ctx.getVariable();
		// 原支付订单
		Order orgOrder = order.getOrgOrder();
		
		if("1".equals(ctx.getData("checkflag"))){
			String userId = order.getCashierId();
			String password = (String) ctx.getData("password");

			Map userDataMap = new HashMap();
			userDataMap.put("userId", userId);
			User user = (User) this.getSqlMap().queryForObject("pp.core.qryUserInfo", userDataMap);

			if (user == null) {
				logger.error("用户名[{}]不存在.", userId);
				throw new PeException("E00014");
			}
			
			if("1".equals(user.getUserStatus())){
				throw new PeException("E00021");
			}
			if (!BCrypt.checkpw(password, user.getPassword())) {
				throw new PeException("E00025");
			}
		}

		if(order.getTransId().equals("APRF")) {
			String orgTransAmt= (String) ctx.getData("total_fee");
			if(!MiscUtil.isNullOrEmpty(orgTransAmt)) {
				BigDecimal orAmount =  new BigDecimal(MiscUtil.DivideHundred(orgTransAmt)).setScale(2, BigDecimal.ROUND_HALF_UP);
				if(orAmount.compareTo(orgOrder.getAmount())!=0) {
					throw new PeException("E10003");
				}
			}
			
			//统计交易待付中的退款，（状态不明、成功、交易超时），不能再次退款 by wzj
			BigDecimal countAmount = (BigDecimal) this.getSqlMap().queryForObject("pp.core.queryAPRFcountAmt", orgOrder);
			if(null !=countAmount && countAmount.compareTo(orgOrder.getAmount())>0) {
				log.error("原支付订单已经");
				throw new PeException("E00007");
			}
		}
		
		if (order.getAmount().compareTo(new BigDecimal(0)) <= 0) {
			logger.error(new StringBuffer("支付平台流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("交易金额[").append(order.getAmount())
					.append("] ").append("交易金额非法").toString());
			throw new PeException("E00004");
		}

		Map merMap = new HashMap();
		merMap.put("mer_id", order.getSubMerchantId());
		Merchant merchant = (Merchant) this.getSqlMap().queryForObject("pp.core.qryMerId", merMap);
		if (!MiscUtil.trimAndEquals(merchant.getMerStatus(), "0")) {
			throw new PeException("E00013");
		}

		if (MiscUtil.isNullOrEmpty(order.getOrgTransSeqNo())) {
			log.error(new StringBuffer("网关流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("原商户流水号必须输入").append(" 原商户流水[")
					.append(order.getOrgHostDate()).append("]").toString());
			throw new PeException("E00006");
		}

		String orgTransId = orgOrder.getTransId();

		// 原交易已退货金额
		BigDecimal alreadyWithdrawAmount = orgOrder.getAmount1();

		if (alreadyWithdrawAmount == null) {
			alreadyWithdrawAmount = new BigDecimal(0);
		}

		// 退货金额 > 原交易金额-原交易已退货金额
		if (order.getAmount().compareTo(orgOrder.getAmount3().subtract(alreadyWithdrawAmount)) > 0) {
			log.error(new StringBuffer("网关流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("退货金额超过支付金额\n").append("原交易[")
					.append(orgTransId).append("]").append("可退金额[").append(orgOrder.getAmount3().subtract(alreadyWithdrawAmount)).append("]").append("当前交易[").append(order.getTransId()).append("]")
					.append("退货金额[").append(order.getAmount()).append("]").toString());
			throw new PeException("E00007");
		}

		// 检查退货期限
		this.checkDateNotBefore(orgOrder);

		// 原交易响应码检查
		String responseCode = MiscUtil.toStringAndTrim(orgOrder.getReturnCode());

		if (!MiscUtil.trimAndEquals(responseCode, Constants.AAAAAAA)) {
			log.error(new StringBuffer("网关流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("原交易响应码不正常").append("原交易响应码[")
					.append(responseCode).append("]").toString());
			throw new PeException("E00009");
		}

		// 原交易状态
		String status = MiscUtil.toStringAndTrim(orgOrder.getStatus());

		if (MiscUtil.isNullOrEmpty(status) || (!status.equals("00") && !status.equals("03"))) {
			log.error(new StringBuffer("网关流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("原交易完成标志必须为成功或者部分退货").append("原交易完成标志[")
					.append(status).append("]").toString());
			throw new PeException("E00009");
		}

	}

	// 检查是否超过退货期限
	private void checkDateNotBefore(Order order) throws PeException {

		Calendar clearDate = Calendar.getInstance();
		clearDate.setTime(new Date());
		clearDate.add(Calendar.MONTH, -drawMonth);

		// 可以退货的最早日期
		Date rolDate = new Date(clearDate.getTimeInMillis());

		// 判断原交易的清算日期是否比可以退货的最早日期还要早
		if (order.getClearDate().before(rolDate)) {
			log.error(new StringBuffer("网关流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("退货交易的清算日期超出了允许的日期范围").append("原清算日期[")
					.append(order.getClearDate()).append("]").append("退货期限[").append(drawMonth).append("]").toString());
			throw new PeException("E00008");
		}
	}
}
