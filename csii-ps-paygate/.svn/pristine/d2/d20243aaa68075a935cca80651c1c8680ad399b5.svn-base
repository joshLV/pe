package com.csii.alipay.zhilian.trans.action;

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

/***
 * 支付宝 直连 后期处理类
 * @author bin
 *
 */


public class AftAlipayAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(AftAlipayAction.class);

	private OrderManager orderManager;

	@Override
	public void execute(Context ctx) throws PeException {

		Order order = (Order) ctx.getVariable();
		if(order != null){
			
			order.setReturnCode(MiscUtil.toStringAndTrim(ctx.getData("ReturnCode")));
			
			order.setReturnMsg(MiscUtil.toStringAndTrim(ctx.getData("ReturnMsg")));

			if (Constants.AAAAAAA.equals(order.getReturnCode())) {
				order.setStatus(Constants.TRANS_STATUS_OK);
				//支付宝统一下单
				if ("APCP".equals(order.getTransId()) || "AGZH".equals(order.getTransId())) {
				
					order.setStatus(Constants.TRANS_STATUS_UNPAY);
					
					insertOtQuery(order);
				//刷卡交易
				}else if("APMP".equals(order.getTransId())) {
					//支付宝流水号
					order.setHostSeqNo(MiscUtil.toStringAndTrim(ctx.getData("transaction_id")));
					//核心交易日期
					if(!MiscUtil.isNullOrEmpty(ctx.getData("time_end"))) {
						String hostDateTime = MiscUtil.toStringAndTrim(ctx.getData("time_end"));
						
						order.setHostDateTime(new Timestamp(CsiiUtils.bocmDatetimeToCal(hostDateTime).getTimeInMillis()));
						// 商户日期
						String hostDate = MiscUtil.toStringAndTrim(ctx.getData("time_end")).substring(0, 8);
						
						order.setHostDate(MiscUtil.calStringToDate(hostDate));
						//支付宝账号
						order.setOpenid(MiscUtil.toStringAndTrim(ctx.getData("openid")));
						
					}
					
				}
				
				
			} else if ("USERPAYING".equals(order.getReturnCode())) {
				order.setStatus(Constants.TRANS_STATUS_UNPAY);
				insertOtQuery(order);
			} else if("SYSTEMERROR".equals(order.getReturnCode())){
				order.setStatus(Constants.TRANS_STATUS_PAYING);
				insertOtQuery(order);
			}else{
				order.setStatus(Constants.TRANS_STATUS_ERROR);
			}


			orderManager.updateOrder(order);
		
			//返回报文
			Map json = new HashMap();

			String rand = MiscUtil.getRandom();
			//扫码支付
			if ("APCP".equals(order.getTransId()) || "AGZH".equals(order.getTransId())) {
				json.put("prepay_id", MiscUtil.toStringAndTrim(ctx.getData("prepay_id")));
				json.put("code_url", MiscUtil.toStringAndTrim(ctx.getData("code_url")));
				json.put("cash_fee", MiscUtil.MultiplyHundred(order.getAmount()));
			}
			//刷卡支付交易
			else if ("APMP".equals(order.getTransId())) {
				json.put("bank_type", MiscUtil.toStringAndTrim(ctx.getData("bank_type")));
				json.put("cash_fee", MiscUtil.MultiplyHundred(order.getAmount()));
			}else if("APRF".equals(order.getTransId())) {
				json.put("out_refund_no", order.getTransSeqNo());
				json.put("refund_fee", MiscUtil.toStringAndTrim(ctx.getData("refund_fee")));
			}

			json.put("nonce_str", rand);
			json.put("ReturnCode", MiscUtil.toStringAndTrim(ctx.getData("ReturnCode")));
			json.put("ReturnMsg", MiscUtil.toStringAndTrim(ctx.getData("ReturnMsg")));

			json.put("total_fee", ctx.getString("total_fee"));
			json.put("out_trade_no", MiscUtil.toStringAndTrim(ctx.getData("out_trade_no")));
			json.put("cash_fee", MiscUtil.MultiplyHundred(order.getAmount()));
			json.put("trans_datetime", MiscUtil.timeToString1(order.getTransDateTime()));// 交易时间
			json.put("DepartmentId", order.getDepartmentId());
			json.put("trade_type", order.getPayType());

			//返回结果
			ctx.setData("json", json);
			
			//交易不为成功，则回滚限额
			//刷卡支付不为异步，成功结果直接返回，在此做限额回滚。统一下单为异步，在查询时做限额回滚。
			//扣减限额
			if ("APMP".equals(order.getTransId())) {
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
