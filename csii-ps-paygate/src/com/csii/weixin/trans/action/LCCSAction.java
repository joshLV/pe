package com.csii.weixin.trans.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;

public class LCCSAction extends AbstractAction {

	private WxUtil wxUtil;
	private String url;

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Merchant merchant = (Merchant) this.getSqlMap().queryForObject("pp.core.qryParentMerId", ctx.getDataMap());
		Map paraMap = new HashMap();
		paraMap.put("bankId", merchant.getMerBankId());
		paraMap.put("departmentId", "weixin");
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", paraMap);
		String paysignkey = (String) MerBankParameterMap.get("paysignkey");
		Map sendMap = new HashMap();
		sendMap.put("appid", MerBankParameterMap.get("appId"));
		sendMap.put("mch_id", merchant.getMerBankId());
		sendMap.put("sub_mch_id", merchant.getMerWxId());
		sendMap.put("nonce_str", MiscUtil.getRandom());
		sendMap.put("long_url", ctx.getData("long_url"));

		String str1 = MiscUtil.sign(sendMap,paysignkey);

		log.info("微信转换短链接接口支付平台签名数据为：" + str1);
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
		log.info("微信转换短链接接口支付平台流水号微信返回信息如下:" + xmap);
		if (Constants.QRCODE_FAIL.equals(xmap.get("return_code"))) {
			throw new PeException("000001");
		}
		String Sign = (String) xmap.get("sign");
		xmap.remove("sign");
		String tSign = MiscUtil.sign(xmap,paysignkey);

		if (Sign.equals(tSign)) {
			log.info("微信转换短链接接口支付平台验签成功");
			// 响应成功并且处理成功
			if (Constants.QRCODE_SUCCESS.equals(xmap.get("return_code")) && Constants.QRCODE_SUCCESS.equals(xmap.get("result_code"))) {

			} else {
				log.info("微信转换短链接接口支付平台,微信错误码[" + xmap.get("err_code") + "],微信错误信息[" + xmap.get("err_code_des"));
				throw new PeException("000001");// 交易失败
			}
		} else {

			log.info("微信转换短链接接口支付平台验签失败");
			throw new PeException("000002");
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

}
