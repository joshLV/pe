package com.csii.batch.action;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;
import com.csii.pp.entity.BusinessEntity;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.util.MiscUtil;

public class DataTransferAction extends AbstractBatchAction{

	public void execute(Context context) throws PeException {
		// TODO Auto-generated method stub
		ClearingEntity clearingEntity = (ClearingEntity) context.getData("ClearingEntity");
		DepartmentEntity departmentEntity = (DepartmentEntity) context.getData("DepartmentEntity");
		
		log.info("清算日期[" + MiscUtil.dateToString(clearingEntity.getClearDate()) + "]" + departmentEntity.getDepartmentName() + "数据转移开始");
		
		try {
			final Map para = new HashMap();
			para.put(Dict.PPCLEARDATE, clearingEntity.getClearDate());
			para.put(Dict.PPDEPARTMENTID, departmentEntity.getDepartmentId());
			para.put(Dict.PPTRANSSTATUS, Constants.TRANS_PROC_STATUS_PENDING);
			// 删除历史交易流水表中当前省中心，当前清算日期的记录
			getSqlMap().delete("pp.batch.deleteHistoryTransByClearDate", para);
			
			// 将当前省中心，当前清算日期的交易明细内容导入历史交易明细表
			getSqlMap().insert("pp.batch.transferTransToHistoryByClearDate", para);

			// 更新历史交易明细表当前省中心，当前清算日期的交易为清算处理中
			getSqlMap().update("pp.batch.updateHistoryTransProcStatusByClearDate", para);

			// 删除清算明细表中当前省中心，当前清算日期的记录
			getSqlMap().delete("pp.batch.deleteClearTrans", para);

			// 将当前清算日期，当前省中心的交易明细内容导入清算明细表
			getSqlMap().insert("pp.batch.transferTransToClearByClearDate", para);

			getTransactionTemplate().execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus arg0) {
					// 清理交易明细表
					getSqlMap().delete("pp.batch.deleteTransByClearDate", para);
					return null;
				}
			});

		} catch (DataAccessException ex) {
			throw new PeException(messageSource.getMessage("batch_dataTransfer_error",
					new Object[] { MiscUtil.dateToString(clearingEntity.getClearDate()), departmentEntity.getDepartmentName() },
					Locale.getDefault()), ex);
		} catch (Exception ex) {
			throw new PeException(messageSource.getMessage("batch_dataTransfer_error",
					new Object[] { MiscUtil.dateToString(clearingEntity.getClearDate()), departmentEntity.getDepartmentName() },
					Locale.getDefault()), ex);
		}
		
		
		log.info("清算日期[" + MiscUtil.dateToString(clearingEntity.getClearDate()) + "]" + departmentEntity.getDepartmentName() + "数据转移结束");
	}

}
