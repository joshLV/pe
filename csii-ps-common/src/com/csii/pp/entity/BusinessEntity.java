/*
 * @(#)BusinessEntity.java	1.0 2009-9-29 上午02:12:05
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pp.entity;

import java.util.Date;

import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;
import com.csii.pp.dict.Constants;

/**
 * BusinessEntity.java
 * 支付平台业务参数
 *
 * @author Cuiyi
 * <p>
 *   Created on 2009-9-29
 *   Modification history
 * </p>
 * <p>
 *   IBS Product Expert Group, CSII
 *   Powered by CSII PowerEngine 6.0
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class BusinessEntity implements SqlMapAware {
	
	public static final String SYSTEM_STATUS_OPEN = "0";
	public static final String SYSTEM_STATUS_CLOSE = "1";
	public static final String SYSTEM_STATUS_BUSY = "9";
	
	private SqlMapClientOperations sqlMap;
	
	/**
	 * 机构ID
	 */
	private Object departmentId;
	
	/**
	 * 清算日期
	 */
	private Date clearDate;
	
	/**
	 * 上一清算日期
	 */
	private Date preClearDate;
	
	/**
	 * 总行账户
	 */
	private String headOfficeAcctNo;
	
	/**
	 * 业务代理号
	 */
	private String businessNo;
	
	/**
	 * 日切处理日
	 */
	private Date cutOffProcDate;
	
	/**
	 * 日切处理状态
	 */
	private String cutOffProcStatus;
	
	/**
	 * 利润分配周期
	 */
	private String profitPeriod;
	
	/**
	 * 通知商户次数
	 */
	private int merRepTimes;
	
	/**
	 * 通知商户间隔时间
	 */
	private int merRepInterval;
	
	/**
	 * 退货期限
	 */
	private int withdrawMonths;
	
	/**
	 * 系统状态，默认为开启，如果超时交易超过规定的数目后可以设置该状态为忙，
	 * 以便控制系统不再接收其他交易，当超时交易处理完毕后系统状态恢复
	 */
	private String systemStatus = SYSTEM_STATUS_OPEN;
	
	/**
	 * 初始化
	 *
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	public void init() {
		BusinessEntity tempEntity = (BusinessEntity) sqlMap.queryForObject(
				Constants.QUERY_BUSINESS_ENTITY_SQL, departmentId);
		
		this.departmentId = tempEntity.departmentId;
		this.clearDate = tempEntity.clearDate;
		this.preClearDate = tempEntity.preClearDate;
		this.businessNo = tempEntity.businessNo;
		this.cutOffProcDate = tempEntity.cutOffProcDate;
		this.cutOffProcStatus = tempEntity.cutOffProcStatus;
		this.profitPeriod = tempEntity.profitPeriod;
		this.headOfficeAcctNo = tempEntity.headOfficeAcctNo;
		this.merRepTimes = tempEntity.merRepTimes;
		this.merRepInterval = tempEntity.merRepInterval;
		this.withdrawMonths = tempEntity.withdrawMonths;
		//this.systemStatus = tempEntity.systemStatus;
	}
	
	public void update() {
		sqlMap.update(Constants.UPDATE_BUSINESS_SQL, this);
	}

	/**
	 * @return the clearDate
	 */
	public Date getClearDate() {
		return clearDate;
	}

	/**
	 * @param clearDate the clearDate to set
	 */
	public void setClearDate(Date clearDate) {
		this.clearDate = clearDate;
	}

	/**
	 * @return the preClearDate
	 */
	public Date getPreClearDate() {
		return preClearDate;
	}

	/**
	 * @param preClearDate the preClearDate to set
	 */
	public void setPreClearDate(Date preClearDate) {
		this.preClearDate = preClearDate;
	}
	
	/**
	 * @return the departmentId
	 */
	public Object getDepartmentId() {
		return departmentId;
	}

	/**
	 * @param departmentId the departmentId to set
	 */
	public void setDepartmentId(Object departmentId) {
		this.departmentId = departmentId;
	}

	/**
	 * @return the headOfficeAcctNo
	 */
	public String getHeadOfficeAcctNo() {
		return headOfficeAcctNo;
	}

	/**
	 * @param headOfficeAcctNo the headOfficeAcctNo to set
	 */
	public void setHeadOfficeAcctNo(String headOfficeAcctNo) {
		this.headOfficeAcctNo = headOfficeAcctNo;
	}

	/**
	 * @return the businessNo
	 */
	public String getBusinessNo() {
		return businessNo;
	}

	/**
	 * @param businessNo the businessNo to set
	 */
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	/**
	 * @return the cutOffProcDate
	 */
	public Date getCutOffProcDate() {
		return cutOffProcDate;
	}

	/**
	 * @param cutOffProcDate the cutOffProcDate to set
	 */
	public void setCutOffProcDate(Date cutOffProcDate) {
		this.cutOffProcDate = cutOffProcDate;
	}

	/**
	 * @return the cutOffProcStatus
	 */
	public String getCutOffProcStatus() {
		return cutOffProcStatus;
	}

	/**
	 * @param cutOffProcStatus the cutOffProcStatus to set
	 */
	public void setCutOffProcStatus(String cutOffProcStatus) {
		this.cutOffProcStatus = cutOffProcStatus;
	}

	/**
	 * @return the profitPeriod
	 */
	public String getProfitPeriod() {
		return profitPeriod;
	}

	/**
	 * @param profitPeriod the profitPeriod to set
	 */
	public void setProfitPeriod(String profitPeriod) {
		this.profitPeriod = profitPeriod;
	}

	/**
	 * @return the merRepTimes
	 */
	public int getMerRepTimes() {
		return merRepTimes;
	}

	/**
	 * @param merRepTimes the merRepTimes to set
	 */
	public void setMerRepTimes(int merRepTimes) {
		this.merRepTimes = merRepTimes;
	}

	/**
	 * @return the merRepInterval
	 */
	public int getMerRepInterval() {
		return merRepInterval;
	}

	/**
	 * @param merRepInterval the merRepInterval to set
	 */
	public void setMerRepInterval(int merRepInterval) {
		this.merRepInterval = merRepInterval;
	}

	/**
	 * @return the withdrawMonths
	 */
	public int getWithdrawMonths() {
		return withdrawMonths;
	}

	/**
	 * @param withdrawMonths the withdrawMonths to set
	 */
	public void setWithdrawMonths(int withdrawMonths) {
		this.withdrawMonths = withdrawMonths;
	}

	/**
	 * @return the systemStatus
	 */
	public String getSystemStatus() {
		return systemStatus;
	}

	/**
	 * @param systemStatus the systemStatus to set
	 */
	public void setSystemStatus(String systemStatus) {
		this.systemStatus = systemStatus;
	}

	/**
	 * @return the sqlMap
	 */
	public SqlMapClientOperations getSqlMap() {
		return sqlMap;
	}

	/**
	 * @param sqlMap the sqlMap to set
	 */
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}

}
