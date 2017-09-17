package com.csii.batch.action;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;

public class DayDataTransferAction extends AbstractBatchAction{

	public void execute(Context context) throws PeException {
		// TODO Auto-generated method stub
		ClearingEntity clearingEntity = (ClearingEntity) context.getData("ClearingEntity");
		DepartmentEntity departmentEntity = (DepartmentEntity) context.getData("DepartmentEntity");
		
		log.info("清算日期[" + MiscUtil.dateToString(clearingEntity.getClearDate()) + "]" + departmentEntity.getDepartmentName() + "数据转移开始");
		
		try {
			final Map para = new HashMap();
			para.put(Dict.PPTRANSSTATUS, Constants.JNL_TRANS_STATUS_SUCCESS);
			para.put(Dict.PPTRANSSTEP, Constants.TRANS_STEP_CHECK_NO);
			// 查询日间批量交易
			List<Order> list = getSqlMap().queryForList("pp.batch.queryDayClearTrans");
			for(final Order tmp : list){
				getTransactionTemplate().execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus arg0) {
						// 插入bt_dayclear_trans
						tmp.setStep(Constants.TRANS_STEP_CHECK_OK);
						getSqlMap().insert("pp.batch.insertBtDayClearTransOrder", tmp);
						getSqlMap().update("pp.batch.updateOtTransStatus", para);
						return null;
					}
				});
			}
			
			

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
