/*
 * @(#)AbstractLoadHostCheckingFileAction.java	1.0 2009-12-28 上午11:01:49
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.batch.action;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.context.NoSuchMessageException;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactory;
import com.csii.pp.dict.Dict;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.order.Order;
import com.csii.pp.sftp.SFTPFileTransfer;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;

/**
 * AbstractLoadHostCheckingFileAction.java
 *
 * @author Cuiyi
 * <p>
 *   Created on 2009-12-28
 *   Modification history
 * </p>
 * <p>
 *   IBS Product Expert Group, CSII
 *   Powered by CSII PowerEngine 6.0
 * </p>
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractLoadHostCheckingFileAction extends AbstractBatchAction {
	
	private int retryTimes;

	private long delayTime;
	
	private String fileDescription;
	
	private TransformerFactory transformerFactory;
	
	private WxUtil wxUtil;
	
	private SFTPFileTransfer sftpFileTransfer;
	
	private String localPath;
	
	private String downloadPath;
	
	private String downloadPathForALi;
	
	
	private String url;

	/* (non-Javadoc)
	 * @see com.csii.pp.action.AbstractAction#execute(com.csii.pe.core.Context)
	 */
	public void execute(Context ctx) throws PeException {
		ClearingEntity clearingEntity = (ClearingEntity) ctx.getData("ClearingEntity");
		DepartmentEntity departmentEntity = (DepartmentEntity) ctx.getData("DepartmentEntity");
		//删除核心交易明细表中当前清算日期的内容
//		getSqlMap().delete("pp.batch.deleteHostHistoryByDeptId", departmentEntity.getDepartmentId());
		byte[] bytea =null;
		if("weixin".equals(departmentEntity.getDepartmentId())){
			bytea = download(clearingEntity,departmentEntity);
		}else if("mybank".equals(departmentEntity.getDepartmentId())){
			bytea = downloadForMybank(clearingEntity,departmentEntity);
		}else if("alipay".equals(departmentEntity.getDepartmentId())){
			bytea = downloadForAlipay(clearingEntity,departmentEntity);
		}
	}
	
	/**
	 * 下载对账文件
	 *
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	private byte[] download(ClearingEntity clearingEntity,DepartmentEntity departmentEntity) throws PeException {
		int retry = 0;
		byte[] bytea = null;
			
		boolean result = true;
			//循环获取对账文件
			
		List bankList = getSqlMap().queryForList("pp.core.queryAllMerBankParameter",departmentEntity.getDepartmentId());
		for (Iterator it = bankList.iterator(); it.hasNext();) {
			Map bankMap = (Map) it.next();
			Map send = new HashMap();
			send.put("appid", bankMap.get("appId"));
			send.put("mch_id", bankMap.get("merId"));
			send.put("nonce_str", MiscUtil.getRandom());
			send.put("bill_date", MiscUtil.dateToString(clearingEntity.getClearDate()));
			send.put("bill_type", "ALL");
			String str1 = MiscUtil.sign(send, (String) bankMap.get("paysignkey"));

			send.put("sign", str1);

			String xmlData = wxUtil.parseXML(send);
			while (true) {
				retry++;
				log.info(
						messageSource
								.getMessage("batch_down_hosthistory_begin",
										new Object[] { MiscUtil.dateToString(clearingEntity.getClearDate()),
												new Integer(retry), departmentEntity.getDepartmentName() },
										Locale.getDefault()));
				bytea = wxUtil.httpSend(xmlData.getBytes(), "https://api.mch.weixin.qq.com/pay/downloadbill");
				String str = "";
				try {
					str = new String(bytea, "UTF-8");
					log.info(str);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.info(str);
				if(!MiscUtil.isNullOrEmpty(str)){
					if(str.startsWith("<xml")){
						result =false;
						Map xmap = XmlUtil.toMap(str, "xml");
						if("No Bill Exist".equals(xmap.get("return_msg"))){
							result =true;
							log.info("清算日期[" + MiscUtil.dateToString(clearingEntity.getClearDate()) + "]" + departmentEntity.getDepartmentName() + "没有已成交的订单");
						}
					}else{

			            List transDetailList = parse(bytea,clearingEntity,departmentEntity);
						
						try {
							for (Iterator iter = transDetailList.iterator(); iter.hasNext();) {
								Map transDetail = (Map) iter.next();
								transDetail.put("DepartmentId", departmentEntity.getDepartmentId());
								transDetail.put("ClearDate", clearingEntity.getClearDate());
								transDetail.put("BankId", bankMap.get("bankId"));
								load(transDetail);
							}
						} catch (Exception ex) {
							throw new PeException(messageSource.getMessage(
								"batch_hosthistory_insert_error",
										new Object[] {
												MiscUtil.dateToString(clearingEntity.getClearDate()),
												departmentEntity.getDepartmentName()},
										Locale.getDefault()),ex);
						}
						
						log.info(messageSource.getMessage("batch_hosthistory_insert_success",
								new Object[] {
									MiscUtil.dateToString(clearingEntity.getClearDate()),
									departmentEntity.getDepartmentName(),
									new Integer(transDetailList.size()) },
								Locale.getDefault()));
					
						result =true;
					}
				}else{
					result = false;
				}
				if (result) {
					log.info(messageSource.getMessage("batch_down_hosthistory_success",
							new Object[] {
								MiscUtil.dateToString(clearingEntity.getClearDate()),
								new Integer(retry),
								departmentEntity.getDepartmentName() },
							Locale.getDefault()));
					break;
				} else {
					if (retry >= retryTimes) {
						throw new PeException(
							messageSource.getMessage(
								"batch_down_hosthistory_error",
								new Object[] {
										MiscUtil.dateToString(clearingEntity.getClearDate()),
										new Integer(retry), departmentEntity.getDepartmentName()},
								Locale.getDefault()));
					} else {
						try {
							Thread.sleep(delayTime * 1000);
						} catch (Exception ee) {
							log.error("thread interrupted exception", ee);
						}
						continue;
					}
				}
			}
		}
		return bytea;
	}
	
	
	
	/**
	 * 解析对账文件
	 *
	 * @param bytea
	 * @return
	 * @throws PeException
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	private List parse(byte[] bytea,ClearingEntity clearingEntity,DepartmentEntity departmentEntity) throws PeException {
		Map data = null;
		InputStream inputStream = null;
		BufferedReader reader = null;
		List transDetailList = new ArrayList();
		try {
			inputStream = new ByteArrayInputStream(bytea);
			reader = new BufferedReader(new InputStreamReader(inputStream));
			Transformer transformer = transformerFactory.getTransformer(fileDescription);
			String line ="";
			boolean isWillLast = false;//标记倒数第二行
			boolean isFirst = true;//标记第一行
			while (true) {
				line = reader.readLine();
				if (line == null) {
					break;
				}else
				{
					line = line.replace("`", "");
				}
				if(isFirst)//第一行
				{
					isFirst = false;
					log.info("第一行："+line);
					continue;
				}
				if(isWillLast)//最后一行
				{
					  log.info("最后一行："+line);
					  break;
				}
				if(line.startsWith("总交易单数"))
				{
					isWillLast = true;
					log.info("倒数第二行："+line);
					continue;
				}
				//正常交易列表
				log.info(line);
				data = (Map) transformer.parse(line.getBytes(), null);
				transDetailList.add(data);
			}	
		} catch (Exception ex) {
			log.error("解析对账文件异常", ex);
			throw new PeException(
					messageSource.getMessage(
						"batch_hosthistory_parse_error",
						new Object[] {
							MiscUtil.dateToString(clearingEntity.getClearDate()),
							departmentEntity.getDepartmentName(), null},
						Locale.getDefault()));
		}
		if (data == null) {
			throw new PeException(
					messageSource.getMessage(
						"batch_hosthistory_parse_error",
						new Object[] {
							MiscUtil.dateToString(clearingEntity.getClearDate()),
							departmentEntity.getDepartmentName(), null },
						Locale.getDefault()));
		}
		
		
		
		if (transDetailList.isEmpty()||transDetailList.size()==0) {
			log.warn("清算日期["
					+ MiscUtil.dateToString(clearingEntity.getClearDate())
					+ "]，" + departmentEntity.getDepartmentName()
					+ "没有交易明细");
		}
		
		return transDetailList;
	}
	/** 
	 * 从输入流中获取字节数组 
	 * @param inputStream 
	 * @return 
	 * @throws IOException 
	 */  
	public  byte[] readInputStream(InputStream inputStream) throws IOException {    
	    byte[] buffer = new byte[1024];    
	    int len = 0;    
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();    
	    while((len = inputStream.read(buffer)) != -1) {    
	        bos.write(buffer, 0, len);    
	    }    
	    bos.close();    
	    return bos.toByteArray();    
	}    
	/**
	 * 
	 * @param urlStr	请求url
	 * @param file		保存路径
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private String downLoadAlipay(String urlStr,String fileStr) throws MalformedURLException, IOException, FileNotFoundException {
		URL url = new URL(urlStr);    
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
		//设置超时间为3秒  
		conn.setConnectTimeout(30*1000);  
		//防止屏蔽程序抓取而返回403错误  
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  

		//得到输入流  
		InputStream inputStream = conn.getInputStream();    
		//获取自己数组  
		byte[] getData = readInputStream(inputStream);      

		//文件保存位置  
		File saveDir = new File(fileStr);  
		if(!saveDir.exists()){  
			saveDir.mkdir();  
		}  
		String file2= saveDir+"/"+MiscUtil.dateToDayString(MiscUtil.rolDate(new Date(System.currentTimeMillis()), 1))+".zip";
		File file = new File(file2);      
		FileOutputStream fos = new FileOutputStream(file);       
		fos.write(getData);   
		if(fos!=null){  
			fos.close();    
		}  
		if(inputStream!=null){  
			inputStream.close();  
		}  

		log.info("info:"+url+" download success");
		return file2;
	}
	
	
	private byte[] downloadForAlipay(ClearingEntity clearingEntity,DepartmentEntity departmentEntity) throws PeException{
		int retry = 0;
		byte[] bytea = null;
		boolean result = true;
		//循环获取对账文件
		List bankList = getSqlMap().queryForList("pp.core.queryAllMerBankParameter",departmentEntity.getDepartmentId());
		for (Iterator it = bankList.iterator(); it.hasNext();) {
			Map bankMap = (Map) it.next();
//			"https://openapi.alipaydev.com/gateway.do"
			AlipayClient alipayClient = new DefaultAlipayClient(url,
					bankMap.get("alipayAppId").toString(),
					bankMap.get("alipayBankPriKey").toString().trim(),
					"json","GBK",
					bankMap.get("alipayPubKey").toString().trim(),"RSA2");
//			System.out.println(bankMap.get("appId").toString());//存放appid
//			System.out.println(bankMap.get("alipayBankPriKey").toString().trim());//存放自己的私钥
//			System.out.println(bankMap.get("alipayPubKey").toString().trim());//存放阿里的公钥
			AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
			request.setBizContent("{" +
			"\"bill_type\":\"trade\"," +
			"\"bill_date\":\"" +MiscUtil.dateToDayString(MiscUtil.rolDate(new Date(System.currentTimeMillis()), 1))+ "\""+
			"  }");
			AlipayDataDataserviceBillDownloadurlQueryResponse response;
			try {
				response = alipayClient.execute(request);
				if(response.isSuccess()){
					log.info("获取对账url调用成功，下载对账文件开始");
					//压缩包全路径
					String zipfile="";
					//zip解压文件路径
					String unzipPath=downloadPathForALi+MiscUtil.dateToDayString(MiscUtil.rolDate(new Date(System.currentTimeMillis()), 1));
					//下载对账文件
					try {
						zipfile=downLoadAlipay(response.getBillDownloadUrl(),unzipPath);
					} catch (Exception e) {
						log.error(e);
						throw new PeException("下载对账文件失败");
					} 
					log.info("下载对账文件结束，解压对账文件开始");
					ZipUtil.upzipFile(zipfile, unzipPath);
					log.info("解压对账文件结束，数据入库开始");
					if(result){
						List transDetailList = parseForAlipay(unzipPath,clearingEntity,departmentEntity);
						
						try {
							for (Iterator iter = transDetailList.iterator(); iter.hasNext();) {
								Map transDetail = (Map) iter.next();
								transDetail.put("DepartmentId", departmentEntity.getDepartmentId());
								transDetail.put("ClearDate", clearingEntity.getClearDate());
								transDetail.put("BankId", bankMap.get("bankId"));
								load(transDetail);
							}
						} catch (Exception ex) {
							throw new PeException(messageSource.getMessage(
								"batch_hosthistory_insert_error",
										new Object[] {
												MiscUtil.dateToString(clearingEntity.getClearDate()),
												departmentEntity.getDepartmentName()},
										Locale.getDefault()),ex);
						}
						
						log.info(messageSource.getMessage("batch_hosthistory_insert_success",
								new Object[] {
									MiscUtil.dateToString(clearingEntity.getClearDate()),
									departmentEntity.getDepartmentName(),
									new Integer(transDetailList.size()) },
								Locale.getDefault()));
					}
				} else {
					log.info("调用失败");
				}
			} catch (AlipayApiException e1) {
				// TODO Auto-generated catch block
				throw new PeException("下载对账文件失败");
			}
		}
		return bytea;
	}
	
	private List parseForAlipay(String downloadPath2,ClearingEntity clearingEntity, DepartmentEntity departmentEntity) throws PeException, NoSuchMessageException {
		Map data = null;
		BufferedReader reader = null;
		List transDetailList = new ArrayList();
		String filePath="";
		try {
			File file = new File(downloadPath2);  
			//获取文件下的所有文件
			File[] array = file.listFiles();
			for(int i=0;i<array.length;i++){
				if(array[i].isFile()){   
	             if(array[i].getPath().endsWith("业务明细.csv")) {
	            	 filePath= array[i].getPath();  
	             	break;
	             }
	            }  
			}
//			InputStream inputStream = new FileInputStream(new File(filePath));
			FileInputStream fis = null;    
		    InputStreamReader isw = null;    
		    BufferedReader br = null;   
			fis = new FileInputStream(new File(filePath));    
	        isw = new InputStreamReader(fis, "GBK");    
	        br = new BufferedReader(isw); 
	        StringBuffer readLine = new StringBuffer();  
			
//			String fileContent = IOUtils.toString(inputStream, "GBK");
//			reader = new BufferedReader(new InputStreamReader(inputStream));
			Transformer transformer = transformerFactory.getTransformer(fileDescription);
			String line ="";
			boolean isFirst =true;
			int readline=0;
			while (true) {
				String strReadLine = br.readLine(); 
//				line = reader.readLine();
				readline++;
				if (MiscUtil.isNullOrEmpty(strReadLine)) {
					break;
				}
				if(readline<6){
					log.info(strReadLine);
					continue;
				}
				if(strReadLine.startsWith("#-----------------------------------------业务明细列表结束"))
				{
					break;
				}
				//正常交易列表
				log.info(strReadLine);
				data = (Map) transformer.parse(strReadLine.getBytes(), null);
				transDetailList.add(data);
			}	
		} catch (Exception ex) {
			log.error("解析对账文件异常", ex);
			throw new PeException(
					messageSource.getMessage(
							"batch_hosthistory_parse_error",
							new Object[] {
									MiscUtil.dateToString(clearingEntity
											.getClearDate()),
									departmentEntity.getDepartmentName(),
									filePath }, Locale.getDefault()));
		}
		//如果当日交易为空的话，此处就会抛出异常，导致交易无法进行
/*		if (data == null) {
			throw new PeException(
					messageSource.getMessage(
							"batch_hosthistory_parse_error",
							new Object[] {
									MiscUtil.dateToString(clearingEntity
											.getClearDate()),
									departmentEntity.getDepartmentName(),
									filePath }, Locale.getDefault()));
		}
*/
		// 没有分隔符
		if (transDetailList.isEmpty()) {
			log.warn("清算日期["
					+ MiscUtil.dateToString(clearingEntity.getClearDate())
					+ "]，" + departmentEntity.getDepartmentName() + "没有交易明细");
		}

		return transDetailList;
	}





	private byte[] downloadForMybank(ClearingEntity clearingEntity,DepartmentEntity departmentEntity) throws PeException {
		
		int retry = 0;
		byte[] bytea = null;
			
		boolean result = true;
			//循环获取对账文件
			
		List bankList = getSqlMap().queryForList("pp.core.queryAllMerBankParameter",departmentEntity.getDepartmentId());
		for (Iterator it = bankList.iterator(); it.hasNext();) {
			Map bankMap = (Map) it.next();
			String isvId = MiscUtil.toStringAndTrim(bankMap.get("mybankIsvOrgId"));
			String bankId = MiscUtil.toStringAndTrim(bankMap.get("bankId"));
			String clearDate = MiscUtil.dateToString(clearingEntity.getClearDate());
			String fileName = isvId + "_" +clearDate+".txt";
			while (true) {
				retry++;
				log.info(
						messageSource
								.getMessage("batch_down_hosthistory_begin",
										new Object[] { MiscUtil.dateToString(clearingEntity.getClearDate()),
												new Integer(retry), departmentEntity.getDepartmentName() },
										Locale.getDefault()));
				try {
					sftpFileTransfer.connect();
					sftpFileTransfer.download(localPath, fileName, downloadPath+isvId+"/"+clearDate+"/");
					
				} catch (PeException e) {
					log.info("文件"+downloadPath+isvId+"/"+clearDate+"/" + fileName+"下载失败:",e);
					result = false;
				}finally{
					sftpFileTransfer.disconnect();
				}
				
				if(result){
					List transDetailList = parse(localPath+fileName,clearingEntity,departmentEntity);
					
					try {
						for (Iterator iter = transDetailList.iterator(); iter.hasNext();) {
							Map transDetail = (Map) iter.next();
							transDetail.put("DepartmentId", departmentEntity.getDepartmentId());
							transDetail.put("ClearDate", clearingEntity.getClearDate());
							transDetail.put("BankId", bankId);
							load(transDetail);
						}
					} catch (Exception ex) {
						throw new PeException(messageSource.getMessage(
							"batch_hosthistory_insert_error",
									new Object[] {
											MiscUtil.dateToString(clearingEntity.getClearDate()),
											departmentEntity.getDepartmentName()},
									Locale.getDefault()),ex);
					}
					
					log.info(messageSource.getMessage("batch_hosthistory_insert_success",
							new Object[] {
								MiscUtil.dateToString(clearingEntity.getClearDate()),
								departmentEntity.getDepartmentName(),
								new Integer(transDetailList.size()) },
							Locale.getDefault()));
				}
				
			     
					
				if (result) {
					log.info(messageSource.getMessage("batch_down_hosthistory_success",
							new Object[] {
								MiscUtil.dateToString(clearingEntity.getClearDate()),
								new Integer(retry),
								departmentEntity.getDepartmentName() },
							Locale.getDefault()));
					break;
				} else {
					if (retry >= retryTimes) {
						throw new PeException(
							messageSource.getMessage(
								"batch_down_hosthistory_error",
								new Object[] {
										MiscUtil.dateToString(clearingEntity.getClearDate()),
										new Integer(retry), departmentEntity.getDepartmentName()},
								Locale.getDefault()));
					} else {
						try {
							Thread.sleep(delayTime * 1000);
						} catch (Exception ee) {
							log.error("thread interrupted exception", ee);
						}
						continue;
					}
				}
			}
		}
		return bytea;
		
	}
	
	private List parse(String fileName,ClearingEntity clearingEntity,DepartmentEntity departmentEntity) throws PeException {
		Map data = null;
		BufferedReader reader = null;
		List transDetailList = new ArrayList();
		try {
			InputStream inputStream = new FileInputStream(new File(fileName));
			String fileContent = IOUtils.toString(inputStream, "UTF-8");
			reader = new BufferedReader(new InputStreamReader(inputStream));
			Transformer transformer = transformerFactory.getTransformer(fileDescription);
			String line ="";
			boolean isFirst =true;
			while (true) {
				line = reader.readLine();
				if (MiscUtil.isNullOrEmpty(line)) {
					break;
				}
				if(isFirst)//第一行
				{
					isFirst = false;
					log.info("第一行："+line);
					continue;
				}
				if(line.startsWith("商户号"))
				{
					log.info("第二行："+line);
					continue;
				}
				//正常交易列表
				log.info(line);
				data = (Map) transformer.parse(line.getBytes(), null);
				transDetailList.add(data);
			}	
		} catch (Exception ex) {
			log.error("解析对账文件异常", ex);
			throw new PeException(
					messageSource.getMessage(
							"batch_hosthistory_parse_error",
							new Object[] {
									MiscUtil.dateToString(clearingEntity
											.getClearDate()),
									departmentEntity.getDepartmentName(),
									fileName }, Locale.getDefault()));
		}
		if (data == null) {
			throw new PeException(
					messageSource.getMessage(
							"batch_hosthistory_parse_error",
							new Object[] {
									MiscUtil.dateToString(clearingEntity
											.getClearDate()),
									departmentEntity.getDepartmentName(),
									fileName }, Locale.getDefault()));
		}

		// 没有分隔符
		if (transDetailList.isEmpty()) {
			log.warn("清算日期["
					+ MiscUtil.dateToString(clearingEntity.getClearDate())
					+ "]，" + departmentEntity.getDepartmentName() + "没有交易明细");
		}

		return transDetailList;
	}
	
	/**
	 * 加载数据
	 *
	 * @param transData
	 * @param fileData
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	abstract public void load(Map transData);


	/**
	 * @param retryTimes the retryTimes to set
	 */
	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}


	/**
	 * @param delayTime the delayTime to set
	 */
	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}

	/**
	 * @param fileDescription the fileDescription to set
	 */
	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}

	/**
	 * @param transformerFactory the transformerFactory to set
	 */
	public void setTransformerFactory(TransformerFactory transformerFactory) {
		this.transformerFactory = transformerFactory;
	}

	public void setWxUtil(WxUtil wxUtil) {
		this.wxUtil = wxUtil;
	}


	public void setSftpFileTransfer(SFTPFileTransfer sftpFileTransfer) {
		this.sftpFileTransfer = sftpFileTransfer;
	}


	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}


	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDownloadPathForALi() {
		return downloadPathForALi;
	}

	public void setDownloadPathForALi(String downloadPathForALi) {
		this.downloadPathForALi = downloadPathForALi;
	}


}
