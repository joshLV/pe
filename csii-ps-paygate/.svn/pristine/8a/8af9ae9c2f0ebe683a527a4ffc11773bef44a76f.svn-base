package com.csii.mybank.trans.action;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;

/**
 * 查询退款接口action
 * */
public class TKCXAction extends AbstractTwoPhaseAction {


	private String url;
    
	

	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public void execute(Context ctx) throws PeException {
		Order order = (Order) ctx.getVariable();
		String status = order.getStatus();
		Map MerBankParameterMap = (Map) ctx.getData("MerBankParameterMap");
		if (Constants.TRANS_STATUS_PROCESSING.equals(status)) {

		ctx.setData("QryWxTransStatus", true);
		Map<String,Object> reqMap = new HashMap<String,Object>();
		reqMap.put("Version", "1.0.0"); // 版本号
		reqMap.put("Function", "ant.mybank.bkmerchanttrade.refundQuery"); // 接口代码
		reqMap.put("ReqTime", MiscUtil.getCurrentTimeString()); // 报文发起时间
		reqMap.put("ReqTimeZone", "UTC+8"); // 报文发起时区
		reqMap.put("ReqMsgId", MiscUtil.getCurrentTimeString()); // 请求报文ID
		reqMap.put("InputCharset", "UTF-8"); // 报文字符编码
		reqMap.put("Reserve", ""); // 保留字段
		reqMap.put("SignType", "RSA"); // 签名方式
		
		reqMap.put("OutRefundNo", order.getTransSeqNo()); 
		reqMap.put("IsvOrgId", MerBankParameterMap.get("mybankIsvOrgId"));
		reqMap.put("MerchantId", order.getMerWxId());
		
		reqMap.put("_TransName", "TKCX"); // 创建订单终端的IP
		reqMap.put("submitURL", url); 
		reqMap.put("mybankJfPriKey", MerBankParameterMap.get("mybankJfPriKey"));
		reqMap.put("mybankPubKey", MerBankParameterMap.get("mybankPubKey"));
		reqMap.put("Appid",order.getAppid()); // 应用ID
		Map resMap = (Map) this.issueAlipayCoreHostTrs(ctx, reqMap);
		resMap.remove("Signature");

		ctx.setDataMap(resMap);

		log.info("网商-支付宝支付查询退款接口支付平台流水号[" + order.getTransSeqNo() + "]网商-支付宝返回信息如下:" + resMap);

		String returnCode = (String) resMap.get("ResultCode");
		String returnMsg = (String) resMap.get("ResultMsg");
		ctx.setData("ReturnCode", returnCode);
		ctx.setData("ReturnMsg", returnMsg);


		if (Constants.WS_ALIPAY_SUCCESS.equals(returnCode)) {
			String ResultStatus = (String) resMap.get("ResultStatus");
			
			String ReturnCode = (String) resMap.get("ResultCode");
			String ReturnMsg = (String) resMap.get("ResultMsg");
			ctx.setData("ReturnCode", "000000");
			ctx.setData("ReturnMsg", "交易成功");

				if ("S".equals(ResultStatus)) {
					String TradeStatus  = (String) resMap.get("TradeStatus");

					if ("succ".equals(TradeStatus)) {
						order.setReturnCode(Constants.AAAAAAA);
						order.setReturnMsg("交易成功");
						order.setHostSeqNo(MiscUtil.toStringAndTrim(ctx.getData("RefundOrderNo")));
						String hostDateTime = MiscUtil.toStringAndTrim(ctx.getData("GmtRefundment"));
						order.setHostDateTime(Timestamp.valueOf(hostDateTime));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						order.setHostDate(MiscUtil.calStringToDate(hostDateTime.substring(0,10)));
					}else if("refunding".equals(TradeStatus)){
						order.setReturnCode(Constants.QRCODE_PROCESSING);
						order.setReturnMsg("退款处理中");
					}else{
						order.setReturnCode("FAIL");
						order.setReturnMsg("退款失败");
					}

					order.setStatus(WxUtil.getTransStatus(TradeStatus));

					ctx.setVariable(order);
				} else {
					String errorCode = (String) resMap.get("ResultCode");
					String errorMsg = (String) resMap.get("ResultMsg");
					log.error("网商-支付宝查询退款 - 支付平台流水号[" + order.getTransSeqNo() + "]交易失败，错误码[" + errorCode + "]，错误信息[" + errorMsg + "]");
					throw new PeException(errorCode);
				}
			
		}
	} else {
			ctx.setData("QryWxTransStatus", false);
			log.info("平台流水号[" + order.getTransSeqNo() + "]订单状态为[" + status + "]，故不发送请求至网商-支付宝查询退款状态");
		}
	}






	public void setUrl(String url) {
		this.url = url;
	}

	
}
