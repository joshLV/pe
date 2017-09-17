package com.csii.mybank.trans.action;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.CommunicationException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.signature.XmlSignature;
import com.csii.pp.util.MiscUtil;
import com.csii.weixin.trans.action.PRNTAction;

public class MBNTAction extends AbstractTwoPhaseAction{

	private static Logger logger = LoggerFactory.getLogger(MBNTAction.class);
	private OrderManager orderManager;
	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Map MybankNotifyMap = (Map) ctx.getData("MybankNotifyMap");
        ctx.setData("_TransName", ctx.getTransactionId());
		
		Map map = new HashMap();
		String outTradeNo = (String) MybankNotifyMap.get("OutTradeNo");
		map.put("TransSeqNo", outTradeNo);
		Order order = (Order) getSqlMap().queryForObject("pp.core.queryOtByTransSeqNo", map);
		if (order == null) {
			order = (Order) getSqlMap().queryForObject("pp.core.queryHtByTransSeqNo", map);
		}
		if (order == null) {
			logger.error("网商支付通知 - 商户订单号[{}]未找到", outTradeNo);
			ctx.setData("ResultCode", "FAIL");
			ctx.setData("ResultMsg", "商户订单号未找到");
			ctx.setData("ResultStatus", "F");
			throw new PeException("E00012", new Object[] { outTradeNo });
		}

		String orderStatus = order.getStatus();
		
		if (!orderStatus.equals("07") && !orderStatus.equals("08")&&!orderStatus.equals("99")) {
			logger.error("网商支付通知 - 商户订单号[{}]状态为[{}] - 不需要做订单状态更新", outTradeNo, orderStatus);
			ctx.setData("ResultCode", "0000");
			ctx.setData("ResultMsg", "通知成功");
			ctx.setData("ResultStatus", "S");
			return;
		}
		
		String message =(String) ctx.getData("Message");
		String alipayPubKey = "";
		try {
			if(!XmlSignature.verifyXmlMessage(message, XmlSignature.getPublicKey(alipayPubKey.getBytes()))){
				log.error("网商银行返回报文验证签名失败");
				ctx.setData("ResultCode", "FAIL");
				ctx.setData("ResultMsg", "验签失败");
				ctx.setData("ResultStatus", "F");
			}else{
				log.info("网商银行返回报文验证签名成功");
				order.setReturnCode(Constants.AAAAAAA);
				order.setReturnMsg("交易成功");
				order.setStatus(Constants.TRANS_STATUS_OK);
				
				order.setHostSeqNo(MiscUtil.toStringAndTrim(MybankNotifyMap.get("OrderNo")));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				order.setHostDateTime(Timestamp.valueOf(MiscUtil.toStringAndTrim(MybankNotifyMap.get("GmtPayment"))));
				order.setHostDate(sdf.parse(MiscUtil.toStringAndTrim(MybankNotifyMap.get("GmtPayment")).substring(0, 10)));
				order.setBankType(MiscUtil.toStringAndTrim(MybankNotifyMap.get("BankType")));
				String amountStr = MiscUtil.DivideHundred(MybankNotifyMap.get("cash_fee"));
				BigDecimal amount = new BigDecimal(amountStr).setScale(2, BigDecimal.ROUND_HALF_UP);
				order.setAmount(amount);
				
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
						log.debug("aaaaa"+order.getAmount3());
						log.info("kkkkk"+order.getAmount3());
						log.debug("bbbbb"+ctrl.get("PerDayAmt"));
						log.debug("ccccc"+((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
						ctrl.put("PerDayAmt",((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
						this.getSqlMap().update("pp.core.updateMerLimitCtrl",ctrl);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("网商银行返回报文验证签名异常",e);
			ctx.setData("ResultCode", "FAIL");
			ctx.setData("ResultMsg", "通知失败");
			ctx.setData("ResultStatus", "F");
		}
	}
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
	
}
