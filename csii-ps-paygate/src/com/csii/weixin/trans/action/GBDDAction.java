package com.csii.weixin.trans.action;

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

public class GBDDAction extends AbstractAction {

	private WxUtil wxUtil;
	private String url;
	private OrderManager orderManager;

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Map map = new HashMap();
		map.put("TransSeqNo", ctx.getData("out_trade_no"));
		Order order = this.orderManager.getOrder(ctx.getString("out_trade_no"));

		Map sendMap = new HashMap();
		sendMap.put("appid", order.getAppid());
		sendMap.put("mch_id", order.getBankId());
		sendMap.put("sub_mch_id", order.getMerWxId());
		sendMap.put("nonce_str", MiscUtil.getRandom());
		sendMap.put("out_trade_no", ctx.getData("out_trade_no"));// 平台流水
		Map paraMap = new HashMap();
		paraMap.put("bankId", order.getBankId());
		paraMap.put("departmentId", order.getDepartmentId());
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", paraMap);
		 String paysignkey = (String) MerBankParameterMap.get("paysignkey");
		String str1 = MiscUtil.sign(sendMap,paysignkey);

		log.info("微信支付关闭订单接口支付平台流水号[" + order.getTransSeqNo() + "]签名数据为：" + str1);
		sendMap.put("sign", str1);

		String xmlData = wxUtil.parseXML(sendMap);

		byte[] bytea = wxUtil.httpSend(xmlData.getBytes(), url);

		String str = "";
		try {
			str = new String(bytea, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map xmap = XmlUtil.toMap(str, "xml");
		ctx.setDataMap(xmap);
		if (Constants.QRCODE_FAIL.equals(xmap.get("return_code"))) {
			ctx.setData("ReturnCode", "FAIL");
			// order.setReturnCode(MiscUtil.toStringAndTrim(ctx.getData("ReturnCode")));
			// order.setReturnMsg(MiscUtil.toStringAndTrim(ctx.getData("ReturnMsg")));
		} else {
			String Sign = (String) xmap.get("sign");
			xmap.remove("sign");
			String tSign = MiscUtil.sign(xmap,paysignkey);

			if (Sign.equals(tSign)) {
				log.info("微信支付关闭订单接口支付平台流水号[" + order.getTransSeqNo() + "]验签成功");
				// 响应成功并且处理成功
				if (Constants.QRCODE_SUCCESS.equals(xmap.get("return_code"))) {
					if (Constants.QRCODE_SUCCESS.equals(xmap.get("result_code"))) {
						order.setReturnCode(Constants.AAAAAAA);
						order.setStatus(Constants.TRANS_STATUS_ORDER_CLOSE);
						orderManager.updateOrder(order);
						ctx.setData("ReturnCode", Constants.AAAAAAA);
					} else {
						log.info("微信支付关闭订单接口支付平台流水号[" + order.getTransSeqNo() + "],微信业务响应码[" + xmap.get("result_code") + "],微信错误码[" + xmap.get("err_code") + "],微信错误信息[" + xmap.get("err_code_des"));
						ctx.setData("ReturnCode", "FAIL");
					}
				} else {
					log.info("微信支付关闭订单接口支付平台流水号[" + order.getTransSeqNo() + "],微信通信错误码[" + xmap.get("return_code") + "],微信通信错误信息[" + xmap.get("return_msg"));
					ctx.setData("ReturnCode", "FAIL");
				}
			} else {

				log.info("微信关闭订单支付接口支付平台流水号[" + order.getTransSeqNo() + "]验签失败");
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
