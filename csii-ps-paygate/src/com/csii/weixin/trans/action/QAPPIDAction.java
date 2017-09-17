package com.csii.weixin.trans.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.merchant.Merchant;

public class QAPPIDAction extends AbstractAction{

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		String mchToken = ctx.getString("merToken");
		Map m = new HashMap();
		m.put("mchToken", mchToken);
		Merchant merchant = (Merchant) this.getSqlMap().queryForObject("pp.core.qryMerInfoByToken", m);
		if (merchant == null) {
			log.error("根据商户[TOKEN:"+mchToken+"]无法查找到商户");
			throw new PeException("E00003");
		}
		List MerBankParameter = (List) this.getSqlMap().queryForList("pp.core.queryMerBankParameterByDepartmentId", merchant.getMerBankId());
		Map map = new HashMap();
		for(Iterator it =MerBankParameter.iterator();it.hasNext(); ){
			Map MerBankParameterMap = (Map) it.next();
			if("weixin".equals(MerBankParameterMap.get("departmentId"))){
				map.put("APPID", MerBankParameterMap.get("appId"));
			}else if("alipay".equals(MerBankParameterMap.get("departmentId"))){
				map.put("ALIPAYAPPID", MerBankParameterMap.get("alipayAppId"));
				map.put("departmentId", "alipay");
			}else if("mybank".equals(MerBankParameterMap.get("departmentId"))){
				map.put("ALIPAYAPPID", MerBankParameterMap.get("alipayAppId"));
				map.put("departmentId", "mybank");
			}
		}
		ctx.setData("json", map);
	}

}
