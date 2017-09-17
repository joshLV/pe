package com.csii.weixin.trans.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;

/**
 * 申请退款接口action
 * */
public class WP03Action extends AbstractAction {

	private static Logger logger = LoggerFactory.getLogger(WP03Action.class);

	private WxUtil wxUtil;

	private String url;

	@Override
	public void execute(Context ctx) throws PeException {
		Order order = (Order) ctx.getVariable();
		Map paraMap = new HashMap();
		paraMap.put("bankId", order.getBankId());
		paraMap.put("departmentId", order.getDepartmentId());
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", paraMap);
		Map reqMap = new HashMap();
		reqMap.put("appid", order.getAppid()); // 公众帐号ID
		reqMap.put("mch_id", MerBankParameterMap.get("merId")); // 商户号
		reqMap.put("sub_appid", ctx.getData("sub_appid")); // 子商户公众帐号
		reqMap.put("sub_mch_id", order.getMerWxId()); // 子商户号
		reqMap.put("device_info", ctx.getData("device_info")); // 设备号
		reqMap.put("nonce_str", MiscUtil.getRandom()); // 随机字符串
		reqMap.put("out_trade_no", order.getOrgOrder().getTransSeqNo()); // 商户支付原订单号
		reqMap.put("out_refund_no", order.getTransSeqNo()); // 商户退款单号
		reqMap.put("total_fee", ctx.getData("total_fee")); // 订单金额
		reqMap.put("refund_fee", ctx.getData("refund_fee")); // 申请退款金额
		reqMap.put("refund_fee_type", Constants.Default_Currency); // 货币种类
		reqMap.put("op_user_id", order.getBankId()); // 操作员 默认为商户号
		reqMap.put("refund_account", ctx.getData("refund_account")); // 退还资金来源

		String paysignkey = (String) MerBankParameterMap.get("paysignkey");
		String keystorePath = (String) MerBankParameterMap.get("keystorePath");
		String keystorePassword = (String) MerBankParameterMap.get("keystorePassword");
		// 获取签名字符串
		String signStr = MiscUtil.sign(reqMap,paysignkey);

		reqMap.put("sign", signStr);

		String xmlData = wxUtil.parseXML(reqMap);

		byte[] bytea = wxUtil.httpSSLSend(xmlData.getBytes(), url ,keystorePath,keystorePassword);

		String str = getStrMsg(bytea, "UTF-8");

		// 返回值map
		Map resMap = XmlUtil.toMap(str, "xml");

		ctx.setDataMap(resMap);

		// 判断交易状态码
		if (Constants.QRCODE_FAIL.equals(resMap.get("return_code"))) {
			order.setStatus(Constants.TRANS_STATUS_ERROR);
			log.info("申请退款交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理失败 - 错误码[" + resMap.get("return_code") + "]错误信息[" + resMap.get("return_msg") + "]");
			throw new PeException((String) resMap.get("return_code"));
		}

		String sign = (String) resMap.get("sign");

		resMap.remove("sign");

		String tSign = MiscUtil.sign(resMap,paysignkey);

		if (sign.equals(tSign)) {
			String returnCode = (String) resMap.get("return_code");
			String returnMsg = (String) resMap.get("return_msg");

			ctx.setData("returnCode", returnCode);
			ctx.setData("returnMsg", returnMsg);

			if (Constants.QRCODE_SUCCESS.equals(returnCode)) {

				String resultCode = (String) resMap.get("result_code");

				if (Constants.QRCODE_SUCCESS.equals(resultCode)) {
					order.setReturnCode(Constants.QRCODE_SUCCESS);

					log.info("申请退款交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理成功.");
				} else {
					String errCode = (String) resMap.get("err_code");
					String errCodeDes = (String) resMap.get("err_code_des");

					log.info("申请退款交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理失败 - 错误码[" + errCode + "]错误信息[" + errCodeDes + "]");
					throw new PeException((String) resMap.get("err_code"));
				}

				ctx.setDataMap(resMap);

				ctx.setVariable(order);
			} else {
				log.info("申请退款交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理失败 - 错误码[" + returnCode + "]错误信息[" + returnMsg + "]");
				throw new PeException("000001");
			}
		} else {
			log.info("申请退款交易 - 支付平台流水号[" + order.getTransSeqNo() + "]验签失败");
			throw new PeException("000002");
		}
	}

	private String getStrMsg(byte[] bytes, String encoding) {
		String str = "";
		try {
			if ((bytes != null) && (bytes.length > 0)) {
				str = new String(bytes, encoding);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		return str;
	}

	public void setWxUtil(WxUtil wxUtil) {
		this.wxUtil = wxUtil;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
