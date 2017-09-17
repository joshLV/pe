/*
 * PaySettlementBooker.java Created on 2007-10-29 3:54:55
 * 
 * Copyright 2004 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pp.clearing.book;

import com.csii.pe.core.PeException;
import com.csii.pp.order.Order;

/**
 * 支付交易商户支付结算登记
 */
public class WithdrawSettlementBooker extends AbstractSettlementBooker {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csii.pp.clearing.book.SettlementBooker#book(java.lang.Object)
	 */
	public void book(Object data) throws PeException {

		Order order = (Order) data;
			//商户支付结算
		merchantWithdrawSettlement(order);
	
	}

}
