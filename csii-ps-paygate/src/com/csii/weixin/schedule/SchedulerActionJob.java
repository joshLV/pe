/*
 * @(#)SchedulerActionJob.java	2012-8-20
 *
 * Copyright 2004-2012 Client Service International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.weixin.schedule;

import java.util.HashMap;

import com.csii.ext.schedule.JobContext;
import com.csii.ext.schedule.JobException;
import com.csii.ext.schedule.impl.AbstractJob;
import com.csii.pe.action.Executable;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.schedule.ScheduleContext;

/**
 *
 * @author cuiyi
 * <p>
 *   Created on 2012-8-20
 *   Modification history
 * </p>
 * <p>
 *   EPP Product Expert Group, CSII
 *   Powered by CSII PowerEngine
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class SchedulerActionJob extends AbstractJob {
	
	private Executable action;

	/* (non-Javadoc)
	 * @see com.csii.ext.schedule.impl.AbstractJob#exec(com.csii.ext.schedule.JobContext)
	 */
	@Override
	protected void exec(JobContext jobcontext) throws JobException {
		final Context ctx = new ScheduleContext("", new HashMap());

		try {
			action.execute(ctx);
		} catch (PeException ex) {
			log.error("job execute error", ex);
			throw new JobException(ex);
		}
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(Executable action) {
		this.action = action;
	}

}
