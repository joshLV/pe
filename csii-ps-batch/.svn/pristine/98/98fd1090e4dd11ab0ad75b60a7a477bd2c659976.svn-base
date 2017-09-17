package com.csii.batch.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.batch.check.Checker;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.core.PeRuntimeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.order.Order;
import com.csii.pe.action.Executable;

public class BthostCheckingAction extends AbstractBatchAction {

	private int queryCount;

	private Checker check;

	/* (non-Javadoc)
	 * @see com.csii.pe.action.Executable#execute(com.csii.pe.core.Context)
	 */
	public void execute(Context ctx) throws PeException {
		ClearingEntity clearingEntity = (ClearingEntity) ctx.getData("ClearingEntity");
		DepartmentEntity departmentEntity = (DepartmentEntity) ctx.getData("DepartmentEntity");
		Map prePara = new HashMap();
		prePara.put(Dict.PPCLEARDATE, clearingEntity.getClearDate());
		prePara.put(Dict.PPDEPARTMENTID, departmentEntity.getDepartmentId());

//		getSqlMap().delete("pp.batch.deleteHTCheckError", prePara);
//		getSqlMap().delete("pp.batch.deleteCheckError", prePara);
		
		boolean endFlag = false;

		//查询bt_mer_sett表中发送成功的交易记录
//		Map para = new HashMap();
//		para.put(Dict.PPDEPARTMENTID, departmentEntity.getDepartmentId());
		prePara.put(Dict.PPCHECKSTATUS, Constants.TRANS_CHECK_STATUS_NO);
		prePara.put(Dict.PPQUERYNUMBER, queryCount);

		int counter = 0;
		while (!endFlag) {
			//查询指定笔数的记录后，进行每条记录的对账处理
			final List checkList = getSqlMap().queryForList("pp.batch.queryHostHistoryForCheck", prePara);
			counter++;
			log.info("第" + counter + "次查询bt_host_trans 交易记录 支付平台共有" + checkList.size() + "条记录");
			
			if (checkList == null || checkList.isEmpty()) {
				endFlag = true;
			} else {
				getTransactionTemplate().execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus arg0) {
						for (Iterator it = checkList.iterator(); it.hasNext();) {
							Map hostData = (Map) it.next();
							try {
								check.check(hostData);
							} catch (Exception ex) {
								log.error("对账出错，出错数据：" + hostData, ex);
								throw new PeRuntimeException(ex);
							}
						}
						return null;
					}
				});
				
				if (checkList.size() < queryCount) {
					endFlag = true;
				}
			}
		}
		
	}
	
	/**
	 * @param queryCount the queryCount to set
	 */
	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}

	public void setCheck(Checker check) {
		this.check = check;
	}

}
