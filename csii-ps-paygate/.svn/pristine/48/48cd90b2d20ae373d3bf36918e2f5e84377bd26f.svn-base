package com.csii.alipay.zhilian.trans.action;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.common.util.CsiiUtils;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.weixin.notify.action.MerchantSendAction;

/**
 * 支付结果通知接口action
 * */
public class AliTradePayResNtAtion extends AbstractAction {

	private static Logger logger = LoggerFactory.getLogger(AliTradePayResNtAtion.class);
	private MerchantSendAction merchantSendAction;
	private OrderManager orderManager;

	@Override
	public void execute(Context ctx) throws PeException {
		Order order = (Order) ctx.getVariable();
		
		String orderStatus = order.getStatus();
		if (!orderStatus.equals("07") && !orderStatus.equals("08")) {
			logger.error("支付宝通知 - 商户订单号[{}]状态为[{}] - 不需要做订单状态更新", order.getStatus(), orderStatus);
			ctx.setData("ReturnCode", "success");
			ctx.setData("ReturnMsg", "OK");
			return;
		}

		// 通知成功状态
			
		
		ctx.setData("ReturnCode", "success");
		ctx.setData("ReturnMsg", "OK");
		
		String tradeStatus = (String) ctx.getData("trade_status");
		//如果状态是成功的话，触发以下逻辑
		if(!MiscUtil.isNullOrEmpty(tradeStatus)  &&  tradeStatus.equals("TRADE_SUCCESS")) {
			
			log.info("支付宝通知 - 商户订单号[{"+order.getTransSeqNo()+"}] - 订单状态更新开始...");
			order.setReturnCode(Constants.AAAAAAA);
			order.setReturnMsg("交易成功");
			order.setStatus(Constants.TRANS_STATUS_OK);
			
			String amountStr = ctx.getString("total_amount")==null?"0":ctx.getString("total_amount").toString();
			
			BigDecimal amount = new BigDecimal(amountStr).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			if(amount.compareTo(order.getAmount())!=0) {
				log.info("支付宝支付结果通知接口支付平台流水号[" + order.getTransSeqNo() + "]交易金额异常");
				ctx.setData("ReturnCode", "fail");
				ctx.setData("ReturnMsg", "交易金额异常");
				return ;
			}
			order.setAmount(amount);
			order.setOpenid(MiscUtil.toStringAndTrim(ctx.getString("buyer_logon_id")));
			
			
			if(!order.getAppid().equals(ctx.getString("app_id"))) {
				log.info("支付宝支付结果通知接口支付平台流水号[" + order.getTransSeqNo() + "]appid异常");
				ctx.setData("ReturnCode", "fail");
				ctx.setData("ReturnMsg", "appid不符");
				return ;
			}
			
			order.setHostSeqNo(ctx.getString("trade_no")); //支付宝流水号
			
			
			// 核心交易时间
			String gmt_close =ctx.getString("gmt_payment")==null?null:ctx.getString("gmt_payment").toString().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
			if(gmt_close!=null) {
				String hostDateTime = MiscUtil.toStringAndTrim(gmt_close);
				order.setHostDateTime(new Timestamp(CsiiUtils.bocmDatetimeToCal(hostDateTime).getTimeInMillis()));
				// 商户日期
				String hostDate = MiscUtil.toStringAndTrim(gmt_close).substring(0, 8);
				order.setHostDate(MiscUtil.calStringToDate(hostDate));
			}else {
				order.setHostDate(order.getClearDate());
			}
			
			boolean  isnotify=false;
			
			// 更新交易状态 
			orderManager.updateOrder(order);
			//交易成功，扣减限额，发送商户通知
			if ("00".equals(order.getTransType())) {
				Map para1 = new HashMap();
				para1.put("MerchantId", order.getSubMerchantId());
				para1.put("Status", "0");
				//商户交易限额控制
				Map ctrl = (Map) this.getSqlMap().queryForObject("pp.core.queryMerchantTransCtrlDynamic",para1);
				log.info("order======"+order.getAmount3());
				if (ctrl != null) {
					log.debug("当前日限额为"+((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
					ctrl.put("PerDayAmt",((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
					this.getSqlMap().update("pp.core.updateMerLimitCtrl",ctrl);
				}
				isnotify= true;
			}
			
			if(isnotify){
				//发送商户通知
				ctx.setVariable(order);
				merchantSendAction.execute(ctx);
			}
			log.info("支付宝支付结果通知接口支付平台流水号[" + order.getTransSeqNo() + "]更新订单成功");
		}else {
			log.info("支付宝通知我方 的商户订单号为[{"+order.getTransSeqNo()+"}]，交易状态为["+tradeStatus+"]， 订单状态将不参与更新...");
		}

	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
	/**
	 * @param action
	 */
	public void setMerchantSendAction(MerchantSendAction action) {
		merchantSendAction = action;
	}
}
