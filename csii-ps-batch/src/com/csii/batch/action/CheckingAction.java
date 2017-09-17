package com.csii.batch.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.action.Executable;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Dict;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;

public class CheckingAction extends AbstractBatchAction{
	
	private int queryCount;

	private List checkerList;

	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		for(Iterator it = checkerList.iterator();it.hasNext();){
			Executable exeAction = (Executable)it.next();
			exeAction.execute(ctx);
		}
		
		final ClearingEntity clearingEntity = (ClearingEntity) ctx.getData("ClearingEntity");
		final DepartmentEntity departmentEntity = (DepartmentEntity) ctx.getData("DepartmentEntity");
		
		        //对账结束，将对账差错转入历史表，查询时从历史表查
				getTransactionTemplate().execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus arg0) {
						//将对帐差错明细表的内容转移到历史表中
						Map data = new HashMap();
						data.put(Dict.PPDEPARTMENTID, departmentEntity.getDepartmentId());
						data.put(Dict.PPCLEARDATE, clearingEntity.getClearDate());
						getSqlMap().insert("pp.batch.transferCheckErrorToHistory", data);
						getSqlMap().delete("pp.batch.deleteCheckError", data);
						return null;
					}
				});
				
				//对账结束，转移核心交易明细到历史表,然后删除当前核心交易明细表中的内容
				getTransactionTemplate().execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus arg0) {
						Map data = new HashMap();
						data.put(Dict.PPDEPARTMENTID, departmentEntity.getDepartmentId());
						data.put(Dict.PPCLEARDATE, clearingEntity.getClearDate());
						
						getSqlMap().delete("pp.batch.deleteHostHistoryByDeptIdAndDate", data);
						getSqlMap().insert("pp.batch.transferHostHistoryToHistory", data);
						//删除核心交易明细表中当前清算日期的内容
						getSqlMap().delete("pp.batch.deleteHostHistoryByDeptId", data);
						return null;
					}
				});
		
		
	}

	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}

	public void setCheckerList(List checkerList) {
		this.checkerList = checkerList;
	}
	
	

}
