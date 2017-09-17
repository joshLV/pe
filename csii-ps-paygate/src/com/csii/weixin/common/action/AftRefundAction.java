package com.csii.weixin.common.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;

public class AftRefundAction extends AbstractAction {

	private OrderManager orderManager;

	public void execute(Context ctx) throws PeException {

		final Order order = (Order) ctx.getVariable();

		Map dataMap = ctx.getDataMap();
		
		Map json = new HashMap();
		json.put("ReturnCode", MiscUtil.toStringAndTrim(ctx.getData("ReturnCode")));
		json.put("ReturnMsg", MiscUtil.toStringAndTrim(ctx.getData("ReturnMsg")));

		if(order != null){
			
			// 表明退款交易微信已受理成功，将退款订单状态置为处理中
			if (Constants.QRCODE_SUCCESS.equals(order.getReturnCode())) {

				order.setReturnCode(Constants.QRCODE_PROCESSING);

				order.setReturnMsg("退款处理中");
	            order.setStatus(Constants.TRANS_STATUS_PROCESSING);
	            if("weixin".equals(order.getDepartmentId())){
	            	String transactionId = (String) dataMap.get("transaction_id");// 微信订单号
					String outTradeNo = (String) dataMap.get("out_trade_no");// 商户订单号

					String refundId = (String) dataMap.get("refund_id");// 微信退款单号
					String outRefundNo = (String) dataMap.get("out_refund_no");// 商户退款单号

					order.setHostSeqNo(refundId);
	            }else if("mybank".equals(order.getDepartmentId())){
	            	order.setHostSeqNo(MiscUtil.toStringAndTrim(ctx.getData("refundId")));
	            }else if("alipay".equals(order.getDepartmentId())){
	            	order.setHostSeqNo(MiscUtil.toStringAndTrim(ctx.getData("trade_no")));
	            }
				
//				insertOtQuery(order);
			}else{
				order.setReturnCode(MiscUtil.toStringAndTrim(ctx.getData("ReturnCode")));
				order.setReturnMsg(MiscUtil.toStringAndTrim(ctx.getData("ReturnMsg")));
				order.setStatus(Constants.TRANS_STATUS_ERROR);
			}

			getTransactionTemplate().execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus arg0) {
					// 更新原支付订单信息
					updateOriginal(order);

					orderManager.updateOrderInOtAndHt(order);

					return null;
				}
			});
			
			json.put("total_fee", ctx.getData("total_fee"));
			json.put("refund_fee", ctx.getData("refund_fee"));
			json.put("out_trade_no", order.getOrgTransSeqNo());
			json.put("out_refund_no", order.getTransSeqNo());
			json.put("trans_datetime", MiscUtil.timeToString1(order.getTransDateTime()));// 交易时间
		}
		ctx.setData("json", json);
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public void updateOriginal(Order order) {
		// 原支付订单
		final Order originalOrder = order.getOrgOrder();

		// 退货交易微信受理成功
		if (order.getReturnCode().equals(Constants.QRCODE_PROCESSING)) {

			// 本次交易前原订单已退货金额
			BigDecimal alreadyWithdrawAmount = originalOrder.getAmount1();

			// 原订单的已退货金额等于本次交易前的已退货金额加上本次退货金额
			alreadyWithdrawAmount = alreadyWithdrawAmount.add(order.getAmount3());

			originalOrder.setAmount1(alreadyWithdrawAmount);

			// 原订单的未退货金额为原订单金额-已退货金额
			originalOrder.setAmount2(originalOrder.getAmount3().subtract(alreadyWithdrawAmount));

			// 设置原交易状态为已经退货,原交易日志修改时间为当前日期时间
			if (alreadyWithdrawAmount.compareTo(originalOrder.getAmount3()) == 0) {
				// 全部退货
				originalOrder.setStatus(Constants.TRANS_STATUS_ALL_WITHDRAW);
			} else {
				// 部分退货
				originalOrder.setStatus(Constants.TRANS_STATUS_SUB_WITHDRAW);
			}

			orderManager.updateOrderInOtAndHt(originalOrder);
		}
	}
	
	private void insertOtQuery(Order order){
		this.getSqlMap().insert("pp.core.insertOtQuery", order);
	}
}
