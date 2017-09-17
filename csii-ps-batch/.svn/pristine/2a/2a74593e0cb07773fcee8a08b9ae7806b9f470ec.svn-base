/*
 * @(#)CutoffAction.java	1.0 2011-12-15 下午05:12:47
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.batch.collector;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.entity.BusinessEntity;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.util.MiscUtil;

public class CutoffAction extends AbstractExecutableAction {

	/* (non-Javadoc)
	 * @see com.csii.pe.action.Executable#execute(com.csii.pe.core.Context)
	 */
	public void execute(Context context) throws PeException {
		ClearingEntity clearingEntity = (ClearingEntity) context.getData("ClearingEntity");
		BusinessEntity businessEntity = (BusinessEntity) context.getData("BusinessEntity");
		
		log.info("汇总结算日切开始，前一清算日期[" + MiscUtil.dateToString(clearingEntity.getClearDate()) + "]");
		
		// 如果业务参数表中的前一清算日期大于清算控制表中清算日期就进行日切处理
		if (businessEntity.getPreClearDate().compareTo(clearingEntity.getClearDate()) > 0) {
			// 修改支付网关清算控制表，每次只滚动一天
			clearingEntity.setClearDate(MiscUtil.rolDate(clearingEntity.getClearDate(), -1));
			clearingEntity.setProcDate(MiscUtil.getCurrentDate());
			clearingEntity.update();
		} else {
			return;
		}
		
		log.info("汇总结算日切结束，当前清算日期[" + MiscUtil.dateToString(clearingEntity.getClearDate()) + "]");
	}

}
