/*
 * CheckAction.java Created on 2007-10-15 6:18:27
 * 
 * Copyright 2004 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pp.checker;

import java.util.List;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.action.Executable;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

/**
 * 
 * 
 * 通用的交易检查类
 * 
 * @author X
 * 
 *
 */
public class CheckAction extends AbstractAction {

	private List<Object> checkers;

	/**
	 * 
	 */
	public CheckAction() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.csii.pe.action.Executable#execute(com.csii.pe.core.Context)
	 */
	public void execute(Context ctx) throws PeException {

		if (checkers != null && !checkers.isEmpty()) {

			for (int i = 0; i < checkers.size(); i++) {

				((Executable) checkers.get(i)).execute(ctx);
			}

		}

	}

	/**
	 * @param list
	 */
	public void setCheckers(List<Object> list) {
		checkers = list;
	}

}
