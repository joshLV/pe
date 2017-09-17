/*
 * @(#)CollectorController.java	1.0 2011-12-6 下午07:53:32
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.batch.collector;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.csii.pe.core.Context;
import com.csii.pe.core.CoreController;
import com.csii.pe.core.PeException;
import com.csii.pe.core.PeRuntimeException;
import com.csii.pe.service.executor.Executor;
import com.csii.pp.dict.Constants;
import com.csii.pp.entity.BusinessEntity;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.util.MiscUtil;

public class CollectorController extends AbstractExecutableAction {
	
	private int retryTimes;
	
	private int processNum;
	
	private List transactions;
	
	private CoreController coreController;
	
	private Executor executor;
	
	private String phase;
	
	/* (non-Javadoc)
	 * @see com.csii.pe.action.Executable#execute(com.csii.pe.core.Context)
	 */
	public void execute(Context context) throws PeException {
		try {
			// 检查对公各文件是否已经回应
			//batchFileTransfer.sendConfirm();

			if (prepare(context)) {
				submit(context);
			}
		} catch (Exception ex) {
			log.error("汇总结算执行异常", ex);
		}
	}
	
	public boolean prepare(Context context) throws PeException {
		BusinessEntity businessEntity = getBusinessEntity(Constants.COLLECTOR);
		
		log.info("处理[" + MiscUtil.dateToString(businessEntity.getClearDate()) +"]汇总结算");
		
		List list = sqlMap.queryForList("pp.batch.querySuccessProcessList", null);
		int processNumDB = list.size();
		
		log.info("前置条件：[" + processNum + "]进程完成清算");
		log.info("现有情况：[" + processNumDB +"]进程完成清算");
		
		return (processNum == processNumDB);
	}
	
	public void submit(final Context context) throws PeException {
//		executeTransaction(context);
		try {
			//使用线程池执行
			//1.避免任务长时间执行，导致控制台会话超时
			//2.任务执行异常不会杀死轮询线程
			executor.execute(new Runnable() {
				public void run() {
					executeTransaction(context);
				}
			});
		} catch (InterruptedException ex) {
			log.error("汇总结算线程执行异常", ex);
			throw new PeException(ex);
		}
		
	}
	/*
	 *如果多核心的话，日切的日期是一致的
	 */
	public void executeTransaction(Context context) {
		ClearingEntity clearingEntity = getClearingEntity(Constants.COLLECTOR);
		BusinessEntity businessEntity = getBusinessEntity(Constants.COLLECTOR);
		DepartmentEntity departmentEntity = getDepartmentEntity(Constants.COLLECTOR);
		Date coreDate = (Date) sqlMap.queryForObject("pp.core.queryDepartmentDate","weixin");// ft_department
		
		if (coreDate.compareTo(businessEntity.getClearDate()) > 0) {
			log.info("[" + MiscUtil.dateToString(businessEntity.getClearDate()) + "]汇总结算开始");
			
			businessEntity.setPreClearDate(MiscUtil.rolDate(clearingEntity.getClearDate(), -1));
			businessEntity.update();
		} else {
			return;
		}
		
		context.setData("ClearingEntity", clearingEntity);
		context.setData("BusinessEntity", businessEntity);
		context.setData("DepartmentEntity", departmentEntity);
		
		try {
			//循环执行直到清算控制表的清算日期等于业务参数控制表中的前一清算日期,
			//并且清算控制表中的清算状态为成功
			int retry = 0;
			while (businessEntity.getPreClearDate().compareTo(clearingEntity.getClearDate()) > 0
					|| ( businessEntity.getPreClearDate().compareTo(clearingEntity.getClearDate())== 0 &&
						!(phase.equals(clearingEntity.getClearPhase())  && 
						  ClearingEntity.CLEAR_STATUS_OK.equals(clearingEntity.getClearStatus())) 
						)
					){
				
				retry++;
				
				if (transactions != null && !transactions.isEmpty()) {
					for (Iterator it = transactions.iterator(); it.hasNext();) {
						String transactionId = (String) it.next();
						context.setTransactionId(transactionId);
						coreController.execute(context);
					}
				}
				
//				businessEntity.setClearDate(corpDate);
			    businessEntity.setClearDate(MiscUtil.rolDate(businessEntity.getClearDate(),-1));
				businessEntity.update();
				
				log.info("[" + MiscUtil.dateToString(clearingEntity.getClearDate()) + "]汇总结算完成");
				
				if (retry >= retryTimes) {
					break;
				}
			}
		} catch (PeException ex) {
			throw new PeRuntimeException(ex);
		}
	}
	
	private BusinessEntity getBusinessEntity(String departmentId) {
		BusinessEntity businessEntity = (BusinessEntity) sqlMap.queryForObject(Constants.QUERY_BUSINESS_ENTITY_SQL, departmentId);
		businessEntity.setSqlMap(sqlMap);
		return businessEntity;
	}
	
	private ClearingEntity getClearingEntity(String departmentId) {
		ClearingEntity clearingEntity = (ClearingEntity) sqlMap.queryForObject(Constants.QUERY_CLEARING_ENTITY_SQL, departmentId);
		clearingEntity.setSqlMap(sqlMap);
		return clearingEntity;
	}
	private DepartmentEntity getDepartmentEntity(String departmentId) {
		DepartmentEntity departmentEntity = (DepartmentEntity) sqlMap.queryForObject(Constants.QUERY_DEPARTMENT_ENTITY_SQL, departmentId);
		departmentEntity.setSqlMap(sqlMap);
		return departmentEntity;
	}

	/**
	 * @param retryTimes the retryTimes to set
	 */
	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	/**
	 * @param processNum the processNum to set
	 */
	public void setProcessNum(int processNum) {
		this.processNum = processNum;
	}
	
	/**
	 * @param transactions the transactions to set
	 */
	public void setTransactions(List transactions) {
		this.transactions = transactions;
	}

	/**
	 * @param executor the executor to set
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	/**
	 * @param coreController the coreController to set
	 */
	public void setCoreController(CoreController coreController) {
		this.coreController = coreController;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}
	
}
