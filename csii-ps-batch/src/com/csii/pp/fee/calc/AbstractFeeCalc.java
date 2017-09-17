package com.csii.pp.fee.calc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractFeeCalc implements FeeCalc {
	
	protected Log log;
	
	public AbstractFeeCalc() {
		super();
		log = LogFactory.getLog(this.getClass());
	}

}
