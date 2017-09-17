/*
 * @(#)AlipayContextResolver.java	Nov 21, 2012
 *
 * Copyright 2004-2012 Client Service International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pe.channel.http.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ApplicationObjectSupport;

import com.csii.pe.channel.http.IdentityResolver;
import com.csii.pe.channel.http.LocalServletContext;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactory;
import com.csii.pp.util.MiscUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author xudong
 *         <p>
 *         Created on Dec 20, 2016 Modification history
 *         </p>
 *         <p>
 *         EPP Product Expert Group, CSII Powered by CSII PowerEngine
 *         </p>
 * @version 1.0
 * @since 1.0
 */
public class AlipayNotifyContextResolver extends ApplicationObjectSupport implements ContextResolver {

	private static Logger logger = LoggerFactory.getLogger(AlipayNotifyContextResolver.class);

	private String transName ;

	/**
	 * 创建context
	 */
	private Context creatConext(String transactionId, HttpServletRequest request, Locale locale) {
		Context context = new LocalServletContext(transactionId, request, locale);
		return context;
	}

	@Override
	public Context resolveContext(HttpServletRequest request, Locale locale, IdentityResolver identityresolver) throws PeException {
		Context context = creatConext(null, request, locale);
		resolveContext(context, request, locale);
		return context;
	}

	protected void resolveContext(Context context, HttpServletRequest request, Locale locale) throws PeException {
	
		
		parseJson(context, request, locale);

	}


	/**
	 * 将字符数组解析为context
	 */
	private void parseJson(Context context,  HttpServletRequest request, Locale locale) throws PeException {
		logger.info("process begin.");
		try {
			Map reqMap = new HashMap();
			
			java.util.Map paras = request.getParameterMap();
			for (java.util.Iterator it = paras.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String[] value = (String[]) paras.get(key);
				reqMap.put(key, value[0]);
				logger.info("REQUEST MESSAGE:"+  key + "=" + value[0]);
			}
			
			transName =(String) reqMap.get("trans_id");
			if(MiscUtil.isNullOrEmpty(transName)) {
				throw new PeException("999999");
			}

			context.setTransactionId(transName);

			logger.info("=========TRANS：[{}]------REQUEST JSON Trans Name is ===============", transName);

			context.setData("aliNotifyMap", reqMap);

			String remoteIp = getRemortIP(request);
			logger.info("=========TRANS：[{}]------REMOTE IP：[{}]===============", transName, remoteIp);
			context.setData("spbillCreateIp", remoteIp);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new PeException(e);
		}
	}



	/**
	 * 获取到客户端IP地址
	 * 
	 * @param request
	 * @return
	 */
	public String getRemortIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public String getTransName() {
		return transName;
	}

	public void setTransName(String transName) {
		this.transName = transName;
	}



}
