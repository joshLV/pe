/*
 * AbstractTwoPhaseAction.java   Created on 2005-10-14 17:11:31
 *
 * Copyright 2004 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.mybank.trans.action;


import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.csii.ibs.action.AbstractIbsAction;
import com.csii.pe.action.Executable;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.CommunicationException;
import com.csii.pe.service.comm.Transport;
import com.csii.pp.order.Order;

/**
 * @author X
 */

public abstract class AbstractTwoPhaseAction extends AbstractIbsAction
implements Executable{

	protected Log log;

	protected MessageSource messageSource;

	public void prepare(Context context) throws PeException {
		
	}

	/**
	 * 
	 */
	public AbstractTwoPhaseAction() {
		super();
		log = LogFactory.getLog(getClass());
	}

	protected Order getOrder(Context ctx) {

		return (Order) ctx.getVariable();

	}
	public Transport getAlipayCoreHostTransport() {
		return (Transport) getService("alipaytransport");
	}
//	
	public Object issueAlipayCoreHostTrs(Context context, Map map) throws CommunicationException {
		Transport transport = getAlipayCoreHostTransport();
	    Map fromHost = (Map) transport.submit(map);
//	    String retCode = (String)fromHost.get(Dict.PPRESPCODE);
//	    String retMsg = (String)fromHost.get(Dict.PPRESPMSG);
	    context.setDataMap(fromHost);
//	    if(!"000000".equals(retCode)) {
//			context.setDataMap(fromHost);
//			RemoteRejectException e = new RemoteRejectException(retCode, new Object[] {retMsg});
//			e.setDefaultMessage(retMsg);
//			e.setResponseData(fromHost);
//	    	throw e;
//	    }
	    return fromHost;
	}
	

}
