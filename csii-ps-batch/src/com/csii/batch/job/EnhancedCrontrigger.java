package com.csii.batch.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.ext.schedule.JobContext;
import com.csii.ext.schedule.trigger.CronTrigger;

public class EnhancedCrontrigger extends CronTrigger {
	protected static Log log = LogFactory.getLog(EnhancedCrontrigger.class);
	
	public void fired(JobContext jobContext)
    {
    	try{
    		super.fired(jobContext);
    	}catch(Throwable e){
    		log.error("Fired Error",e);
    	}
    }
}
