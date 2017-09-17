package com.csii.weixin.trans.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;

/**
 * 撤销订单接口action
 * */
public class CLODAction extends AbstractAction {

	private WxUtil wxUtil;
	private String url;

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Order order = (Order) ctx.getVariable();
		Map sendMap = new HashMap();
		sendMap.put("appid", order.getAppid()); // 公众帐号ID
		sendMap.put("mch_id", order.getBankId()); // 商户号
		sendMap.put("sub_mch_id", order.getMerWxId()); // 子商户号
		sendMap.put("nonce_str", MiscUtil.getRandom()); // 随机字符串
		sendMap.put("out_trade_no", order.getOrgTransSeqNo()); // 商户订单号
		Map paraMap = new HashMap();
		paraMap.put("bankId", order.getBankId());
		paraMap.put("departmentId", order.getDepartmentId());
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", paraMap);
		String paysignkey = (String) MerBankParameterMap.get("paysignkey");
		String keystorePath = (String) MerBankParameterMap.get("keystorePath");
		String keystorePassword = (String) MerBankParameterMap.get("keystorePassword");
		// 获取签名字符串
		String signStr = MiscUtil.sign(sendMap,paysignkey);

		log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "]签名数据为：" + signStr);
		sendMap.put("sign", signStr);

		String xmlData = wxUtil.parseXML(sendMap);

		// 请求需要双向证书
		byte[] bytea = wxUtil.httpSSLSend(xmlData.getBytes(), url,keystorePath,keystorePassword);

		String str = "";
		try {
			str = new String(bytea, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 返回值map
		Map resMap = XmlUtil.toMap(str, "xml");

		ctx.setDataMap(resMap);

		// 判断交易状态码
		if (Constants.QRCODE_FAIL.equals(resMap.get("return_code"))) {
			order.setStatus(Constants.TRANS_STATUS_ERROR);
			log.info("撤销订单交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理失败 - 错误码[" + resMap.get("return_code") + "]错误信息[" + resMap.get("return_msg") + "]");
			throw new PeException((String) resMap.get("return_code"));
		}

		String sign = (String) resMap.get("sign");

		resMap.remove("sign");

		String tSign = MiscUtil.sign(resMap, paysignkey);

		if (sign.equals(tSign)) {
			String returnCode = (String) resMap.get("return_code");
			String returnMsg = (String) resMap.get("return_msg");

			// ctx.setData("returnCode", returnCode);
			// ctx.setData("returnMsg", returnMsg);

			if (Constants.QRCODE_SUCCESS.equals(returnCode)) {

				String resultCode = (String) resMap.get("result_code");

				if (Constants.QRCODE_SUCCESS.equals(resultCode)) {
					order.setReturnCode(Constants.QRCODE_SUCCESS);

					log.info("撤销订单交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理成功.");
				} else {
					String errCode = (String) resMap.get("err_code");
					String errCodeDes = (String) resMap.get("err_code_des");

					order.setReturnCode(errCode);
					order.setReturnMsg(errCodeDes);

					// ctx.setData("errCode", errCode);
					// ctx.setData("errCodeDes", errCodeDes);

					log.info("撤销订单交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理失败 - 错误码[" + errCode + "]错误信息[" + errCodeDes + "]");
				}

				ctx.setDataMap(resMap);

				ctx.setVariable(order);
			} else {
				log.info("撤销订单交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理失败 - 错误码[" + returnCode + "]错误信息[" + returnMsg + "]");
				throw new PeException("000001");
			}
		} else {
			log.info("撤销订单交易 - 支付平台流水号[" + order.getTransSeqNo() + "]验签失败");
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
