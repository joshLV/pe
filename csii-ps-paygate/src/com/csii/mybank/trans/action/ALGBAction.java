package com.csii.mybank.trans.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;

public class ALGBAction extends AbstractTwoPhaseAction {

	private String url;
	private OrderManager orderManager;

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Map map = new HashMap();
		map.put("TransSeqNo", ctx.getData("out_trade_no"));
		Order order = this.orderManager.getOrder(ctx.getString("out_trade_no"));
		Map paraMap = new HashMap();
		paraMap.put("bankId", order.getBankId());
		paraMap.put("departmentId", order.getDepartmentId());
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", paraMap);
		String alipayBankPriKey = (String) MerBankParameterMap.get("alipayBankPriKey");
		String alipayPubKey = (String) MerBankParameterMap.get("alipayPubKey");
		String alipayAppId = (String) MerBankParameterMap.get("alipayAppId");
		Map sendMap = new HashMap();
//		报文头
		sendMap.put("_TransName", "ALGB");
		sendMap.put("Appid", alipayAppId);
		sendMap.put("Function", "ant.mybank.bkmerchanttrade.payClose");//ant.mybank.bkmerchanttrade.payCancel
		sendMap.put("ReqTime", MiscUtil.timeToString(order.getTransDateTime()));
		sendMap.put("ReqMsgId", MiscUtil.timeToString(order.getTransDateTime()));
		//报文体
		sendMap.put("IsvOrgId", order.getBankId());
		sendMap.put("MerchantId", order.getMerWxId());
		sendMap.put("OutTradeNo", ctx.getData("out_trade_no"));
		//请求url
		sendMap.put("submitURL", url);
		sendMap.put("alipayBankPriKey", alipayBankPriKey);
		sendMap.put("alipayPubKey", alipayPubKey);
		log.info("支付关闭订单接口支付平台流水号[" + order.getTransSeqNo() + "]签名数据为：" + sendMap);
		Map hostRsp = (Map) this.issueAlipayCoreHostTrs(ctx, sendMap);

		ctx.setDataMap(hostRsp);
		if (Constants.QRCODE_FAIL.equals(hostRsp.get("return_code"))) {
			ctx.setData("ReturnCode", "FAIL");
			// order.setReturnCode(MiscUtil.toStringAndTrim(ctx.getData("ReturnCode")));
			// order.setReturnMsg(MiscUtil.toStringAndTrim(ctx.getData("ReturnMsg")));
		} else {
				// 响应成功并且处理成功
				if (Constants.QRCODE_SUCCESS.equals(hostRsp.get("return_code"))) {
					if (Constants.QRCODE_SUCCESS.equals(hostRsp.get("result_code"))) {
						order.setReturnCode(Constants.AAAAAAA);
						order.setStatus(Constants.TRANS_STATUS_ORDER_CLOSE);
						orderManager.updateOrder(order);
						ctx.setData("ReturnCode", Constants.AAAAAAA);
					} else {
						log.info("阿里支付关闭订单接口支付平台流水号[" + order.getTransSeqNo() + "],阿里业务响应码[" + hostRsp.get("result_code") + "],阿里错误码[" + hostRsp.get("err_code") + "],阿里错误信息[" + hostRsp.get("err_code_des"));
						ctx.setData("ReturnCode", "FAIL");
					}
				} else {
					log.info("阿里支付关闭订单接口支付平台流水号[" + order.getTransSeqNo() + "],阿里通信错误码[" + hostRsp.get("return_code") + "],阿里通信错误信息[" + hostRsp.get("return_msg"));
					ctx.setData("ReturnCode", "FAIL");
				}
		}
		Map json = new HashMap();
		String returnCode = (String) ctx.getData("return_code");
		String returnMsg = (String) ctx.getData("return_msg");
		String resultCode = (String) ctx.getData("result_code");
		String errorCode = (String) ctx.getData("err_code");
		String errorMsg = (String) ctx.getData("err_code_des");

		if ("SUCCESS".equals(returnCode)) {
			if ("SUCCESS".equals(resultCode)) {
				json.put("ReturnCode", Constants.AAAAAAA);
				json.put("ReturnMsg", "");
			} else {
				json.put("ReturnCode", errorCode);
				json.put("ReturnMsg", errorMsg);
			}
		} else {
			json.put("ReturnCode", returnCode);
			json.put("ReturnMsg", returnMsg);
		}
		ctx.setData("json", json);
	}


	public void setUrl(String url) {
		this.url = url;
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
