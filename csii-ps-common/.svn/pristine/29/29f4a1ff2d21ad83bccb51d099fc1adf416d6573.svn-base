package com.csii.pp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.alipay.api.DefaultAlipayClient;
import com.csii.pe.config.support.SqlMapAware;
import com.csii.pp.util.MiscUtil;

public class AlipayClientManager implements SqlMapAware{
	private SqlMapClientOperations sqlMap;
	private List bankList;
	private List bankList1;
	private DefaultAlipayClient alipayClient;
	private Map alipayClientMap = new HashMap();
	private String url;
	public void init() {
		bankList = this.sqlMap.queryForList("pp.core.queryAllMerBankParameter","alipay");
		for(Iterator it = bankList.iterator();it.hasNext();){
			Map map = (Map) it.next();
			if(!MiscUtil.isNullOrEmpty(map.get("alipayAppId"))){
				alipayClient = new DefaultAlipayClient(url, (String)map.get("alipayAppId"), (String)map.get("alipayBankPriKey"), "json", "utf-8", (String)map.get("alipayPubKey"), "RSA2");
				alipayClientMap.put(map.get("alipayAppId"), alipayClient);
			    
			}
			
		}
		
		bankList1 = this.sqlMap.queryForList("pp.core.queryAllMerBankParameter","mybank");
		for(Iterator it = bankList1.iterator();it.hasNext();){
			Map map = (Map) it.next();
			if(!MiscUtil.isNullOrEmpty(map.get("alipayAppId"))){
				alipayClient = new DefaultAlipayClient(url, (String)map.get("alipayAppId"), (String)map.get("alipayBankPriKey"), "json", "utf-8", (String)map.get("alipayPubKey"), "RSA2");
				alipayClientMap.put(map.get("alipayAppId"), alipayClient);
			    
			}
			
		}
	}

	public DefaultAlipayClient getAlipayClient(String appid) {
		return (DefaultAlipayClient) alipayClientMap.get(appid);
	}

	public void setAlipayClient(DefaultAlipayClient alipayClient) {
		this.alipayClient = alipayClient;
	}

	@Override
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}

	public Map getAlipayClientMap() {
		return alipayClientMap;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	

}
