/*
 * @(#)DepartmentEntity.java	1.0 2009-10-31 上午10:48:48
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pp.entity;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;
import com.csii.pe.core.PeRuntimeException;
import com.csii.pp.dict.Constants;

/**
 * DepartmentEntity.java
 *
 * @author Cuiyi
 * <p>
 *   Created on 2009-10-31
 *   Modification history
 * </p>
 * <p>
 *   IBS Product Expert Group, CSII
 *   Powered by CSII PowerEngine 6.0
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class DepartmentEntity implements SqlMapAware {
	
	/**
	 * 机构ID
	 */
	private Object departmentId;
	
	/**
	 * 银行顺序号
	 */
	private Object bankId;
	
	/**
	 * 上级机构ID
	 */
	private Object parentId;
	
	/**
	 * 机构号
	 */
	private String departmentCode;
	
	/**
	 * 机构名称
	 */
	private String departmentName;
	
	/**
	 * 机构类型
	 */
	private String departmentType;
	
	/**
	 * 机构日期
	 */
	private Date departmentDate;
	
	/**
	 * 所在省
	 */
	private String province;
	
	/**
	 * 所在市
	 */
	private String city;
	
	/**
	 * 地址
	 */
	private String address;
	
	/**
	 * 邮政编码
	 */
	private String zipCode;
	
	/**
	 * 电话
	 */
	private String phone;
	
	/**
	 * 电子邮件
	 */
	private String email;
	
	/**
	 * 机构状态
	 */
	private String status;
	
	/**
	 * 机构账户列表
	 */
	private List accounts;
	
	private SqlMapClientOperations sqlMap;
	
	public void init() {
		DepartmentEntity tempEntity = (DepartmentEntity) sqlMap.queryForObject(
				Constants.QUERY_DEPARTMENT_ENTITY_SQL, departmentId);
		
		this.bankId = tempEntity.bankId;
		this.parentId = tempEntity.parentId;
		this.departmentCode = tempEntity.departmentCode;
		this.departmentName = tempEntity.departmentName;
		this.departmentType = tempEntity.departmentType;
		this.departmentDate = tempEntity.departmentDate;
		this.province = tempEntity.province;
		this.city = tempEntity.city;
		this.address = tempEntity.address;
		this.zipCode = tempEntity.zipCode;
		this.phone = tempEntity.phone;
		this.email = tempEntity.email;
		this.status = tempEntity.status;
		this.accounts = tempEntity.accounts;
	}
	
	public void update() {
		sqlMap.update(Constants.UPDATE_DEPARTMENT_ENTITY_SQL, this);
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
	 * @return the bankId
	 */
	public Object getBankId() {
		return bankId;
	}

	/**
	 * @param bankId the bankId to set
	 */
	public void setBankId(Object bankId) {
		this.bankId = bankId;
	}
	
	/**
	 * @return the parentId
	 */
	public Object getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(Object parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the departmentCode
	 */
	public String getDepartmentCode() {
		return departmentCode;
	}


	/**
	 * @param departmentCode the departmentCode to set
	 */
	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}


	/**
	 * @return the departmentName
	 */
	public String getDepartmentName() {
		return departmentName;
	}


	/**
	 * @param departmentName the departmentName to set
	 */
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}


	/**
	 * @return the departmentType
	 */
	public String getDepartmentType() {
		return departmentType;
	}


	/**
	 * @param departmentType the departmentType to set
	 */
	public void setDepartmentType(String departmentType) {
		this.departmentType = departmentType;
	}

	/**
	 * @return the departmentDate
	 */
	public Date getDepartmentDate() {
		return departmentDate;
	}


	/**
	 * @param departmentDate the departmentDate to set
	 */
	public void setDepartmentDate(Date departmentDate) {
		this.departmentDate = departmentDate;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * @return the accounts
	 */
	public List getAccounts() {
		return accounts;
	}

	/**
	 * @param accounts the accounts to set
	 */
	public void setAccounts(List accounts) {
		this.accounts = accounts;
	}

	/* (non-Javadoc)
	 * @see com.csii.pe.config.support.SqlMapAware#setSqlMap(org.springframework.orm.ibatis.SqlMapClientOperations)
	 */
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}
}
