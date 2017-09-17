package com.csii.batch.job;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.ext.schedule.JobContext;
import com.csii.ext.schedule.JobException;
import com.csii.ext.schedule.impl.AbstractJob;
import com.csii.pe.core.Context;
import com.csii.pe.core.CoreController;
import com.csii.pe.core.PeException;
import com.csii.pe.service.schedule.ScheduleContext;

public class CollectorJob extends AbstractJob{

	private CoreController coreController;
	private List transactions;
   	protected Log log = LogFactory.getLog(getClass());
	@Override
	protected void exec(JobContext arg0) throws JobException {
		// TODO Auto-generated method stub
		log.info("--------collector job start---");
		Iterator it = transactions.iterator();
		Map map = new HashMap();
		while (it.hasNext()) {
			String id = (String) it.next();
			map.put("transactionId", id);
			Context context = new ScheduleContext(id, map);
			try {
				coreController.execute(context);
			} catch (PeException ex) {
				log.error("coreController exception", ex);
			}
		}
	}
	public void setCoreController(CoreController coreController) {
		this.coreController = coreController;
	}
	public void setTransactions(List transactions) {
		this.transactions = transactions;
	}

}
