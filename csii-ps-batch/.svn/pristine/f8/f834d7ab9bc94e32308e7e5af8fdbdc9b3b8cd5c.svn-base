package com.csii.batch.check;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.order.Order;

public abstract class AbstractChecker implements Checker, SqlMapAware {

	protected Log log;

	protected SqlMapClientOperations sqlMap;
	
	public AbstractChecker() {
		super();
		log = LogFactory.getLog(getClass());
	}
	protected void insertHostCheckError(Order order) {
		sqlMap.insert("pp.batch.insertHostCheckError", order);
	}
	protected void insertCheckError(Map data) {
		sqlMap.insert("pp.batch.insertCheckError", data);
	}

	protected void updateHostHistory(Map data) {
		sqlMap.insert("pp.batch.updateHostHistoryCheckStatus", data);
	}
	protected void updateBtMerSett(Order order){
		sqlMap.update("pp.batch.updateBtmerSettSendStatus",order);
	}
	protected void updateBtFeeSett(Order order){
		sqlMap.update("pp.batch.updateBtfeeSettSendStatus",order);
	}
	/* (non-Javadoc)
	 * @see com.csii.pe.config.support.SqlMapAware#setSqlMap(org.springframework.orm.ibatis.SqlMapClientOperations)
	 */
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}

	/* (non-Javadoc)
	 * @see com.csii.pp.entity.DepartmentEntityAware#setDepartmentEntity(com.csii.pp.entity.DepartmentEntity)
	 */
}
