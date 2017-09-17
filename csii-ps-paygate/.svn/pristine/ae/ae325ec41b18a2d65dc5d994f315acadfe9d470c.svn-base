package com.csii.weixin.common.action;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

public class AftMybankPayAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(AftMybankPayAction.class);

	private OrderManager orderManager;

	@Override
	public void execute(Context ctx) throws PeException {

		Order order = (Order) ctx.getVariable();
		if(order != null){
			order.setReturnCode(MiscUtil.toStringAndTrim(ctx.getData("ReturnCode")));
			order.setReturnMsg(MiscUtil.toStringAndTrim(ctx.getData("ReturnMsg")));

			if (Constants.AAAAAAA.equals(order.getReturnCode())) {
				order.setStatus(Constants.TRANS_STATUS_OK);
				//统一下单
				if ("ALZS".equals(order.getTransId())) {
					order.setStatus(Constants.TRANS_STATUS_UNPAY);
//					insertOtQuery(order);
				}
			}else if("U".equals(ctx.getData("ResultStatus"))){
				order.setStatus(Constants.TRANS_STATUS_UNKNOW);
			}
			else {
				order.setStatus(Constants.TRANS_STATUS_ERROR);
			}

			if ("ALBS".equals(ctx.getTransactionId())&&Constants.TRANS_STATUS_OK.equals(order.getStatus())) {
				order.setHostSeqNo(MiscUtil.toStringAndTrim(ctx.getData("OrderNo")));
				order.setRemark1(MiscUtil.toStringAndTrim(ctx.getData("PayChannelOrderNo")));
				order.setOpenid(MiscUtil.toStringAndTrim(ctx.getData("OpenId")));
				order.setBankType(MiscUtil.toStringAndTrim(ctx.getData("BankType")));
				String hostDateTime = MiscUtil.toStringAndTrim(ctx.getData("GmtPayment"));
				order.setHostDateTime(Timestamp.valueOf(hostDateTime));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				order.setHostDate(MiscUtil.calStringToDate(hostDateTime.substring(0,10)));
			}

			orderManager.updateOrder(order);
			Map json = new HashMap();

			//统一下单
			if ("ALZS".equals(order.getTransId())) {
					json.put("TRADE_NO", MiscUtil.toStringAndTrim(ctx.getData("PrePayId")));
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
			
			//扣减限额
			if ("ALBS".equals(order.getTransId())) {
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
