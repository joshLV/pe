package com.csii.weixin.common.action;

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

public class AftTenpayAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(AftTenpayAction.class);

	private OrderManager orderManager;

	@Override
	public void execute(Context ctx) throws PeException {

		Order order = (Order) ctx.getVariable();
		if(order != null){
			order.setReturnCode(MiscUtil.toStringAndTrim(ctx.getData("ReturnCode")));
			order.setReturnMsg(MiscUtil.toStringAndTrim(ctx.getData("ReturnMsg")));
			order.setAppid(MiscUtil.toStringAndTrim(ctx.getString("appid")));

			if (Constants.AAAAAAA.equals(order.getReturnCode())) {
				order.setStatus(Constants.TRANS_STATUS_OK);
				//统一下单
				if ("TPCP".equals(order.getTransId())||"WP02".equals(order.getTransId())) {
					order.setStatus(Constants.TRANS_STATUS_UNPAY);
					insertOtQuery(order);
				}
			} else if ("USERPAYING".equals(order.getReturnCode())) {
				order.setStatus(Constants.TRANS_STATUS_UNPAY);
				insertOtQuery(order);
			} else {
				order.setStatus(Constants.TRANS_STATUS_ERROR);
			}

			if ((Constants.QRCODE_SUCCESS.equals(ctx.getData("result_code")) && "MPCP".equals(order.getTransId()))
					||(Constants.QRCODE_SUCCESS.equals(ctx.getData("result_code")) && "WP01".equals(order.getTransId()))) {
				String amountStr = MiscUtil.DivideHundred(ctx.getData("cash_fee"));
				BigDecimal amount = new BigDecimal(amountStr).setScale(2, BigDecimal.ROUND_HALF_UP);
				order.setAmount(amount);
				order.setOpenid(MiscUtil.toStringAndTrim(ctx.getData("openid")));
				order.setBankType(MiscUtil.toStringAndTrim(ctx.getData("bank_type")));
				order.setHostSeqNo(MiscUtil.toStringAndTrim(ctx.getData("transaction_id")));
				String hostDateTime = MiscUtil.toStringAndTrim(ctx.getData("time_end"));
				order.setHostDateTime(new Timestamp(CsiiUtils.bocmDatetimeToCal(hostDateTime).getTimeInMillis()));
				// 商户日期
				String hostDate = MiscUtil.toStringAndTrim(ctx.getData("time_end")).substring(0, 8);
				order.setHostDate(MiscUtil.calStringToDate(hostDate));
			}

			orderManager.updateOrder(order);
			Map json = new HashMap();

			//统一下单
			if ("TPCP".equals(order.getTransId())||"WP02".equals(order.getTransId())) {
				//公众号支付
				if ("JSAPI".equals(MiscUtil.toStringAndTrim(ctx.getData("trade_type")))) {
					String rand = MiscUtil.getRandom();
					String key = ctx.getString("paysignkey");
					Map jsApiMap = new HashMap();
					jsApiMap.put("appId", MiscUtil.toStringAndTrim(ctx.getData("appid")));
					jsApiMap.put("timeStamp", System.currentTimeMillis() + "");
					jsApiMap.put("nonceStr", rand);
					jsApiMap.put("package", "prepay_id=" + MiscUtil.toStringAndTrim(ctx.getData("prepay_id")));
					jsApiMap.put("signType", "MD5");
					jsApiMap.put("paySign", MiscUtil.sign(jsApiMap, key));

					json.putAll(jsApiMap);
					json.put("nonce_str", rand);
				} else {//扫码支付
					json.put("prepay_id", MiscUtil.toStringAndTrim(ctx.getData("prepay_id")));
					json.put("code_url", MiscUtil.toStringAndTrim(ctx.getData("code_url")));
				}
			} else if ("MPCP".equals(order.getTransId())||"WP01".equals(order.getTransId())) {
				json.put("bank_type", MiscUtil.toStringAndTrim(ctx.getData("bank_type")));
			}

			json.put("ReturnCode", MiscUtil.toStringAndTrim(ctx.getData("ReturnCode")));
			json.put("ReturnMsg", MiscUtil.toStringAndTrim(ctx.getData("ReturnMsg")));

			json.put("total_fee", ctx.getBigDecimal("total_fee"));
			json.put("out_trade_no", MiscUtil.toStringAndTrim(ctx.getData("out_trade_no")));
			json.put("cash_fee", MiscUtil.MultiplyHundred(order.getAmount()));
			json.put("trans_datetime", MiscUtil.timeToString1(order.getTransDateTime()));// 交易时间
			json.put("DepartmentId", order.getDepartmentId());
			json.put("trade_type", order.getPayType());

			//返回结果
			ctx.setData("json", json);
			
			//交易不为成功，则回滚限额
			//刷卡支付不为异步，成功结果直接返回，在此做限额回滚。统一下单为异步，在查询时做限额回滚。
//			Map para = new HashMap();
//			para.put("MerchantId", order.getSubMerchantId());
//			Map ctrl = (Map) this.getSqlMap().queryForObject("pp.core.queryMerchantTransCtrlDynamic",para);
//			BigDecimal dayamt=  ((BigDecimal) ctrl.get("PerDayAmt")).subtract(order.getAmount3());
//			para.put("dayAmt", dayamt);
//			this.getSqlMap().update("pp.core.updateMerBackLimitCtrl",para);
			//扣减限额
			if ("MPCP".equals(order.getTransId())||"WP01".equals(order.getTransId())) {
				if ((Constants.TRANS_STATUS_OK).equals(order.getStatus())) {
					Map para = new HashMap();
					para.put("MerchantId", order.getSubMerchantId());
					para.put("Status", "0");
					//商户交易限额控制
					Map ctrl = (Map) this.getSqlMap().queryForObject("pp.core.queryMerchantTransCtrlDynamic",para);
					if (ctrl != null) {
						ctrl.put("PerDayAmt",((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
						this.getSqlMap().update("pp.core.updateMerLimitCtrl",ctrl);
					}
				}
			}
			//交易结束。
		}
		
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	private void insertOtQuery(Order order) {
		this.getSqlMap().insert("pp.core.insertOtQuery", order);
	}

}
