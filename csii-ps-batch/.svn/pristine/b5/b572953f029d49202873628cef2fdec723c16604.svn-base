package com.csii.pp.clearing;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;
import com.csii.pe.service.id.IdFactory;
import com.csii.pp.dict.Dict;
import com.csii.pp.util.MiscUtil;

public class MerchantTransManager implements SqlMapAware {

	private IdFactory clearingIdFactory;

	protected SqlMapClientOperations sqlMap;
	
	/**
	 * @param factory
	 */
	public void setClearingIdFactory(IdFactory factory) {
		clearingIdFactory = factory;
	}

	/* (non-Javadoc)
	 * @see com.csii.pe.config.support.SqlMapAware#setSqlMap(org.springframework.orm.ibatis.SqlMapClientOperations)
	 */
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}


	public void insertData(Map<String, Object> data) {
		String seqNo = clearingIdFactory.generate().toString();
		data.put(Dict.PPCLEARSEQNO, seqNo);
		data.put(Dict.PPCLEARDATE, data.get(Dict.PPCLEARDATE));
		if (data.get(Dict.PPTRANSDATE) == null) {
			data.put(Dict.PPTRANSDATE, MiscUtil.getDate());
		}
		data.put(Dict.PPTRANSDATETIME, new java.util.Date());
		sqlMap.insert("pp.batch.insertMerchantTrans", data);
	}
	
	
	public BigDecimal queryHtHostTransToFeeAmt(Map data) {
		String feeAmt = (String) sqlMap.queryForObject("pp.batch.queryHtHostTransToFeeAmt", data);
		BigDecimal feemount = new BigDecimal(feeAmt);
		return feemount;
	}

	/* (non-Javadoc)
	 * @see com.csii.pp.entity.BusinessEntityAware#setBusinessEntity(com.csii.pp.entity.BusinessEntity)
	 */

}
