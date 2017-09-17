
package com.csii.batch.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.util.MiscUtil;

public class EndBatchAction extends AbstractBatchAction{
	
	private int month;

	public void execute(Context context) throws PeException {
		// TODO Auto-generated method stub
		final ClearingEntity clearingEntity = (ClearingEntity) context.getData("ClearingEntity");
		final DepartmentEntity departmentEntity = (DepartmentEntity) context.getData("DepartmentEntity");
		
		//移除bt_mer_trans
		final Map paraMap = new HashMap();
		paraMap.put("ClearDate", clearingEntity.getClearDate());
	    paraMap.put("DepartmentId", clearingEntity.getDepartmentId());
		getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				getSqlMap().insert("pp.batch.insertHtmertransBySettdate",paraMap);
				getSqlMap().delete("pp.batch.deleteBtmertransBySettDate",paraMap);
				return null;
			}
		});
		
		getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				log.info("处理" + departmentEntity.getDepartmentName() + "清算交易明细表开始");
				
				Map para = new HashMap();
				para.put(Dict.PPDEPARTMENTID, departmentEntity.getDepartmentId());
				para.put(Dict.PPCLEARDATE, clearingEntity.getClearDate());

				// 删除当前清算明细表的内容
				getSqlMap().delete("pp.batch.deleteClearTrans", para);

				// 修改历史交易明细表中的当前清算日期的交易处理状态为正常
				para.put(Dict.PPTRANSSTATUS, Constants.TRANS_PROC_STATUS_OK);
				getSqlMap().update("pp.batch.updateHistoryTransProcStatusByClearDate", para);
				log.info("处理"+ departmentEntity.getDepartmentName() + "清算交易明细表结束");
				return null;
			}
		});
		
        final Date rolDate = MiscUtil.rolMonth(clearingEntity.getClearDate(), month);
		
		log.info("开始转移" + departmentEntity.getDepartmentName() + "历史交易明细表中[" + MiscUtil.dateToString(rolDate)
				+ "]之前的数据，清算日期["
				+ MiscUtil.dateToString(clearingEntity.getClearDate()) + "]");
		
		getTransactionTemplate().execute(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus arg0) {
				
				// 清理交易明细历史表
				log.info("清理" + departmentEntity.getDepartmentName() + "历史明细表开始");
				Map para = new HashMap();
				para.put(Dict.PPDEPARTMENTID, departmentEntity.getDepartmentId());
				para.put(Dict.PPCLEARDATE, rolDate);
				
				getSqlMap().insert("pp.batch.transferHistoryToHistoryAll", para);

				getSqlMap().delete("pp.batch.deleteHistoryBeforeDate", para);
				log.info("清理" + departmentEntity.getDepartmentName() + "历史明细表结束");
				
				return null;
			}

		});
		
		log.info("结束转移" + departmentEntity.getDepartmentName() + "历史交易明细表中[" + MiscUtil.dateToString(rolDate)
				+ "]之前的数据，清算日期["
				+ MiscUtil.dateToString(clearingEntity.getClearDate()) + "]");
	}

	public void setMonth(int month) {
		this.month = month;
	}

}
