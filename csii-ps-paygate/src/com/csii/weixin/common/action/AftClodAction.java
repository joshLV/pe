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

public class AftClodAction extends AbstractAction{
	
	private OrderManager orderManager;

	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		final Order order = (Order) ctx.getVariable();

		Map dataMap = ctx.getDataMap();

		if(order != null){
			// 表明退款交易微信已受理成功，将退款订单状态置为处理中
			if (Constants.QRCODE_SUCCESS.equals(order.getReturnCode())) {

				order.setReturnCode(Constants.AAAAAAA);

				order.setReturnMsg("撤销成功");

				String transactionId = (String) dataMap.get("transaction_id");// 微信订单号
				String outTradeNo = (String) dataMap.get("out_trade_no");// 商户订单号

				String refundId = (String) dataMap.get("refund_id");// 微信退款单号
				String outRefundNo = (String) dataMap.get("out_refund_no");// 商户退款单号

				order.setHostSeqNo(refundId);

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
		}
		
		Map json = new HashMap();
		json.put("ReturnCode", MiscUtil.toStringAndTrim(ctx.getData("ReturnCode")));
		json.put("ReturnMsg", MiscUtil.toStringAndTrim(ctx.getData("ReturnMsg")));
		ctx.setData("json", json);
	}
	
	public void updateOriginal(Order order) {
		// 原支付订单
		final Order originalOrder = order.getOrgOrder();

		// 退货交易微信受理成功
		if (order.getReturnCode().equals(Constants.AAAAAAA)) {

			originalOrder.setStatus(Constants.TRANS_STATUS_CANCELED);

			orderManager.updateOrderInOtAndHt(originalOrder);
		}
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
	
}
