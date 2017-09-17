package com.csii.mybank.trans.action;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.csii.ibs.action.AbstractAction;
import com.csii.pe.common.util.CsiiUtils;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.core.AlipayClientManager;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;

public class APCXAction extends AbstractTwoPhaseAction{
	private String url;
	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Order order = (Order) ctx.getVariable();
		Map MerBankParameterMap = (Map) ctx.getData("MerBankParameterMap");
		String status = order.getStatus();
		
		if (Constants.TRANS_STATUS_PAYING.equals(status) || Constants.TRANS_STATUS_UNPAY.equals(status)) {

			ctx.setData("QryWxTransStatus", true);
			Map sendMap = new HashMap();
			sendMap.put("submitURL", url);
			sendMap.put("Appid", order.getAppid());
			sendMap.put("Function", "ant.mybank.bkmerchanttrade.payQuery");
			sendMap.put("ReqTime", MiscUtil.getCurrentTimeString());
			sendMap.put("ReqMsgId", MiscUtil.getCurrentTimeString());
			sendMap.put("InputCharset", "UTF-8");
			sendMap.put("Reserve", ""); // 保留字段
			sendMap.put("SignType", "RSA"); // 签名方式
			sendMap.put("ReqTimeZone", "UTC+8"); // 报文发起时区
			sendMap.put("IsvOrgId", MerBankParameterMap.get("mybankIsvOrgId"));
			sendMap.put("MerchantId", order.getMerWxId());
			sendMap.put("OutTradeNo", order.getTransSeqNo());
			sendMap.put("_TransName", "APCX");
			sendMap.put("mybankJfPriKey", MerBankParameterMap.get("mybankJfPriKey"));
			sendMap.put("mybankPubKey", MerBankParameterMap.get("mybankPubKey"));
			Map result =(Map) this.issueAlipayCoreHostTrs(ctx, sendMap);
			
			String returnCode = (String) result.get("ResultCode");
			String returnMsg = (String) result.get("ResultMsg");
			ctx.setData("ReturnCode", returnCode);
			ctx.setData("ReturnMsg", returnMsg);

			if (Constants.WS_ALIPAY_SUCCESS.equals(returnCode)) {
				String ResultStatus = (String) result.get("ResultStatus");
				
				String ReturnCode = (String) result.get("ResultCode");
				String ReturnMsg = (String) result.get("ResultMsg");
				ctx.setData("ReturnCode", "000000");
				ctx.setData("ReturnMsg", "交易成功");
					if ("S".equals(ResultStatus)) {
						String TradeStatus  = (String) result.get("TradeStatus");
						order.setStatus(WxUtil.getTransStatus(TradeStatus));
						if ("succ".equals(TradeStatus)) {
							order.setReturnCode(Constants.AAAAAAA);
							order.setReturnMsg("交易成功");
							order.setHostSeqNo(MiscUtil.toStringAndTrim(ctx.getData("OrderNo")));
							order.setRemark1(MiscUtil.toStringAndTrim(ctx.getData("PayChannelOrderNo")));
							order.setOpenid(MiscUtil.toStringAndTrim(ctx.getData("OpenId")));
							order.setBankType(MiscUtil.toStringAndTrim(ctx.getData("BankType")));
							String hostDateTime = MiscUtil.toStringAndTrim(ctx.getData("GmtPayment"));
							order.setHostDateTime(Timestamp.valueOf(hostDateTime));
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							order.setHostDate(MiscUtil.calStringToDate(hostDateTime.substring(0,10)));
						}else if("paying".equals(TradeStatus)){
							Map map = (Map) this.getSqlMap().queryForObject("pp.core.queryUnpayOrderByTransSeqNo", order.getTransSeqNo());
							if(map == null){
								insertOtQuery(order);
							}
						}

						ctx.setVariable(order);
					} else {
						String errorCode = (String) result.get("ResultCode");
						String errorMsg = (String) result.get("ResultMsg");
						log.error("网商-支付宝订单查询 - 支付平台流水号[" + order.getTransSeqNo() + "]交易失败，错误码[" + errorCode + "]，错误信息[" + errorMsg + "]");
						throw new PeException(errorCode);
					}
				
			}
			log.info("网商查询订单 - 支付平台流水号[" + order.getTransSeqNo() + "],网商返回信息如下:" + result);

		} else {
			ctx.setData("QryWxTransStatus", false);
			log.info("平台流水号[" + order.getTransSeqNo() + "]订单状态为[" + status + "]，故不发送请求至微信查询订单状态");
		}
		
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	private void insertOtQuery(Order order) {
		this.getSqlMap().insert("pp.core.insertOtQuery", order);
	}
}
