package com.csii.pp.entity;

import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;
import com.csii.pp.dict.Constants;

/**
 * ProcessEntity.java
 * 进程信息
 *
 * @author Cuiyi
 * <p>
 *   Created on 2009-9-26
 *   Modification history
 * </p>
 * <p>
 *   IBS Product Expert Group, CSII
 *   Powered by CSII PowerEngine 6.0
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class ProcessEntity implements SqlMapAware {
	
	/**
	 * 进程ID
	 */
	private Object pid;
	
	/**
	 * 进程地址
	 */
	private String host;

	/**
	 * 进程端口
	 */
	private int port;
	
	/**
	 * 所属机构ID
	 */
	private Object departmentId;

	/**
	 * 进程状态
	 */
	private String status;
	
	private SqlMapClientOperations sqlMap;
	
	public void init() {
		ProcessEntity tempEntity = (ProcessEntity) sqlMap.queryForObject(
				Constants.QUERY_PROCESS_ENTITY_SQL, pid);
		
		this.host = tempEntity.host;
		this.port = tempEntity.port;
		this.departmentId = tempEntity.departmentId;
		this.status = tempEntity.status;
	}
	
	public void update() {
		sqlMap.update(Constants.UPDATE_PROCESS_ENTITY_SQL, this);
	}

	/**
	 * @return the pid
	 */
	public Object getPid() {
		return pid;
	}
	
	/**
	 * @param pid the pid to set
	 */
	public void setPid(Object pid) {
		this.pid = pid;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
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
	 * @return the sqlMap
	 */
	public SqlMapClientOperations getSqlMap() {
		return sqlMap;
	}

	/* (non-Javadoc)
	 * @see com.csii.pe.config.support.SqlMapAware#setSqlMap(org.springframework.orm.ibatis.SqlMapClientOperations)
	 */
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}
}