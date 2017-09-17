package com.csii.weixin.trans.action;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.csii.alipay.zhilian.trans.action.APRFQueryAction;
import com.csii.alipay.zhilian.trans.action.APQOAction;
import com.csii.ibs.action.AbstractAction;
import com.csii.mybank.trans.action.APCXAction;
import com.csii.pe.common.util.CsiiUtils;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;
import com.csii.weixin.notify.action.MerchantSendAction;

public class TPCXAction extends AbstractAction {

	private WxUtil wxUtil;
	private String url;
	private APCXAction apcxAction;
	private APQOAction apqoAction;

	private APRFQueryAction aprfQrAction;

	@Override
	@SuppressWarnings("static-access")
	public void execute(Context ctx) throws PeException {
		Order order = (Order) ctx.getVariable();
		if(Constants.WS_MYBANK_DEPARTMENTID.equals(order.getDepartmentId())) {
			apcxAction.execute(ctx);
			
		//支付宝交易	
		}else if(Constants.DEPARTMENTID_ALIPAY.equals(order.getDepartmentId())) {
			
			if(order.getTransType().equals("00")) {
				log.info("支付宝支付交易查询开始");
				apqoAction.execute(ctx);
			}else {
				log.info("支付宝退款查询开始");
				aprfQrAction.execute(ctx);
			}
			
		}else {
        Map MerBankParameterMap = (Map) ctx.getData("MerBankParameterMap");
		String status = order.getStatus();
		String paysignkey = (String) MerBankParameterMap.get("paysignkey");
		if (Constants.TRANS_STATUS_PAYING.equals(status) || Constants.TRANS_STATUS_UNPAY.equals(status)) {

			ctx.setData("QryWxTransStatus", true);

			Map sendMap = new HashMap();
			sendMap.put("appid", order.getAppid());
			// sendMap.put("sub_appid", appid);
			sendMap.put("mch_id", MerBankParameterMap.get("merId"));
			sendMap.put("sub_mch_id", order.getMerWxId());
			sendMap.put("out_trade_no", ctx.getData("out_trade_no"));// 商户订单号
			sendMap.put("nonce_str", MiscUtil.getRandom());
			String str1 = MiscUtil.sign(sendMap,paysignkey);

			sendMap.put("sign", str1);

			String xmlData = wxUtil.parseXML(sendMap);

			// 发起查询的条件
			// 1、当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知；
			// 2、调用支付接口后，返回系统错误或未知交易状态情况；
			// 3、调用被扫支付API，返回USERPAYING的状态；
			// 4、调用关单或撤销接口API之前，需确认支付状态；

			byte[] bytes = wxUtil.httpSend(xmlData.getBytes(), url);

			String str = MiscUtil.getStrMsg(bytes, "UTF-8");

			Map wxResMap = XmlUtil.toMap(str, "xml");

			ctx.setDataMap(wxResMap);

			log.info("微信查询订单 - 支付平台流水号[" + order.getTransSeqNo() + "],微信返回信息如下:" + wxResMap);

			String returnCode = (String) wxResMap.get("return_code");
			String returnMsg = (String) wxResMap.get("return_msg");

			if (Constants.QRCODE_SUCCESS.equals(returnCode)) {
				String appid = (String) wxResMap.get("appid");
				String mch_id = (String) wxResMap.get("mch_id");
				String sub_appid = (String) wxResMap.get("sub_appid");
				String sub_mch_id = (String) wxResMap.get("sub_mch_id");
				String nonce_str = (String) wxResMap.get("nonce_str");
				String sign = (String) wxResMap.get("sign");
				String resultCode = (String) wxResMap.get("result_code");
				String errorCode = (String) wxResMap.get("err_code");
				String errorMsg = (String) wxResMap.get("err_code_des");

				wxResMap.remove("sign");

				String tSign = MiscUtil.sign(wxResMap,paysignkey);

				if (sign.equals(tSign)) {
					if (Constants.QRCODE_SUCCESS.equals(resultCode)) {
						String device_info = (String) wxResMap.get("device_info");
						String openid = (String) wxResMap.get("openid");
						String is_subscribe = (String) wxResMap.get("is_subscribe");
						String sub_openid = (String) wxResMap.get("sub_openid");
						String sub_is_subscribe = (String) wxResMap.get("sub_is_subscribe");
						String trade_type = (String) wxResMap.get("trade_type");
						String trade_state = (String) wxResMap.get("trade_state");
						String bank_type = (String) wxResMap.get("bank_type");
						String detail = (String) wxResMap.get("detail");
						String total_fee = (String) wxResMap.get("total_fee");
						String fee_type = (String) wxResMap.get("fee_type");
						String cash_fee = (String) wxResMap.get("cash_fee");
						String cash_fee_type = (String) wxResMap.get("cash_fee_type");
						String coupon_fee = (String) wxResMap.get("coupon_fee");

						String coupon_count = (String) wxResMap.get("coupon_count");
						if (!MiscUtil.isNullOrEmpty(coupon_count)) {
							for (int i = 1; i <= Integer.valueOf(coupon_count); i++) {
								String coupon_batch_id_n = (String) wxResMap.get("coupon_batch_id_" + i);
								String coupon_id_n = (String) wxResMap.get("coupon_id_" + i);
								String coupon_fee_n = (String) wxResMap.get("coupon_fee_" + i);
							}
						}

						String transaction_id = (String) wxResMap.get("transaction_id");
						String out_trade_no = (String) wxResMap.get("out_trade_no");
						String attach = (String) wxResMap.get("attach");
						String time_end = (String) wxResMap.get("time_end");
						String trade_state_desc = (String) wxResMap.get("trade_state_desc");
						
						order.setStatus(wxUtil.getTransStatus(MiscUtil.toStringAndTrim(ctx.getData("trade_state"))));
						
						if (Constants.QRCODE_SUCCESS.equals(ctx.getData("trade_state"))) {
							order.setReturnCode(Constants.AAAAAAA);
							order.setReturnMsg("交易成功");
							String amountStr = MiscUtil.DivideHundred(ctx.getData("cash_fee"));
							BigDecimal amount = new BigDecimal(amountStr).setScale(2, BigDecimal.ROUND_HALF_UP);
							order.setAmount(amount);
							order.setOpenid(MiscUtil.toStringAndTrim(ctx.getData("openid")));
							order.setBankType(MiscUtil.toStringAndTrim(ctx.getData("bank_type")));
							order.setHostSeqNo(MiscUtil.toStringAndTrim(ctx.getData("transaction_id")));
							String hostDateTime = MiscUtil.toStringAndTrim(ctx.getData("time_end"));
							order.setHostDateTime(new Timestamp(CsiiUtils.bocmDatetimeToCal(hostDateTime).getTimeInMillis()));

							String hostDate = MiscUtil.toStringAndTrim(ctx.getData("time_end")).substring(0, 8);
							order.setHostDate(MiscUtil.calStringToDate(hostDate));
							
							//交易成功，扣减限额，发送商户通知
							if ("00".equals(order.getTransType())) {
								Map para1 = new HashMap();
								para1.put("MerchantId", order.getSubMerchantId());
								para1.put("Status", "0");
								//商户交易限额控制
								Map ctrl = (Map) this.getSqlMap().queryForObject("pp.core.queryMerchantTransCtrlDynamic",para1);
								log.info("order======"+order.getAmount3());
								if (ctrl != null) {
									log.debug("aaaaa"+order.getAmount3());
									log.info("kkkkk"+order.getAmount3());
									log.debug("bbbbb"+ctrl.get("PerDayAmt"));
									log.debug("ccccc"+((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
									ctrl.put("PerDayAmt",((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
									this.getSqlMap().update("pp.core.updateMerLimitCtrl",ctrl);
								}
							}
						}
					} else {
						ctx.setData("ReturnCode", errorCode);
						ctx.setData("ReturnMsg", errorMsg);
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
			log.info("平台流水号[" + order.getTransSeqNo() + "]订单状态为[" + status + "]，故不发送请求至微信查询订单状态");
		}
	}

	}

	public void setWxUtil(WxUtil wxUtil) {
		this.wxUtil = wxUtil;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setApcxAction(APCXAction apcxAction) {
		this.apcxAction = apcxAction;
	}

	public APQOAction getApqoAction() {
		return apqoAction;
	}

	public void setApqoAction(APQOAction apqoAction) {
		this.apqoAction = apqoAction;
	}

	public APRFQueryAction getAprfQrAction() {
		return aprfQrAction;
	}

	public void setAprfQrAction(APRFQueryAction aprfQrAction) {
		this.aprfQrAction = aprfQrAction;
	}
	
	
	
	
}
