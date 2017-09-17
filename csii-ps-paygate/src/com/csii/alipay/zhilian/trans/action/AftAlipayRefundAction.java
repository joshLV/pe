package com.csii.alipay.zhilian.trans.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.ErrorConstants;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;

public class AftAlipayRefundAction extends AbstractAction {

	private OrderManager orderManager;

	public void execute(Context ctx) throws PeException {

		final Order order = (Order) ctx.getVariable();

		Map dataMap = ctx.getDataMap();
		
		Map json = new HashMap();
		json.put("ReturnCode", MiscUtil.toStringAndTrim(ctx.getData("ReturnCode")));
		json.put("ReturnMsg", MiscUtil.toStringAndTrim(ctx.getData("ReturnMsg")));

		if(order != null){
			
	        order.setHostSeqNo(MiscUtil.toStringAndTrim(ctx.getData("trade_no")));

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
		//退款交易发起待查询 （增加超时异常的处理）
		if(order.getStatus()==null || order.getStatus().equals(Constants.TRANS_STATUS_PROCESSING) ||ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR.equals( MiscUtil.toStringAndTrim(ctx.getData("ReturnCode"))) ) {
			insertOtQuery(order);
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
		if (Constants.QRCODE_PROCESSING.equals(order.getReturnCode())||Constants.QRCODE_SUCCESS.equals(order.getReturnCode())
				||Constants.AAAAAAA.equals(order.getReturnCode())
				) {

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
