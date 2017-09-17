package com.csii.batch.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.core.PeRuntimeException;
import com.csii.pp.clearing.ClearingerFactory;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;

public class ClearingAction extends AbstractBatchAction{
	
    private OrderManager orderManager;
	
	private int queryCount;
	
	private ClearingerFactory clearingerFactory;

	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub

		final ClearingEntity clearingEntity = (ClearingEntity) ctx.getData("ClearingEntity");
		final DepartmentEntity departmentEntity = (DepartmentEntity) ctx.getData("DepartmentEntity");
		boolean endFlag = false;
		//每次固定记录数的循环处理，只到所有需要清分的数据都处理完成
		Map para = new HashMap();
		para.put(Dict.PPTRANSSTEP, Constants.TRANS_STEP_CHECK_OK);
		para.put(Dict.PPQUERYNUMBER, queryCount);
		para.put(Dict.PPDEPARTMENTID, departmentEntity.getDepartmentId());
		
		int counter = 0;
		while (!endFlag) {
			//查询指定笔数的记录后，进行每条记录的清分处理
			final List orderList = orderManager.getOrderForClear(para);
			
			counter++;
			log.info("第" + counter + "次查询" + departmentEntity.getDepartmentName() + "共" + orderList.size() + "条记录参与清分");
			
			if (orderList == null || orderList.isEmpty()) {
				endFlag = true;
			} else {
				getTransactionTemplate().execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus arg0) {
						for (Iterator it = orderList.iterator(); it.hasNext();) {
							final Order order = (Order) it.next();
							try {
								clearingerFactory.getClearinger(order).clear(order);
							} catch (Exception ex) {
								log.error(messageSource.getMessage(
										"batch_clear_error",
										new Object[] { 
											departmentEntity.getDepartmentName(),
											MiscUtil.dateToString(clearingEntity.getClearDate()) },
										Locale.getDefault()), ex);
								log.error("订单信息：\n" + order);
								throw new PeRuntimeException(ex);
							}
						}
						return null;
					}
				});
				
				if (orderList.size() < queryCount) {
					endFlag = true;
				}
			}
		}
				
		
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}

	public void setClearingerFactory(ClearingerFactory clearingerFactory) {
		this.clearingerFactory = clearingerFactory;
	}
	
}
