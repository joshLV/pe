/*
 * AbstractChecker.java Created on 2007-10-15 6:22:29
 * 
 * Copyright 2004 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.weixin.common.action;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pp.order.Order;

/**
 * 
 * @author josh
 * 
 *
 */
public abstract class AbstractChecker extends AbstractAction {

	/**
	 * 
	 */
	public AbstractChecker() {
		super();
	}
	
	
	protected Order getOrder (Context ctx){
		
		return (Order)ctx.getVariable();
	}

	

}
