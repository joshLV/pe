package com.csii.batch.collector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapClientOperations;
import org.springframework.transaction.support.TransactionTemplate;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.config.support.SqlMapAware;
import com.csii.pe.config.support.TransactionTemplateAware;

public abstract class AbstractExecutableAction extends AbstractAction 
		implements SqlMapAware, TransactionTemplateAware{
	
	protected Log log = LogFactory.getLog(getClass());

	protected SqlMapClientOperations sqlMap;

	protected TransactionTemplate transactionTemplate;
	
	/*
	 * (non-Javadoc)
	 * @see com.csii.pe.config.support.SqlMapAware#setSqlMap(org.springframework.orm.ibatis.SqlMapClientOperations)
	 */
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.csii.pe.config.support.TransactionTemplateAware#setTransactionTemplate
	 * (org.springframework.transaction.support.TransactionTemplate)
	 */
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

}
