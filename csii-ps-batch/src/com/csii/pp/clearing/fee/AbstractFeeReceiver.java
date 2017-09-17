package com.csii.pp.clearing.fee;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;
import com.csii.pp.clearing.MerchantTransManager;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.fee.calc.FeeCalcFactory;
import com.csii.pp.order.OrderManager;

public abstract class AbstractFeeReceiver implements FeeReceiver,SqlMapAware {

	protected FeeCalcFactory feeCalcFactory;
	
	protected MerchantTransManager merchantTransManager;

	protected OrderManager orderManager;
	
	protected DepartmentEntity departmentEntity;
	
	protected Map feeReceivers;
	
	protected SqlMapClientOperations sqlMap;
	
	protected Log log;

	protected ClearingEntity clearingEntity;
	
	public AbstractFeeReceiver() {
		super();
		log = LogFactory.getLog(getClass());
	}

	/**
	 * @param factory
	 */
	public void setFeeCalcFactory(FeeCalcFactory factory) {
		feeCalcFactory = factory;
	}



	/**
	 * @param manager
	 */
	public void setOrderManager(OrderManager manager) {
		orderManager = manager;
	}
	/**
	 * @param feeReceivers the feeReceivers to set
	 */
	public void setFeeReceivers(Map feeReceivers) {
		this.feeReceivers = feeReceivers;
	}

	/* (non-Javadoc)
	 * @see com.csii.pp.entity.DepartmentEntityAware#setDepartmentEntity(com.csii.pp.entity.DepartmentEntity)
	 */
	public void setDepartmentEntity(DepartmentEntity departmentEntity) {
		this.departmentEntity = departmentEntity;
	}

	public void setClearingEntity(ClearingEntity clearingEntity) {
		this.clearingEntity = clearingEntity;
	}

	public void setMerchantTransManager(MerchantTransManager merchantTransManager) {
		this.merchantTransManager = merchantTransManager;
	}

	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}
	
}
