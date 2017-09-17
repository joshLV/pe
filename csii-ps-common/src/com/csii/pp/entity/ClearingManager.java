/*
 * @(#)ClearingManager.java	1.0 2009-9-29 下午11:30:05
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pp.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;
import com.csii.pp.dict.Constants;

/**
 * ClearingManager.java
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
public class ClearingManager implements SqlMapAware {
	
	Log log =LogFactory.getLog(ClearingManager.class);
	
	private SqlMapClientOperations sqlMap;
	
	private List clearingList;
	
	private Map clearingMap=new HashMap();

	/* (non-Javadoc)
	 * @see com.csii.pe.config.support.SqlMapAware#setSqlMap(org.springframework.orm.ibatis.SqlMapClientOperations)
	 */
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	public void init() {
		clearingList = sqlMap.queryForList(Constants.QUERY_CLEARING_LIST_SQL, null);
		if (clearingList != null && !clearingList.isEmpty()) {
			for (Iterator iterator = clearingList.iterator(); iterator.hasNext();) {
				ClearingEntity entity = (ClearingEntity) iterator.next();
				/*
				 * ClearingEntity对象是查询数据库后创建，非容器控制。
				 * 因此sqlMap无法通过容器获取，通过手动设置注入sqlMap
				 */
				entity.setSqlMap(sqlMap);
//				clearingMap = new HashMap();
				clearingMap.put(entity.getDepartmentId(), entity);
			}
		}else{
			log.error("清算控制表无初始数据，请检查！");
		}
	}

	
	public List getClearingList() {
		return clearingList;
	}

	
	public ClearingEntity getClearingEntity(String bankId) {
		return (ClearingEntity) clearingMap.get(bankId);
	}

}
