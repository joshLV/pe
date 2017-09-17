package com.csii.pp.signature;

import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;

/**
 * 接入方key管理类
 * 
 * @author L
 */
public class MerchantXmlSignature implements SqlMapAware {

	private SqlMapClientOperations sqlMap;

	public MerchantXmlSignature() {
		super();
	}


	public Map getKeyId(String keyId) {
		Map ret = (Map)sqlMap.queryForObject("pp.core.queryKeyContent", keyId);
		return ret;

	}
	
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}

}
