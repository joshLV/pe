package com.csii.batch.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.csii.pe.action.Executable;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;

public class LoadHostFileAction extends AbstractBatchAction{
	
	private Map actionMap;

	public void execute(Context context) throws PeException {
		// TODO Auto-generated method stub
		DepartmentEntity departmentEntity = (DepartmentEntity) context.getData("DepartmentEntity");
		Executable action = (Executable) actionMap.get(departmentEntity.getDepartmentType());
		action.execute(context);
	}

	public void setActionMap(Map actionMap) {
		this.actionMap = actionMap;
	}

}
