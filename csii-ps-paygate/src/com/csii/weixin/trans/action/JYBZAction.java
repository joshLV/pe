package com.csii.weixin.trans.action;

import java.io.UnsupportedEncodingException;
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

/**
 * 交易保障接口action
 * */
public class JYBZAction extends AbstractAction {

	private WxUtil wxUtil;
	private String url;

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Order order = (Order) ctx.getVariable();
		Merchant merchant = (Merchant) this.getSqlMap().queryForObject("pp.core.qryParentMerId", ctx.getDataMap());
		Map paraMap = new HashMap();
		paraMap.put("bankId", order.getBankId());
		paraMap.put("departmentId", order.getDepartmentId());
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", paraMap);
		String paysignkey = (String) MerBankParameterMap.get("paysignkey");
		Map sendMap = new HashMap();
		sendMap.put("appid", MerBankParameterMap.get("appId")); // 公众帐号ID
		sendMap.put("mch_id", merchant.getMerBankId()); // 商户号
		sendMap.put("sub_mch_id", merchant.getMerWxId()); // 子商户号
		sendMap.put("nonce_str", MiscUtil.getRandom()); // 随机字符串
		sendMap.put("interface_url", ctx.getData("interface_url")); // 接口URL
		sendMap.put("user_ip", ctx.getData("user_ip")); // 访问接口IP
		sendMap.put("trades", ""); // 上报数据包

		// 获取签名字符串
		String signStr = MiscUtil.sign(sendMap,paysignkey);

		log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "]签名数据为：" + signStr);
		sendMap.put("sign", signStr);

		String xmlData = wxUtil.parseXML(sendMap);

		byte[] bytea = wxUtil.httpSend(xmlData.getBytes(), url);

		String str = "";
		try {
			str = new String(bytea, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 返回值map
		Map xmap = XmlUtil.toMap(str, "xml");
		ctx.setDataMap(xmap);
		log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "]微信返回信息如下:" + xmap);
		if (Constants.QRCODE_FAIL.equals(xmap.get("return_code"))) { // 判断交易状态码
			order.setStatus(Constants.TRANS_STATUS_ERROR);
			// orderManager.updateOrder(order);
			throw new PeException("000001");
		}
		String Sign = (String) xmap.get("sign");
		xmap.remove("sign");
		String tSign = MiscUtil.sign(xmap,paysignkey);

		if (Sign.equals(tSign)) {
			log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "]验签成功");
			// 响应成功并且处理成功 && Constants.QRCODE_SUCCESS.equals(xmap.get("result_code"))
			if (Constants.QRCODE_SUCCESS.equals(xmap.get("return_code"))) {
				ctx.setData("return_code", xmap.get("return_code"));
				ctx.setData("return_msg", xmap.get("return_msg"));
				ctx.setData("result_code", xmap.get("result_code"));
				// ctx.setData("code_url", xmap.get("code_url"));
				// ctx.setData("prepay_id", xmap.get("prepay_id"));
				// ctx.setData("ReturnCode", Constants.AAAAAAA);
				// order.setStatus(Constants.TRANS_STATUS_UNPAY);//待支付
				// orderManager.updateOrder(order);
				ctx.setVariable(order);
			} else {
				log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "],微信错误码[" + xmap.get("err_code") + "],微信错误信息[" + xmap.get("err_code_des"));
				throw new PeException("000001");// 交易失败
			}
		} else {

			log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "]验签失败");
			throw new PeException("000002");
		}

	}

	public void setWxUtil(WxUtil wxUtil) {
		this.wxUtil = wxUtil;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
