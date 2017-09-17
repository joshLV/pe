package com.csii.weixin.trans.action;

import java.util.HashMap;
import java.util.Map;

import com.csii.alipay.zhilian.trans.action.APRFQueryAction;
import com.csii.ibs.action.AbstractAction;
import com.csii.mybank.trans.action.TKCXAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;

/**
 * 查询退款接口action
 * */
public class TRCXAction extends AbstractAction {

	private WxUtil wxUtil;

	private String url;

	private OrderManager orderManager;
	
	private TKCXAction tkcxAction;

	private APRFQueryAction aprfQrAction;

	@Override
	public void execute(Context ctx) throws PeException {
		Order order = (Order) ctx.getVariable();
		
		if(Constants.WS_MYBANK_DEPARTMENTID.equals(order.getDepartmentId())) {
			tkcxAction.execute(ctx);
		}else  if("weixin".equals(order.getDepartmentId())){
			
			Map MerBankParameterMap = (Map) ctx.getData("MerBankParameterMap");
			String status = order.getStatus();
			String paysignkey = (String) MerBankParameterMap.get("paysignkey");
			if (Constants.TRANS_STATUS_PROCESSING.equals(status) || status==null) {
	
			ctx.setData("QryWxTransStatus", true);
			Map sendMap = new HashMap();
			sendMap.put("appid", order.getAppid()); // 公众帐号ID
			sendMap.put("mch_id", MerBankParameterMap.get("merId")); // 商户号
			sendMap.put("sub_mch_id", order.getMerWxId()); // 子商户号
			sendMap.put("nonce_str", MiscUtil.getRandom()); // 随机字符串
			sendMap.put("out_refund_no", ctx.getData("out_refund_no")); // 商户退款单号
	
			// 获取签名字符串
			String signStr = MiscUtil.sign(sendMap,paysignkey);
	
			log.info("微信支付查询退款接口支付平台流水号[" + order.getTransSeqNo() + "]签名数据为：" + signStr);
			sendMap.put("sign", signStr);
	
			String xmlData = wxUtil.parseXML(sendMap);
	
			byte[] bytea = wxUtil.httpSend(xmlData.getBytes(), url);
	
			String str = MiscUtil.getStrMsg(bytea, "UTF-8");
	
			Map wxResMap = XmlUtil.toMap(str, "xml");
	
			ctx.setDataMap(wxResMap);
	
			log.info("微信支付查询退款接口支付平台流水号[" + order.getTransSeqNo() + "]微信返回信息如下:" + wxResMap);
			String returnCode = (String) wxResMap.get("return_code");
			String returnMsg = (String) wxResMap.get("return_msg");
	
			if (Constants.QRCODE_SUCCESS.equals(returnCode)) {
				String sign = (String) wxResMap.get("sign");
				String resultCode = (String) wxResMap.get("result_code");
				String errorCode = (String) wxResMap.get("err_code");
				String errorMsg = (String) wxResMap.get("err_code_des");
				wxResMap.remove("sign");
	
				String tSign = MiscUtil.sign(wxResMap,paysignkey);
	
				if (sign.equals(tSign)) {
					if (Constants.QRCODE_SUCCESS.equals(resultCode)) {
	
						String refund_count = (String) wxResMap.get("refund_count");
						if (!MiscUtil.isNullOrEmpty(refund_count)) {
							for (int i = 0; i <= Integer.valueOf(refund_count)-1; i++) {
								if (order.getTransSeqNo().equals(wxResMap.get("out_refund_no_" + i))) {
									ctx.setData("refund_id", wxResMap.get("refund_id" + i));
									ctx.setData("refund_status", wxResMap.get("refund_status_" + i));
								}
							}
						}
	
						if (Constants.QRCODE_SUCCESS.equals(ctx.getData("refund_status"))) {
							order.setReturnCode(Constants.AAAAAAA);
							order.setReturnMsg("交易成功");
	
							order.setHostSeqNo(MiscUtil.toStringAndTrim(ctx.getData("refund_id")));
						}else if("PROCESSING".equals(ctx.getData("refund_status"))){
							order.setReturnCode(Constants.QRCODE_PROCESSING);
							order.setReturnMsg("退款处理中");
						}else if("CHANGE".equals(ctx.getData("refund_status"))){
							order.setReturnCode("CHANGE");
							order.setReturnMsg("转入代发");
						}else{
							order.setReturnCode("FAIL");
							order.setReturnMsg("退款失败");
						}
	
						order.setStatus(WxUtil.getTransStatus(MiscUtil.toStringAndTrim(ctx.getData("refund_status"))));
	
						ctx.setVariable(order);
					} else {
						log.error("微信订单查询 - 支付平台流水号[" + order.getTransSeqNo() + "]交易失败，错误码[" + errorCode + "]，错误信息[" + errorMsg + "]");
						throw new PeException(errorCode);
					}
				} else {
					log.error("微信订单查询 - 支付平台流水号[" + order.getTransSeqNo() + "]验签失败.");
					throw new PeException("000002");
				}
			}
		} else {
				ctx.setData("QryWxTransStatus", false);
				log.info("平台流水号[" + order.getTransSeqNo() + "]订单状态为[" + status + "]，故不发送请求至微信查询退款状态");
			}
			
	 }else {
		 
		 log.info("平台流水号[" + order.getTransSeqNo() + "]，第三方支付渠道为["+order.getDepartmentId()+"],将发起查询交易");
		 aprfQrAction.execute(ctx);
	 }
	
	}

	
	
	
	
	public APRFQueryAction getAprfQrAction() {
		return aprfQrAction;
	}





	public void setAprfQrAction(APRFQueryAction aprfQrAction) {
		this.aprfQrAction = aprfQrAction;
	}





	public void setTkcxAction(TKCXAction tkcxAction) {
		this.tkcxAction = tkcxAction;
	}

	public void setWxUtil(WxUtil wxUtil) {
		this.wxUtil = wxUtil;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
