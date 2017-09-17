/*
 * @(#)ClearingEntity.java	1.0 2009-9-29 下午05:20:02
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
 * ClearingEntity.java
 * 清算控制信息
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
public class ClearingEntity implements SqlMapAware {
	
	public static final String CLEAR_STATUS_OK = "S";
	public static final String CLEAR_STATUS_ERROR = "E";
	public static final String CLEAR_STATUS_PENDING = "P";
	
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
	 * 处理日期
	 */
	private Date procDate;
	
	/**
	 * 完成日期
	 */
	private Date endDate;
	
	/**
	 * 处理阶段
	 */
	private String clearPhase;
	
	/**
	 * 处理状态
	 */
	private String clearStatus;
	
	/**
	 * 初始化
	 *
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	public void init() {
		ClearingEntity tempEntity = (ClearingEntity) sqlMap.queryForObject(
				Constants.QUERY_CLEARING_ENTITY_SQL, departmentId);
		
		this.departmentId = tempEntity.departmentId;
		this.clearDate = tempEntity.clearDate;
		this.procDate = tempEntity.procDate;
		this.endDate = tempEntity.endDate;
		this.clearPhase = tempEntity.clearPhase;
		this.clearStatus = tempEntity.clearStatus;
	}

	/**
	 * 更新记录
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	public void update() {
		sqlMap.update(Constants.UPDATE_CLEARING_SQL, this);
	}

	/* (non-Javadoc)
	 * @see com.csii.pe.config.support.SqlMapAware#setSqlMap(org.springframework.orm.ibatis.SqlMapClientOperations)
	 */
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
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
	 * @return the procDate
	 */
	public Date getProcDate() {
		return procDate;
	}

	/**
	 * @param procDate the procDate to set
	 */
	public void setProcDate(Date procDate) {
		this.procDate = procDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the clearPhase
	 */
	public String getClearPhase() {
		return clearPhase;
	}

	/**
	 * @param clearPhase the clearPhase to set
	 */
	public void setClearPhase(String clearPhase) {
		this.clearPhase = clearPhase;
	}

	/**
	 * @return the clearStatus
	 */
	public String getClearStatus() {
		return clearStatus;
	}

	/**
	 * @param clearStatus the clearStatus to set
	 */
	public void setClearStatus(String clearStatus) {
		this.clearStatus = clearStatus;
	}
}
