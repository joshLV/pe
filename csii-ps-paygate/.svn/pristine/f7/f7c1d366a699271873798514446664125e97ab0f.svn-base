package com.csii.weixin.mgmt.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.util.MiscUtil;

public class JYTJAction extends AbstractAction{

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		String cashierId =ctx.getString("cashierId");
		String sub_mch_id =ctx.getString("sub_mch_id");
		String beginDate =ctx.getString("beginDate");
		String endDate =ctx.getString("endDate");
		
		Map map = new HashMap();
		map.put("SubMerId", sub_mch_id);
		map.put("BeginDate", beginDate);
		map.put("EndDate", endDate);
		map.put("CashierId", cashierId);
		map.put("bankId", MiscUtil.toStringAndTrim(ctx.getData("bankId")));

		
		List countList = this.getSqlMap().queryForList("pp.core.queryJYTJCount", map);
		for(Iterator it = countList.iterator();it.hasNext();){
			Map maps = (Map) it.next();
			maps.put("TotalAmount", ((BigDecimal)maps.get("TotalAmount")).toString());
		}
		
		Map json = new HashMap();
		json.put("ReturnCode", Constants.AAAAAAA);
		json.put("ReturnMsg", "交易成功");
		json.put("TotalRecord", countList.size());
		json.put("SendList", countList);
		ctx.setData("json", json);
	}

}
