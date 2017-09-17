/*
 * DuplicateOrderChecker.java Created on 2007-10-15 7:38:31
 * 
 * Copyright 2004 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.weixin.common.action;

import java.util.Iterator;
import java.util.List;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;

/**
 * 
 * 重复交易检查
 * 
 * 
 * @author josh
 * 
 *
 */
public class DuplicateOrderChecker extends AbstractChecker {

	private OrderManager orderManager;

	@SuppressWarnings("rawtypes")
	public void execute(Context ctx) throws PeException {

		//查交易日志表（商户号，订单号，商户日期）

		//如果没有相同订单的交易，正常

		//如果有相同订单的交易
		//1、如果交易成功  重复
		//2、如果交易进行中，重复
		//3、其他，正常

		//支付交易

		//退货交易

		Order order = this.getOrder(ctx);

		log.info(commInfo(order).append("******重复订单检查开始******").toString());

		List duplicateList = orderManager.getDuplicateOrder(order);

		boolean isDuplicate = false;

		//如果存在一笔商户的交易日期，商户号，商户订单号
		if (duplicateList != null && !duplicateList.isEmpty()) {
//			for (Iterator it = duplicateList.iterator(); it.hasNext();) {
//				Order tmpOrder = (Order) it.next();
//				if (!Constants.TRANS_STATUS_ERROR.equals(tmpOrder.getStatus())|| Constants.TRANS_PROC_STATUS_PENDING.equals(tmpOrder.getProcStatus())) {
//					isDuplicate = true;
//					break;
//				}
//
//			}
			isDuplicate = true;
		}

		if (isDuplicate == true) {
			log.error(commInfo(order).append("******该交易为重复交易******").toString());
			throw new PeException("E00041");
		}

		log.info(commInfo(order).append("******交易检查重复成功******").toString());

	}

	private StringBuffer commInfo(Order order) {
		return new StringBuffer()
			.append("网关流水[")
			.append(order.getTransSeqNo())
			.append("]")
			.append("商户号[")
			.append(order.getMerchantId())
			.append("]")
			.append("订单号[")
			.append(order.getMerSeqNo())
			.append("]");
	}

	/**
	 * @param manager
	 */
	public void setOrderManager(OrderManager manager) {
		orderManager = manager;
	}

}
