package com.csii.weixin.mgmt.action;

import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

public class QryCustomerServiceAction extends AbstractAction{

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Map map = (Map) this.getSqlMap().queryForObject("pp.core.queryCustomerService");
		map.put("ReturnCode", "000000");
		map.put("ReturnMsg", "交易成功");
		ctx.setData("json", map);
	}

}
