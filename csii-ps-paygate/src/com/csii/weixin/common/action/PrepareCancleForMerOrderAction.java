package com.csii.weixin.common.action;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.common.util.CsiiUtils;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.id.IdFactory;
import com.csii.pp.dict.Constants;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.merchant.MerchantSett;
import com.csii.pp.model.User;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;

/**
 * 准备撤销订单
 * @author Administrator
 *
 */
public class PrepareCancleForMerOrderAction extends AbstractAction{
	
	private IdFactory cancleOrderSeqNoGenerator;
	private OrderManager orderManager;

	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Order order = new Order();
		// 流水号
		String transSeqNo = cancleOrderSeqNoGenerator.generate().toString();
		order.setTransSeqNo(transSeqNo);
		// 支付平台交易日期
		Date transDate = new Date(ctx.getTimestamp().getTime());
		order.setTransDate(transDate);
		// 支付平台交易时间
		Timestamp transDateTime = new Timestamp(ctx.getTimestamp().getTime());
		
		//商户日期
		String merchantDate =
		MiscUtil.toStringAndTrim(ctx.getData("merdate")).substring(0,8);
		order.setMerTransDate(MiscUtil.calStringToDate(merchantDate));
		
		//商户时间
		String merchantDateTime =
		MiscUtil.toStringAndTrim(ctx.getData("merdate"));
		order.setMerTransDateTime(
		new Timestamp(CsiiUtils.bocmDatetimeToCal(merchantDateTime).getTimeInMillis()));
		
		order.setTransDateTime(transDateTime);
		order.setTransId(MiscUtil.toStringAndTrim(ctx.getTransactionId()));
		order.setAppid(MiscUtil.toStringAndTrim(ctx.getString("appid")));
		String subMchId = MiscUtil.toStringAndTrim(ctx.getString("sub_mch_id"));
		if (MiscUtil.isNullOrEmpty(subMchId)) {
			log.error(new StringBuffer("支付平台流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("子商户ID未上送").toString());
			throw new PeException("E00002");
		}
		order.setSubMerchantId(subMchId);
		
		order.setBankId(ctx.getString("mch_id"));
		//币种
		order.setCurrency(Constants.Default_Currency);
		// 撤销金额
		order.setAmount(Constants.ZERO);
		// 总金额
		order.setAmount1(Constants.ZERO);
		// 预留金额2
		order.setAmount2(Constants.ZERO);
		// 预留金额3
		order.setAmount3(Constants.ZERO);
		// 预留金额4
		order.setAmount4(Constants.ZERO);
		// 手续费金额
//		order.setFeeAmount(Constants.ZERO);
		order.setStep("0");
		order.setProcStatus("0");
		order.setTransType("02");//撤销
		order.setDepartmentId("weixin");
		order.setRemark1(MiscUtil.toStringAndTrim(ctx.getString("remark1")));
		order.setRemark2(MiscUtil.toStringAndTrim(ctx.getString("remark2")));
		
		//原订单号
		order.setOrgMerSeqNo(
			MiscUtil.toStringAndTrim(ctx.getData("orgmerseqno")));

		//原商户日期
		String orgmerDate =
		MiscUtil.toStringAndTrim(ctx.getData("orgmerdate")).substring(0,8);
		order.setOrgMerTransDate(MiscUtil.calStringToDate(orgmerDate));
		
		//mchid
		Merchant merInfo = (Merchant)this.getSqlMap().queryForObject("pp.core.qryParentMerId", ctx.getDataMap());
		if (merInfo == null) {
			log.error(new StringBuffer("支付平台流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("商户信息不存在").toString());
			throw new PeException("E00003");
		}
		String parentMerId = merInfo.getMerParentId();
		order.setMerchantId(parentMerId);
		order.setMerchant(merInfo);
		order.setCashierId(MiscUtil.toStringAndTrim(ctx.getData("cashierId")));
		if (!MiscUtil.isNullOrEmpty(order.getCashierId())) {

			Map data = new HashMap();
			data.put("userId", order.getCashierId());
			User user = (User) getSqlMap().queryForObject("pp.core.qryUserInfo", data);

			if (user == null) {
				log.error("收银员不存在，无法交易");
				throw new PeException("E00018");
			}
			order.setCashierName(MiscUtil.toStringAndTrim(user.getUserName()));
			if("1".equals(user.getUserStatus())){
				throw new PeException("E00021");
			}
		}
		DepartmentEntity departmentEntity = (DepartmentEntity) getSqlMap().queryForObject(Constants.QUERY_DEPARTMENT_ENTITY_SQL, "weixin");
		order.setClearDate(departmentEntity.getDepartmentDate());
		// 获取原支付订单
		Order orgOrder = orderManager.getOriginalOrderByMerInfo(order);
		if (orgOrder == null) {
			log.info(new StringBuffer("网关流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("******原支付订单信息不存在******").toString());
			throw new PeException("E00010");
		}
		order.setOrgOrder(orgOrder);
		order.setOrgTransSeqNo(orgOrder.getTransSeqNo());
		order.setOrgTransDate(orgOrder.getTransDate());
		order.setOrgTransDateTime(orgOrder.getTransDateTime());
		order.setOrgHostSeqNo(orgOrder.getHostSeqNo());
		order.setOrgHostDate(orgOrder.getHostDate());
		order.setAmount(orgOrder.getAmount3());
		order.setAmount3(orgOrder.getAmount3());
		order.setAppid(orgOrder.getAppid());
		order.setOpenid(orgOrder.getOpenid());
		order.setBankId(orgOrder.getBankId());
		order.setBankType(orgOrder.getBankType());
		order.setPayType(orgOrder.getPayType());
		order.setAgentid(orgOrder.getAgentid());
		order.setMerWxId(orgOrder.getMerWxId());
		Map para = new HashMap();
		para.put("merId", order.getSubMerchantId());
		para.put("payType", order.getPayType());
		para.put("departmentId", order.getDepartmentId());
		MerchantSett merchantSett =(MerchantSett) this.getSqlMap().queryForObject("pp.core.queryMerchantSett", para);
		BigDecimal feeAmount = order.getAmount().multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
		order.setFeeAmount(feeAmount.negate());
		Map para1 = new HashMap();
		para1.put("bankId", order.getBankId());
		para1.put("payType", order.getPayType());
		para1.put("departmentId", order.getDepartmentId());
		Map passageway = (Map) this.getSqlMap().queryForObject("pp.core.queryPassageWay",para1);
		BigDecimal costFeeAmount = order.getAmount().multiply(new BigDecimal(MiscUtil.toStringAndTrim(passageway.get("rate"))));
		order.setCostFeeAmount(costFeeAmount.negate());
		if(!MiscUtil.isNullOrEmpty(order.getAgentid())){
			Map para2 = new HashMap();
			para2.put("merId", order.getAgentid());
			para2.put("payType", order.getPayType());
			para2.put("departmentId", order.getDepartmentId());
			MerchantSett merchantSett1 =(MerchantSett) this.getSqlMap().queryForObject("pp.core.queryMerchantSett", para2);
			BigDecimal agentFeeAmount = order.getAmount().multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt().subtract(merchantSett1.getFeeAmt())))).setScale(2,BigDecimal.ROUND_HALF_UP);
			order.setAgentFeeAmount(agentFeeAmount.negate());
		}
		ctx.setVariable(order);
		
	}

	public IdFactory getCancleOrderSeqNoGenerator() {
		return cancleOrderSeqNoGenerator;
	}

	public void setCancleOrderSeqNoGenerator(IdFactory cancleOrderSeqNoGenerator) {
		this.cancleOrderSeqNoGenerator = cancleOrderSeqNoGenerator;
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	

}
