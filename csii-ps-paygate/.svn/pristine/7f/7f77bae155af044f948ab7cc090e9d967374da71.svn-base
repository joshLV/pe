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
public class WeixinNotifyContextResolver extends ApplicationObjectSupport implements ContextResolver {

	private static Logger logger = LoggerFactory.getLogger(WeixinNotifyContextResolver.class);

	private String defaultTransaction;

	private TransformerFactory transformerFactory;

	private String parserResolverName;

	private String transName = "trans_id";

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
		byte[] data = read(context, request, locale);
		parse(context, data, request, locale);

	}

	/**
	 * 将请求数据读取至字节数组
	 */
	private byte[] read(Context context, HttpServletRequest request, Locale locale) throws PeException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int bufferSize = 1024;
		byte[] result = null;

		try {
			InputStream is = request.getInputStream();
			byte[] bytes = new byte[bufferSize];
			int i = 0;
			while ((i = is.read(bytes)) > 0) {
				baos.write(bytes, 0, i);
			}
			result = baos.toByteArray();
			if (result == null) {
				throw new PeException("读取报文为空");
			}
			return result;
		} catch (IOException ex) {
			throw new PeException("读取报文异常", ex);
		} finally {
			try {
				baos.close();
			} catch (IOException ex) {
				throw new PeException("读取报文异常", ex);
			}
		}
	}

	/**
	 * 将字符数组解析为context
	 */
	private void parseJson(Context context, byte[] data, HttpServletRequest request, Locale locale) throws PeException {
		logger.info("process begin.");
		try {
			String reqJsonStr = new String(data);

			logger.info("REQUEST MESSAGE:" + reqJsonStr);

			Map<String, Object> reqJsonMap = new ObjectMapper().readValue(reqJsonStr, Map.class);

			if (checkTransId(reqJsonMap)) {

				String transId = (String) reqJsonMap.get(transName);

				logger.info("START TRANS : {}", transId);

				context.setTransactionId(transId);

				logger.info("=========TRANS：[{}]------REQUEST JSON PARAM BEGIN===============", transId);
				for (Map.Entry entry : reqJsonMap.entrySet()) {
					logger.info("==>> {}:{}", entry.getKey(), entry.getValue());
				}
				logger.info("=========TRANS：[{}]======REQUEST JSON PARAM END=================", transId);

				context.setDataMap(reqJsonMap);

				String remoteIp = getRemortIP(request);
				logger.info("=========TRANS：[{}]------REMOTE IP：[{}]===============", transId, remoteIp);
				context.setData("spbillCreateIp", remoteIp);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new PeException(e);
		}
	}

	private Boolean checkTransId(Map reqMap) throws PeException {
		Boolean flag = false;

		if (reqMap.containsKey(transName)) {

			String transId = (String) reqMap.get(transName);

			if ((transId != null) && !"".equals(transId.trim())) {
				flag = true;
			} else {
				logger.error("请求的JSON数据中交易码[{}]为空.", transName);
			}
		} else {
			logger.error("请求的JSON数据中不存在交易码[{}].", transName);
		}
		return flag;
	}

	/**
	 * 将字符数组解析为context
	 */
	private void parse(Context context, byte[] data, HttpServletRequest request, Locale locale) throws PeException {
		String message = null;

		try {
			message = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new PeException("转换字符串异常", ex);
		}
		logger.info("REQUEST MESSAGE:" + message);
		if (MiscUtil.isNullOrEmpty(message)) {
			throw new PeException("报文为空");
		}

		Transformer transformer = transformerFactory.getTransformer(parserResolverName);
		Map requestData = (Map) transformer.parse(new ByteArrayInputStream(data), null);

		logger.info("微信支付结果通知数据:" + requestData);

		if (requestData == null) {
			throw new PeException("报文转换map为空");
		}

		context.setTransactionId("PRNT");

		context.setData("wxNotifyMap", requestData);
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

	public void setDefaultTransaction(String defaultTransaction) {
		this.defaultTransaction = defaultTransaction;
	}

	public void setTransformerFactory(TransformerFactory transformerFactory) {
		this.transformerFactory = transformerFactory;
	}

	public void setParserResolverName(String parserResolverName) {
		this.parserResolverName = parserResolverName;
	}

}
