/*
 * MerchantSendAction.java Created on 2007-11-18 14:17:13
 * 
 * Copyright 2004 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.weixin.notify.action;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.channel.http.servlet.ObjectMappingCustomer;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author tian_h_m
 * 
 *
 */

public class MerchantSendAction extends AbstractAction {
	
	private int maxSendTimes = 5;
	
	public static void main(String[] args) {
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Context ctx) throws PeException {
		//根据商户号查询key内容及通知url信息，url有则通知。
		Order order = (Order) ctx.getVariable();
//		Map amap = new HashMap();
//		amap.put("sub_mch_id", order.getSubMerchantId());
//		Merchant merInfo = (Merchant)this.getSqlMap().queryForObject("pp.core.qryParentMerId",amap);
		Map merKeyMap = (Map)this.getSqlMap().queryForObject("pp.core.queryMerKey",order.getSubMerchantId());
		if (null == merKeyMap|| merKeyMap.isEmpty()){
			//无此keyid信息
			log.info("无商户所属平台信息，不需要通知。订单号为：["+order.getTransSeqNo()+"]");
		}else{
			if(!MiscUtil.isNullOrEmpty(merKeyMap.get("systemResurl"))){
				
				log.info("************************通知商户开始："+order);
				Map merData=new HashMap();
				String keyContent = (String)merKeyMap.get("keyContent");
				merData.put("ReturnCode",Constants.AAAAAAA);
				merData.put("ReturnMsg","交易成功");
				merData.put("TransMerSeqNo",MiscUtil.toStringAndTrim(order.getMerSeqNo()));
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				String dateTime=sdf.format((Date)order.getTransDateTime()); 
				merData.put("TransDateTime",dateTime);
				merData.put("HostSeqNo",MiscUtil.toStringAndTrim(order.getHostSeqNo()));
				merData.put("SubMerId",MiscUtil.toStringAndTrim(order.getSubMerchantId()));
				merData.put("DepartmentId",MiscUtil.toStringAndTrim(order.getDepartmentId()));
				merData.put("PayType",MiscUtil.toStringAndTrim(order.getPayType()));
				merData.put("Trans_Status",MiscUtil.toStringAndTrim(order.getStatus()));
				//金额注意
				merData.put("Amt",order.getAmount());
				merData.put("Amt1",order.getAmount1());
				merData.put("Amt3",order.getAmount3());
				merData.put("OrgMerSeqNo",MiscUtil.toStringAndTrim(order.getOrgMerSeqNo()));
				merData.put("OrgHostSeqNo",MiscUtil.toStringAndTrim(order.getOrgHostSeqNo()));
				//datetostring
				String orgmerdatetime = "";
				if(order.getOrgMerTransDate()!=null){
					orgmerdatetime=sdf.format((Date)order.getOrgMerTransDate());
				}
				merData.put("OrgDateTime",orgmerdatetime);
				merData.put("sign", MiscUtil.sign(merData, keyContent));

				JSONObject json = JSONObject.fromObject(merData); 
				String resurl = (String)merKeyMap.get("systemResurl");
				log.info("************后台通知地址"+resurl);
				HttpClient client = new DefaultHttpClient();
//				client.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
//			    client.getHttpConnectionManager().getParams().setSoTimeout(3000);
				HttpPost post = new HttpPost(resurl);  
				JSONObject response = null;  
				try {  
				    StringEntity s = new StringEntity(json.toString());  
				    s.setContentEncoding("UTF-8");  
				    s.setContentType("application/json");  
				    post.setEntity(s);  
				    HttpResponse res = client.execute(post);  
				    if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
//				        String result = EntityUtils.toString(res.getEntity());// 返回json格式：
//				        response = JSONObject.fromObject(result);
				    }
				    //设置超时时间。
				} catch (Exception e) { 
					log.info("交易流水["+ MiscUtil.toStringAndTrim(merData.get("TransMerSeqNo"))+ "],交易日期["+ merData.get("TransDateTime")+ "],通知商户失败");
				    throw new RuntimeException(e);  
				}  
				log.info("merchant return data is:"+response);
				log.info("*******************通知商户结束："+order);
			}
		}
	}

	/** 记录商户通知表 */
	private void recordMerchantNotify(Map data, Integer sendTimes) {
		data.put("Plain",data.get("Plain").toString().getBytes());
		data.put("Signature",data.get("Signature").toString().getBytes());
//		if("0".equals(data.get(Dict.ISNEEDRECORD))){
//			return;
//		}
//		if (sendTimes.intValue() == 0) {
//			data.put(Dict.SENDTIMES, new Integer(sendTimes.intValue() + 1));
//			this.getSqlMap().insert("pp.online.insertMerchantNotifyInfo", data);
//		} else if (sendTimes.intValue() < this.maxSendTimes) {
//			data.put(Dict.SENDTIMES, new Integer(sendTimes.intValue() + 1));
//			this.getSqlMap().update("pp.online.updateMercahntNotifyInfo", data);
//		} else {
//			this.getSqlMap().delete("pp.online.deleteMerchantNotiryInfo", data);
//		}
	}
}
