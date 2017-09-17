/*
 * AbstractAction.java Created on 2007-8-17 3:44:01
 * 
 * Copyright 2004 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.batch.job.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.csii.ibs.action.AbstractIbsAction;
import com.csii.pe.action.Executable;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactory;

/**
 * 
 * 抽象的交易处理action
 * 
 * @author Xudong
 * 
 *
 */
public abstract class AbstractAction extends AbstractIbsAction implements Executable {
	
	private TransformerFactory transformerFactory;
	
	private String  DataList= "DataList";

	protected static Log log = LogFactory.getLog(AbstractAction.class);

	public AbstractAction() {
		super();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csii.pe.action.Executable#execute(com.csii.pe.core.Context)
	 */
	public abstract void execute(Context ctx) throws PeException;
	
	/**
	 * 解析对账文件
	 *
	 * @param fileName
	 * @param templateName
	 * @return
	 * @throws PeException
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	public List parse(String fileName,String templateName) throws PeException {
		Map data = null;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(fileName));
			String fileContent = IOUtils.toString(inputStream, "GBK");
			if (fileContent != null)
				fileContent = fileContent.trim();
			Transformer transformer = transformerFactory
					.getTransformer(templateName);
			data = (Map) transformer.parse(fileContent.getBytes(), null);
		} catch (Exception ex) {
			log.error("商户对账文件名称["+fileName+"]解析对账文件异常", ex);
			throw new PeException("解析商户对账文件异常");
		}finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					inputStream = null;
				}
			}
		}
		if (data == null) {
			log.error("商户对账文件名称["+fileName+"]商户交易明细文件解析错误");
			throw new PeException("商户交易明细文件解析错误");
		}

		List transDetailList = (List) data.get("DataList");
		if (transDetailList.isEmpty()) {
			log.warn("商户对账文件名称["+fileName+"]没有交易明细");
		}

		return transDetailList;
	}

	public void setTransformerFactory(TransformerFactory transformerFactory) {
		this.transformerFactory = transformerFactory;
	}

	public TransformerFactory getTransformerFactory() {
		return transformerFactory;
	}

	
}


