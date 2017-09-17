package com.csii.weixin.common.action;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;

public class AddOrderAction extends AbstractAction {

	public void execute(Context context) throws PeException {

		Order order = (Order) context.getVariable();

		log.info(new StringBuffer("网关流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("订单号[").append(order.getTransSeqNo()).append("]")
				.append("******记录交易日志开始!******").toString());
        if(MiscUtil.isNullOrEmpty(context.getData("out_refund_no"))){
        	getSqlMap().insert("pp.core.insertOrder", order);
        }

		log.info(new StringBuffer("网关流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("订单号[").append(order.getTransSeqNo()).append("]")
				.append("******记录交易日志成功!******").toString());
	}
}
