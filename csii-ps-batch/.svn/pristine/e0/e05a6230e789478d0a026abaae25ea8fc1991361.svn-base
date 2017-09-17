package com.csii.batch.action;

import java.util.Locale;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.entity.BusinessEntity;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.util.MiscUtil;

public class CutOffAction extends AbstractBatchAction{

	public void execute(Context context) throws PeException {
		// TODO Auto-generated method stub
		ClearingEntity clearingEntity = (ClearingEntity) context.getData("ClearingEntity");
		BusinessEntity businessEntity = (BusinessEntity) context.getData("BusinessEntity");
		DepartmentEntity departmentEntity = (DepartmentEntity) context.getData("DepartmentEntity");
		
        log.info(departmentEntity.getDepartmentName() + "日切开始，前一清算日期[" + MiscUtil.dateToString(clearingEntity.getClearDate()) + "]");
		
		try {
			// 如果业务参数表中的前一清算日期大于清算控制表中清算日期就进行日切处理
			if (businessEntity.getPreClearDate().compareTo(clearingEntity.getClearDate()) > 0) {
				// 修改支付网关清算控制表，每次只滚动一天
				clearingEntity.setClearDate(MiscUtil.rolDate(clearingEntity.getClearDate(), -1));
				clearingEntity.setProcDate(MiscUtil.getCurrentDate());
				clearingEntity.update();
			} else {
				return;
			}
		} catch (Exception ex) {
			throw new PeException(messageSource.getMessage("batch_cutoff_error",
					new Object[] { MiscUtil.dateToString(clearingEntity.getClearDate()), departmentEntity.getDepartmentName() },
					Locale.getDefault()), ex);
		}
		log.info(departmentEntity.getDepartmentName() + "日切结束，当前清算日期[" + MiscUtil.dateToString(clearingEntity.getClearDate()) + "]");
	}
	

}
