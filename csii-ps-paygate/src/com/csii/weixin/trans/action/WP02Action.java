package com.csii.weixin.trans.action;

import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;

public class WP02Action extends AbstractAction {

	private WxUtil wxUtil;

	private String url;

	private String notifyUrl;

	private AbstractAction duplicateOrderChecker;
	@Override
	public void execute(Context ctx) throws PeException {
		Order order = (Order) ctx.getVariable();
		this.duplicateOrderChecker.execute(ctx);
		Map MerBankParameterMap = (Map) ctx.getData("MerBankParameterMap");
		Merchant merchant = order.getMerchant();

		Map sendMap = new HashMap();
		sendMap.put("appid", order.getAppid());
		sendMap.put("mch_id", MerBankParameterMap.get("merId"));
		sendMap.put("sub_mch_id", order.getMerWxId());
		// sendMap.put("device_info", ctx.getData("device_info"));
		sendMap.put("nonce_str", MiscUtil.getRandom());
		sendMap.put("body", merchant.getMerShortName() + "-" + merchant.getMerAddr() + "-" + merchant.getManageDesc());
		sendMap.put("attach", wxUtil.getAttach(order));
		sendMap.put("out_trade_no", order.getTransSeqNo());
		sendMap.put("total_fee", ctx.getData("total_fee"));
		sendMap.put("spbill_create_ip", ctx.getData("spbill_create_ip"));
		sendMap.put("notify_url", notifyUrl);
		sendMap.put("trade_type", ctx.getData("trade_type"));
		sendMap.put("openid", order.getOpenid());
		sendMap.put("limit_pay", MiscUtil.toStringAndTrim(ctx.getData("limit_pay")));
		//下面3个参数不用传
		sendMap.put("sub_openid",MiscUtil.toStringAndTrim(ctx.getData("sub_openid")));
		sendMap.put("sub_appid", MiscUtil.toStringAndTrim(ctx.getString("sub_appid")));
		//sendMap.put("product_id",MiscUtil.toStringAndTrim(ctx.getData("product_id")));
		
        String paysignkey = ctx.getString("paysignkey");
        log.info(paysignkey);
		sendMap.put("sign", MiscUtil.sign1(sendMap,paysignkey));

		String xmlData = wxUtil.parseXML(sendMap);

		byte[] bytea = wxUtil.httpSend(xmlData.getBytes(), url);

		String str = MiscUtil.getStrMsg(bytea, "UTF-8");

		Map xmap = XmlUtil.toMap(str, "xml");

		ctx.setDataMap(xmap);

		log.info("支付平台流水号[" + order.getTransSeqNo() + "]微信返回信息如下:" + xmap);
		if (Constants.QRCODE_FAIL.equals(xmap.get("return_code"))) {
			order.setStatus(Constants.TRANS_STATUS_ERROR);
			log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "],微信通信错误码[" + xmap.get("return_code") + "],微信通信错误信息[" + xmap.get("return_msg"));
			throw new PeException((String) xmap.get("return_code"));
		}

		String sign = (String) xmap.get("sign");
		xmap.remove("sign");
		String tSign = MiscUtil.sign1(xmap,paysignkey);

		if (sign.equals(tSign)) {
			log.info("微信统一下单接口支付平台流水号[" + order.getTransSeqNo() + "]验签成功");
			if (Constants.QRCODE_SUCCESS.equals(xmap.get("return_code"))) {
				if (Constants.QRCODE_SUCCESS.equals(xmap.get("result_code"))) {
					ctx.setDataMap(xmap);
				} else {
					log.info("微信二维码支付 - 支付平台流水号[" + order.getTransSeqNo() + "],微信业务响应码[" + xmap.get("result_code") + "],微信错误码[" + xmap.get("err_code") + "],微信错误信息[" + xmap.get("err_code_des"));
					throw new PeException((String) xmap.get("err_code"));
				}
			} else {
				log.info("微信二维码支付 - 支付平台流水号[" + order.getTransSeqNo() + "],微信通信错误码[" + xmap.get("return_code") + "],微信通信错误信息[" + xmap.get("return_msg"));
				throw new PeException((String) xmap.get("return_code"));
			}
		} else {

			log.info("微信二维码支付 - 支付平台流水号[" + order.getTransSeqNo() + "]验签失败");
			throw new PeException("000002");
		}

	}

	public void setWxUtil(WxUtil wxUtil) {
		this.wxUtil = wxUtil;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	
	public void setDuplicateOrderChecker(AbstractAction action) {
		duplicateOrderChecker = action;
	}
}
