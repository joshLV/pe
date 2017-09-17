package com.csii.mybank.trans.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.Transport;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.util.MiscUtil;

public class MybankMerchantAction extends AbstractAction{

    private Transport mybankTransport;
	
	private String mybankurl;
	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		log.info("网商银行机构商户号同步检查开始");
		List mybankMerchantList = this.getSqlMap().queryForList("pp.core.queryMybankMerchantnull");
		if(mybankMerchantList.isEmpty()||mybankMerchantList.size()==0){
			log.info("当前网商银行无需要同步商户");
		}else{
			for(Iterator it = mybankMerchantList.iterator();it.hasNext();){
				Map mybankMerchant =(Map) it.next();
				Merchant merchant = (Merchant) this.getSqlMap().queryForObject("pp.core.queryMerchant", (String)mybankMerchant.get("merId"));
				Map para = new HashMap();
				para.put("departmentId", "mybank");
				para.put("bankId", merchant.getMerBankId());
				Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", para);
				
				Map sendMap = new HashMap();
				sendMap.put("submitURL", mybankurl);
				sendMap.put("Appid", MerBankParameterMap.get("mybankAppId"));
				sendMap.put("Function", "ant.mybank.merchantprod.merchant.register.query");
				sendMap.put("ReqTime", MiscUtil.getCurrentTimeString());
				sendMap.put("ReqMsgId", MiscUtil.getCurrentTimeString());
				sendMap.put("InputCharset", "UTF-8");
				sendMap.put("Reserve", ""); // 保留字段
				sendMap.put("SignType", "RSA"); // 签名方式
				sendMap.put("ReqTimeZone", "UTC+8"); // 报文发起时区
				sendMap.put("IsvOrgId", MerBankParameterMap.get("mybankIsvOrgId"));
				sendMap.put("OrderNo", mybankMerchant.get("mybankOrderNo"));
				sendMap.put("mybankJfPriKey", MerBankParameterMap.get("mybankJfPriKey"));
				sendMap.put("mybankPubKey", MerBankParameterMap.get("mybankPubKey"));
				sendMap.put("_TransName", "MMTB");
				Map result = (Map) this.mybankTransport.submit(sendMap);
				
				if("S".equals(result.get("ResultStatus"))){
					if("1".equals(result.get("RegisterStatus"))){
						mybankMerchant.put("deptMerId", result.get("MerchantId"));
						mybankMerchant.put("mybankOpenState", "1");
						this.getSqlMap().update("pp.core.updateMerchantDeptMerId", mybankMerchant);
						log.info("网商银行商户号同步成功,金服商户号["+mybankMerchant.get("merId")+"],网商银行商户号["+result.get("MerchantId")+"]");
					}else if("2".equals(result.get("RegisterStatus"))){
						mybankMerchant.put("mybankOpenState", "2");
						this.getSqlMap().update("pp.core.updateMerchantDeptMerId", mybankMerchant);
						log.info("网商银行商户入驻结果失败，网商商户号未返回："+result);
					}else{
						log.info("网商银行商户入驻结果审核中，网商商户号未返回："+result);
					}
					
				}else{
					log.info("网商商户入驻接口失败："+result);
				}
			}
		}
		log.info("网商银行机构商户号同步检查结束");
	}
	public void setMybankTransport(Transport mybankTransport) {
		this.mybankTransport = mybankTransport;
	}
	public void setMybankurl(String mybankurl) {
		this.mybankurl = mybankurl;
	}
	
	

}
