/*
 * @(#)LoadSavingSystemFileAction.java	1.0 2009-10-23 下午11:32:06
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.batch.action;

import java.util.HashMap;
import java.util.Map;

import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;

/**
 * LoadSavingSystemFileAction.java
 * 支付宝下载对账文件
 *
 * @author Cuiyi
 * <p>
 *   Created on 2009-10-23
 *   Modification history
 * </p>
 * <p>
 *   IBS Product Expert Group, CSII
 *   Powered by CSII PowerEngine 6.0
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class LoadAlipayFileAction extends AbstractLoadHostCheckingFileAction {
	
	/* (non-Javadoc)
	 * @see com.csii.pp.batch.action.AbstractLoadHostCheckingFileAction#load(java.util.Map, java.util.Map)
	 */
	public void load(Map transData) {
		Map para = new HashMap();
		if("交易".equals(transData.get("trade_type"))){
			para.put("TransName", "00");
		}else if("退款".equals(transData.get("trade_type"))){
			para.put("TransName", "01");
		}
		para.put("HostSeqNo", transData.get("out_trade_no"));
		para.put("TransSeqNo", transData.get("out_trade_no"));
		para.put("TransAmount", transData.get("transAmt"));
		para.put(Dict.PPCHECKSTATUS, Constants.TRANS_CHECK_STATUS_NO);
		para.put(Dict.PPDEPARTMENTID, transData.get("DepartmentId"));
		para.put("TransDate", transData.get("ClearDate"));
		para.put("ClearDate", transData.get("ClearDate"));
		para.put("BankId", transData.get("BankId"));
		getSqlMap().insert("pp.batch.insertHostHistory", para);
	}

}
