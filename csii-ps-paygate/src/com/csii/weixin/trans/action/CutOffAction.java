package com.csii.weixin.trans.action;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.util.MiscUtil;

public class CutOffAction extends AbstractAction{

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		String departmentId = "weixin";
		DepartmentEntity departmentEntity = (DepartmentEntity) this.getSqlMap().queryForObject(Constants.QUERY_DEPARTMENT_ENTITY_SQL, departmentId);
		departmentEntity.setDepartmentDate(MiscUtil.rolDate(departmentEntity.getDepartmentDate(), -1));
		this.getSqlMap().update("pp.core.updateDepartmentEntity", departmentEntity);
	}

}
