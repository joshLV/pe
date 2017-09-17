package com.csii.weixin.trans.action;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.common.util.CsiiUtils;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.weixin.notify.action.MerchantSendAction;

/**
 * 支付结果通知接口action
 * */
public class PRNTAction extends AbstractAction {

	private static Logger logger = LoggerFactory.getLogger(PRNTAction.class);
	private MerchantSendAction merchantSendAction;
	private OrderManager orderManager;

	@Override
	public void execute(Context ctx) throws PeException {

		Map wxNotifyMap = (Map) ctx.getData("wxNotifyMap");

		Map map = new HashMap();
		String outTradeNo = (String) wxNotifyMap.get("out_trade_no");
		map.put("TransSeqNo", outTradeNo);
		Order order = (Order) getSqlMap().queryForObject("pp.core.queryOtByTransSeqNo", map);
		if (order == null) {
			order = (Order) getSqlMap().queryForObject("pp.core.queryHtByTransSeqNo", map);
		}
		if (order == null) {
			logger.error("微信支付通知 - 商户订单号[{}]未找到", outTradeNo);
			ctx.setData("ReturnCode", "FAIL");
			ctx.setData("ReturnMsg", "商户订单号未找到");
			throw new PeException("E00012", new Object[] { outTradeNo });
		}

		String orderStatus = order.getStatus();
		if (!orderStatus.equals("07") && !orderStatus.equals("08")) {
			logger.error("微信支付通知 - 商户订单号[{}]状态为[{}] - 不需要做订单状态更新", outTradeNo, orderStatus);
			ctx.setData("ReturnCode", "SUCCESS");
			ctx.setData("ReturnMsg", "OK");
			return;
		}

		if (Constants.QRCODE_FAIL.equals(wxNotifyMap.get("return_code"))) {
			ctx.setData("ReturnCode", "FAIL");
		} else {
			String sign = (String) wxNotifyMap.get("sign");
			wxNotifyMap.remove("sign");
			Map paraMap = new HashMap();
			paraMap.put("bankId", order.getBankId());
			paraMap.put("departmentId", order.getDepartmentId());
			Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", paraMap);
			String paysignkey= (String) MerBankParameterMap.get("paysignkey");
			String tSign = MiscUtil.sign(wxNotifyMap,paysignkey);
			boolean isnotify = false;
			if (sign.equals(tSign)) {
				log.info("微信支付结果通知接口支付平台流水号[" + order.getTransSeqNo() + "]验签成功");
				if (Constants.QRCODE_SUCCESS.equals(wxNotifyMap.get("return_code"))) {
					if (Constants.QRCODE_SUCCESS.equals(wxNotifyMap.get("result_code"))) {
						order.setReturnCode(Constants.AAAAAAA);
						order.setReturnMsg("交易成功");
						order.setStatus(Constants.TRANS_STATUS_OK);

						String amountStr = MiscUtil.DivideHundred(wxNotifyMap.get("cash_fee"));
						BigDecimal amount = new BigDecimal(amountStr).setScale(2, BigDecimal.ROUND_HALF_UP);
						order.setAmount(amount);
						order.setOpenid(MiscUtil.toStringAndTrim(wxNotifyMap.get("openid")));

						order.setBankType(MiscUtil.toStringAndTrim(wxNotifyMap.get("bank_type")));
						order.setHostSeqNo(MiscUtil.toStringAndTrim(wxNotifyMap.get("transaction_id")));

						String hostDateTime = MiscUtil.toStringAndTrim(wxNotifyMap.get("time_end"));
						order.setHostDateTime(new Timestamp(CsiiUtils.bocmDatetimeToCal(hostDateTime).getTimeInMillis()));
						// 商户日期
						String hostDate = MiscUtil.toStringAndTrim(wxNotifyMap.get("time_end")).substring(0, 8);
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
							isnotify= true;
						}
						
					} else {
						log.info("微信支付结果通知接口支付平台流水号[" + order.getTransSeqNo() + "],微信业务响应码[" + wxNotifyMap.get("result_code") + "],微信错误码[" + wxNotifyMap.get("err_code") + "],微信错误信息["
								+ wxNotifyMap.get("err_code_des"));
						order.setReturnCode(MiscUtil.toStringAndTrim(wxNotifyMap.get("err_code")));
						order.setReturnMsg(MiscUtil.toStringAndTrim(wxNotifyMap.get("err_code_des")));
						order.setStatus(Constants.TRANS_STATUS_ERROR);
					}
					orderManager.updateOrder(order);
					if(isnotify){
						//发送商户通知
						ctx.setVariable(order);
						merchantSendAction.execute(ctx);
					}
					ctx.setData("ReturnCode", "SUCCESS");
					ctx.setData("ReturnMsg", "OK");

				} else {
					log.info("微信支付结果通知接口支付平台流水号[" + order.getTransSeqNo() + "],微信通信错误码[" + wxNotifyMap.get("return_code") + "],微信通信错误信息[" + wxNotifyMap.get("return_msg"));
					ctx.setData("ReturnCode", "SUCCESS");
					ctx.setData("ReturnMsg", wxNotifyMap.get("return_msg"));
				}
			} else {
				log.info("微信支付结果通知接口支付平台流水号[" + order.getTransSeqNo() + "]验签失败");
				ctx.setData("ReturnCode", "FAIL");
				ctx.setData("ReturnMsg", "验签失败");
			}
		}

	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
	/**
	 * @param action
	 */
	public void setMerchantSendAction(MerchantSendAction action) {
		merchantSendAction = action;
	}
}
