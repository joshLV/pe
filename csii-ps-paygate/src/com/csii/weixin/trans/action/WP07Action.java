package com.csii.weixin.trans.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactory;
import com.csii.pp.dict.Dict;
import com.csii.pp.dict.ErrorConstants;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;

public class WP07Action extends AbstractAction{
	
	private String localPath;
	
	private WxUtil wxUtil;
	
	 private TransformerFactory transformerFactory;

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub

		String merchantId = ctx.getString("merchantId");
		String agentId = ctx.getString("agentId");
		String subMerchantId = ctx.getString("subMerchantId");
		String clearDate = ctx.getString("clearDate");
		String fileName = "";
		Map  dataMap = new HashMap();
		dataMap.put("ClearDate", clearDate);
		if(MiscUtil.isNullOrEmpty(clearDate)){
			throw new PeException("E00035");
		}
		if(!MiscUtil.isNullOrEmpty(subMerchantId)){
			fileName =  new StringBuffer(localPath)
//					.append(clearDate)
//					.append("/")
					.append(subMerchantId).append("_").append(clearDate).append(".txt").toString();
			dataMap.put("SubMerchantId", subMerchantId);
		}else if(!MiscUtil.isNullOrEmpty(merchantId)&&MiscUtil.isNullOrEmpty(subMerchantId)){
			fileName =  new StringBuffer(localPath)
//					.append(clearDate)
//					.append("/")
					.append(merchantId).append("_").append(clearDate).append(".txt").toString();
			dataMap.put("MerchantId", merchantId);
		}else if(!MiscUtil.isNullOrEmpty(agentId)&&MiscUtil.isNullOrEmpty(merchantId)&&MiscUtil.isNullOrEmpty(subMerchantId)){
			fileName =  new StringBuffer(localPath)
//					.append(clearDate)
//					.append("/")
					.append(agentId).append("_").append(clearDate).append(".txt").toString();
			dataMap.put("AgentId", agentId);
		}else if(MiscUtil.isNullOrEmpty(agentId)&&MiscUtil.isNullOrEmpty(merchantId)&&MiscUtil.isNullOrEmpty(subMerchantId)){
			throw new PeException("E00036");
		}
        //准备数据
        byte[] data = null;
        List merTransList = this.getSqlMap().queryForList("pp.core.queryOrderListForCheckingFileForMer", dataMap);
            log.debug(merTransList);
            for(Iterator it = merTransList.iterator();it.hasNext();){
            	Map transData = (Map)it.next();
            	String transType = (String)transData.get("TransType");
            	String transAmt = String.valueOf(transData.get("TransAmt"));
            	String transAmt3 = String.valueOf(transData.get("TransAmt3"));
            	if("00".equals(transType)){
            		transData.put("TransAmt", transAmt3);
            	}else{//01 02 退货，撤销
            		transData.put("TransAmt", transAmt);
            	}
            	Map para = new HashMap();
            	para.put("sub_mch_id", transData.get("SubMerId"));
            	para.put("mer_id", transData.get("MerId"));
            	para.put("AgentId", transData.get("AgentId"));
            	Merchant submerinfo = (Merchant)this.getSqlMap().queryForObject("pp.core.qryParentMerId", para);
            	transData.put("SubMerName", submerinfo.getName());
            	if(!MiscUtil.isNullOrEmpty(transData.get("MerId"))){
            		Merchant merinfo = (Merchant)this.getSqlMap().queryForObject("pp.core.qryMerId", para);
            		transData.put("MerName", merinfo.getName());
            	}
            	if(!MiscUtil.isNullOrEmpty(transData.get("AgentId"))){
            		Map agentinfo = (Map)this.getSqlMap().queryForObject("pp.core.queryAgentName", para);
                	transData.put("AgentName", agentinfo.get("agtName"));
            	}
            
            }
            //拼接对账文件内容
            Map result = new HashMap();
            result.put("ReportData", merTransList);
            result.put("TotalCount", String.valueOf(merTransList.size()));
            OutputStream outputStream = null;
            try {
                Transformer transformer = transformerFactory.getTransformer("merchantCheckingFile");
                data = (byte[]) transformer.format(result, null);
                outputStream = new FileOutputStream(new File(fileName));
                outputStream.write(data);
                outputStream.flush();
            } catch (Exception ex) {
                log.error("格式化对账文件异常", ex);
                throw new PeException(ErrorConstants.SETTFILE_PROCESS_ERROR);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException ex) {
                        log.error("关闭文件流异常", ex);
                    }
                }
            }

           
        Map map = new HashMap();
		try {
			map.put("DownloadFileContent", new String((byte[]) data,"GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("文件内容"+map.get("DownloadFileContent"));
//        map.put("DownloadFileName", fileName);// 文件名不加路径
//        map.put("DownloadFileType", "txt");
        map.put("ReturnCode", "000000");
        map.put("ReturnMsg", "交易成功");

		ctx.setData("json", map);
        
//if ("xml".equals(ctx.getData("_XML_FLAG_"))){
//			
//			StringBuffer sb = new StringBuffer("SettFile=").append(new String((byte[])map.get("DownloadFileContent")));
//			String plain = sb.toString();
//		//	String signature = payGateSignVerify.signPaygateData(plain);
//			ctx.setData("Plain", plain);
//			
//			try {
//				String plain =ctx.getString("Plain");
//				String signature = WxUtil.
//				String signatureString = signature.signByMD5withRSA(plain, privateKey);
//				
//				//ctx.setData("transName", ctx.getTransactionId());
//				//ctx.setData(Dict.PPPLAIN, plain);
//				ctx.setData(Dict.PPSIGNATURE, signatureString);
//				
//			} catch (Exception ex) {
//				throw new PeException(ErrorConstants.SIGN_ERROR);
//			}
//			
//			String signature = ctx.getString("Signature");
//			
//			Map newmap = new HashMap();
//			newmap.put(Dict.PPTRANSNAME, ctx.getTransactionId());
//			newmap.put(Dict.PPPLAIN, plain);
//			newmap.put(Dict.PPSIGNATURE, signature);	
//			
//			ctx.setDataMap(newmap);
//				
//		}else {
//			ctx.setDataMap(map);
//
//		}
    
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public void setWxUtil(WxUtil wxUtil) {
		this.wxUtil = wxUtil;
	}

	public void setTransformerFactory(TransformerFactory transformerFactory) {
		this.transformerFactory = transformerFactory;
	}
	
	
}
