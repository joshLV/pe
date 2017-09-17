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

public class MPCPAction extends AbstractAction {

	private WxUtil wxUtil;
	private String url;
	private String queryurl;

	@Override
	@SuppressWarnings("static-access")
	public void execute(Context ctx) throws PeException {
		Order order = (Order) ctx.getVariable();
		Merchant merchant = order.getMerchant();
		Map MerBankParameterMap = (Map) ctx.getData("MerBankParameterMap");
		String merId = MiscUtil.toStringAndTrim(MerBankParameterMap.get("merId"));
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
		sendMap.put("auth_code", ctx.getData("auth_code"));
		String paysignkey = ctx.getString("paysignkey");
		String str1 = MiscUtil.sign1(sendMap,paysignkey);

		sendMap.put("sign", str1);

		String xmlData = wxUtil.parseXML(sendMap);
		System.out.println("发送的数据为 》》》 " + xmlData);

		byte[] bytea = wxUtil.httpSend(xmlData.getBytes(), url);

		String str = MiscUtil.getStrMsg(bytea, "UTF-8");

		Map wxResMap = XmlUtil.toMap(str, "xml");

		ctx.setDataMap(wxResMap);

		log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "]微信返回信息如下:" + wxResMap);

		if (Constants.QRCODE_FAIL.equals(wxResMap.get("return_code"))) {
			log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "],微信通信错误码[" + wxResMap.get("return_code") + "],微信通信错误信息[" + wxResMap.get("return_msg") + "]");
			throw new PeException((String) wxResMap.get("return_code"));// 交易失败
		}

		String Sign = (String) wxResMap.get("sign");
		wxResMap.remove("sign");
		String tSign = MiscUtil.sign1(wxResMap,paysignkey);;

		if (Sign.equals(tSign)) {
			if (Constants.QRCODE_SUCCESS.equals(wxResMap.get("return_code"))) {
				if (Constants.QRCODE_SUCCESS.equals(wxResMap.get("result_code"))) {
					ctx.setDataMap(wxResMap);
				} else {
					if ("USERPAYING".equals(wxResMap.get("err_code"))) {
						try {
							Thread.sleep(3000);
							Map resutlt1 = qureyUerPayingOrder(order,paysignkey,merId);
							if (Constants.QRCODE_SUCCESS.equals(resutlt1.get("return_code")) && Constants.QRCODE_SUCCESS.equals(resutlt1.get("result_code"))
									&& Constants.QRCODE_SUCCESS.equals(resutlt1.get("trade_state"))) {
								ctx.setDataMap(resutlt1);
							} else {
								Thread.sleep(5000);
								Map resutlt2 = qureyUerPayingOrder(order,paysignkey,merId);
								if (Constants.QRCODE_SUCCESS.equals(resutlt2.get("return_code")) && Constants.QRCODE_SUCCESS.equals(resutlt2.get("result_code"))
										&& Constants.QRCODE_SUCCESS.equals(resutlt2.get("trade_state"))) {
									ctx.setDataMap(resutlt2);
								} else {
									Thread.sleep(8000);
									Map resutlt3 = qureyUerPayingOrder(order,paysignkey,merId);
									if (Constants.QRCODE_SUCCESS.equals(resutlt3.get("return_code")) && Constants.QRCODE_SUCCESS.equals(resutlt3.get("result_code"))
											&& Constants.QRCODE_SUCCESS.equals(resutlt3.get("trade_state"))) {
										ctx.setDataMap(resutlt3);
									} else {
										throw new PeException((String) wxResMap.get("err_code"));
									}
								}
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "],微信业务响应码[" + wxResMap.get("result_code") + "],微信错误码[" + wxResMap.get("err_code") + "],微信错误信息["
									+ wxResMap.get("err_code_des") + "]");
							throw new PeException((String) wxResMap.get("err_code"));
						}
					} else {
						log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "],微信业务响应码[" + wxResMap.get("result_code") + "],微信错误码[" + wxResMap.get("err_code") + "],微信错误信息["
								+ wxResMap.get("err_code_des") + "]");
						throw new PeException((String) wxResMap.get("err_code"));
					}

				}
			} else {
				log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "],微信通信错误码[" + wxResMap.get("return_code") + "],微信通信错误信息[" + wxResMap.get("return_msg") + "]");
				throw new PeException((String) wxResMap.get("return_code"));
			}
		} else {
			log.info("微信提交刷卡支付接口支付平台流水号[" + order.getTransSeqNo() + "]验签失败");
			throw new PeException("000002");
		}
	}

	private Map qureyUerPayingOrder(Order order,String paysignkey,String merId) throws PeException {
		Map sendMap = new HashMap();
		sendMap.put("appid", order.getAppid());
		sendMap.put("mch_id", merId);
		sendMap.put("sub_mch_id", order.getMerWxId());
		sendMap.put("out_trade_no", order.getTransSeqNo());// 商户订单号
		sendMap.put("nonce_str", MiscUtil.getRandom());
		String str1 = MiscUtil.sign(sendMap,paysignkey);
		sendMap.put("sign", str1);
		String xmlData = wxUtil.parseXML(sendMap);
		byte[] bytes = wxUtil.httpSend(xmlData.getBytes(), queryurl);
		String str = MiscUtil.getStrMsg(bytes, "UTF-8");
		Map wxResMap = XmlUtil.toMap(str, "xml");
		return wxResMap;

	}

	public void setWxUtil(WxUtil wxUtil) {
		this.wxUtil = wxUtil;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setQueryurl(String queryurl) {
		this.queryurl = queryurl;
	}

}
