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

public class BtClearTransCheckingAction extends AbstractBatchAction {

	private int queryCount;

	private Map checkerMap;

	/* (non-Javadoc)
	 * @see com.csii.pe.action.Executable#execute(com.csii.pe.core.Context)
	 */
	public void execute(Context ctx) throws PeException {

		ClearingEntity clearingEntity = (ClearingEntity) ctx.getData("ClearingEntity");
		final DepartmentEntity departmentEntity = (DepartmentEntity) ctx.getData("DepartmentEntity");
		
		 Map prePara = new HashMap();
			prePara.put(Dict.PPCLEARDATE, clearingEntity.getClearDate());
			prePara.put(Dict.PPDEPARTMENTID, departmentEntity.getDepartmentId());
			boolean endFlag = false;

			//每次固定记录数的循环处理，直到所有需要对账的数据都处理完成
			prePara.put(Dict.PPCHECKSTATUS, Constants.TRANS_CHECK_STATUS_NO);
			prePara.put(Dict.PPQUERYNUMBER, queryCount);

			int counter = 0;
			while (!endFlag) {
				 List resultList =null;
				//查询指定笔数的记录后，进行每条记录的对账处理
				final List checkList = getSqlMap().queryForList("pp.batch.queryHostHistoryForCheck", prePara);
				counter++;
				log.info("第" + counter + "次查询" + departmentEntity.getDepartmentName()+"交易记录 ，核心系统共有"
						+ checkList.size() + "条记录");
				
				if (checkList == null || checkList.isEmpty()) {
					endFlag = true;
				} else {
					getTransactionTemplate().execute(new TransactionCallback() {
						public Object doInTransaction(TransactionStatus arg0) {
							for (Iterator it = checkList.iterator(); it.hasNext();) {
								 Map hostData = (Map) it.next();
								try {
									((Checker) checkerMap.get(departmentEntity.getDepartmentType()))
											.check(hostData);
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

	/**
	 * @param checkerMap the checkerMap to set
	 */
	public void setCheckerMap(Map checkerMap) {
		this.checkerMap = checkerMap;
	}
}
