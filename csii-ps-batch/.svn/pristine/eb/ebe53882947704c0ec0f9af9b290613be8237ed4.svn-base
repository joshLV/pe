/*
 * AbstractProfitAssigner.java Created on 2007-10-9 15:42:31
 * 
 * Copyright 2004 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pp.clearing.profit;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;
import com.csii.pp.clearing.MerchantTransManager;
import com.csii.pp.clearing.ProfitAssigner;
import com.csii.pp.fee.calc.FeeCalcFactory;

public abstract class AbstractProfitAssigner implements ProfitAssigner, SqlMapAware {


	protected SqlMapClientOperations sqlMap;
	
	protected MerchantTransManager merchantTransManager;
	
	protected FeeCalcFactory feeCalcFactory;
	
	protected static Log log = LogFactory.getLog(AbstractProfitAssigner.class);

	


	public void setMerchantTransManager(MerchantTransManager merchantTransManager) {
		this.merchantTransManager = merchantTransManager;
	}

	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}

	public void setFeeCalcFactory(FeeCalcFactory feeCalcFactory) {
		this.feeCalcFactory = feeCalcFactory;
	}

}
