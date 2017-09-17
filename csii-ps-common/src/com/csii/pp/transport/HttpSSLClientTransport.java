/*
 * @(#)HttpClientTransport.java	1.0 2011-6-29 下午07:54:28
 *
 * Copyright 2004-2010 Client Service International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pp.transport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.csii.pe.service.comm.CommunicationException;
import com.csii.pe.service.comm.Transport;

/**
 * HttpClientTransport.java
 *
 * @author cuiyi
 * <p>
 *   Created on 2011-6-29
 *   Modification history
 * </p>
 * <p>
 *   IBS Product Expert Group, CSII
 *   Powered by CSII PowerEngine 6.0
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class HttpSSLClientTransport implements Transport {
	
	private Log log = LogFactory.getLog(HttpClientTransport.class);
	
	/**
	 * 协议类型，目前支持http与https协议
	 */
	private String protocol;
	
	/**
	 * 目标服务器
	 */
	private String host;
	
	/**
	 * 目标端口
	 */
	private int port;
	
	/**
	 * 目标地址
	 */
	private String target;
	
	/**
	 * 查询参数
	 */
	private String queryString;
	
	/**
	 * 代理
	 */
	private boolean proxy;
	private String proxyHost;
	private int proxyPort;
	private String domain;
	private String userName;
	private String userPassword;
	
	/**
	 * SSL证书容器
	 */
	private String keystoreUrl; //客户端证书JKS
	private String keystorePassword;
	private String truststoreUrl; //服务器证书JKS
	private String truststorePassword;
	
	/**
	 * 请求类型
	 */
	private String contentType;
	

	/* (non-Javadoc)
	 * @see com.csii.pe.service.comm.Transport#submit(java.lang.Object)
	 */
	public Object submit(Object data) throws CommunicationException {
		HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, 100);
		params.setMaxTotalConnections(500);
		connectionManager.setParams(params);
		KeyStore keyStore = null;
		CloseableHttpClient httpclient = null;
		FileInputStream instream = null;
		try {
			keyStore = KeyStore.getInstance("PKCS12");
			instream = new FileInputStream(new File(keystoreUrl));
			keyStore.load(instream, keystorePassword.toCharArray());
			SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, keystorePassword.toCharArray()).build();
		        // Allow TLSv1 protocol only
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
		                sslcontext,
		                new String[] { "TLSv1" },
		                null,
		                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		    httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		        //HttpClients.custom().setConnectionManager(connectionManager);
		        //httpclient ＝ proxy("127.0.0.1", 8087).setConnectionManager(connManager).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("创建httpClient异常！！！",e);
			throw new CommunicationException("pe07");
		}finally{
			if(instream != null){
				try {
					instream.close();
				} catch (Exception e) {
					instream = null;
				}
			}
			
		}
		if (data instanceof byte[]) {
			return send(httpclient, (byte[]) data);
		} else if (data instanceof Map) {
			return send(httpclient, (Map) data);
		} else {
			throw new CommunicationException("pe07");
		}
	}
	
	
	private byte[] send(CloseableHttpClient httpclient, final byte[] bytes) throws CommunicationException {
		URL url = null;
		try {
			 url =  new URL(target);
		} catch (Exception e2) {
			log.error("url 异常");
			throw new CommunicationException("pe07");
		}
		//HttpHost target = new HttpHost("10.10.100.102:8080/mytest", 8080,"http"); 
		HttpHost targetHost = new HttpHost(url.getHost(),url.getPort()==-1?url.getDefaultPort():url.getPort() , "https");
		
		HttpPost method = new HttpPost(target);
		RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(7000).setConnectTimeout(7000)
                .setConnectionRequestTimeout(7000).build();
		method.setConfig(requestConfig);
		
		if(proxy){
			HttpHost httpProxy = new HttpHost(proxyHost, proxyPort);  
			RequestConfig config = RequestConfig.custom().setProxy(httpProxy).build();
			method.setConfig(config);
		}
		CloseableHttpResponse response = null;
		try {
			method.setEntity(new ByteArrayEntity(bytes));
			response = httpclient.execute(targetHost, method);
			//log.info("HTTP STATUS:[" + methods.getStatusLine() + "]");
			if(response == null){
				log.error("HttpClient未收到返回！！！");
				throw new CommunicationException("pe07");
			}
			StatusLine  statusLine =  response.getStatusLine();
			log.info("httpStatus:["+statusLine.getStatusCode()+"]");
			if(HttpStatus.SC_OK ==statusLine.getStatusCode()){
					HttpEntity entity = response.getEntity();
		            byte[] result = IOUtils.toByteArray(entity.getContent());
		//            log.info(IOUtils.toString(entity.getContent()));
		            EntityUtils.consume(entity);
		            return result;
		     }else{
		    	 log.error("HttpClient收到响应，但是状态不正确！！！["+statusLine.getStatusCode()+"]");
		    	 throw new CommunicationException("pe07");
		     }
		} catch (Exception ex) {
			if (ex instanceof CommunicationException) {
				throw (CommunicationException) ex;
			}
			throw new CommunicationException("pe07", ex);
		} finally {
			if(response != null){
				try {
					response.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					response = null;
					log.error("关闭响应留response异常！！",e1);
				}
			}
			if(httpclient!=null){
				try {
					httpclient.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpclient = null;
					log.error("关闭异常！！",e);
				}
			}
			
		}
	}
	
	private byte[] send(CloseableHttpClient httpClient, Map data) throws CommunicationException {
		//PostMethod methods = new PostMethod(target);
		URL url = null;
		try {
			 url =  new URL(target);
		} catch (Exception e2) {
			log.error("url 异常");
			throw new CommunicationException("pe07");
		}
		//HttpHost target = new HttpHost("10.10.100.102:8080/mytest", 8080,"http"); 
		HttpHost targetHost = new HttpHost(target,url.getPort()==-1?url.getDefaultPort():url.getPort() , "https");
		
		HttpPost method = new HttpPost(target);
		//methods.setRequestHeader("Content-Type", contentType);
		RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(7000).setConnectTimeout(7000)
                .setConnectionRequestTimeout(7000).build();
		method.setConfig(requestConfig);
		if(proxy){
			HttpHost httpProxy = new HttpHost(proxyHost, proxyPort);  
			RequestConfig config = RequestConfig.custom().setProxy(httpProxy).build();
			method.setConfig(config);
		}
		CloseableHttpResponse response = null;
		//if (!MiscUtil.isNullOrEmpty(queryString)) {
			//methods.setQueryString(queryString);
		//}
		List paramList = new ArrayList();
		if (data != null && !data.isEmpty()) {
			log.info("REQUEST MESSAGE:" + data);
			
			for (Iterator iterator = data.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				BasicNameValuePair pair = new BasicNameValuePair(key, (String) data.get(key));
				paramList.add(pair);
			}
		}
		try {
			method.setEntity(new UrlEncodedFormEntity(paramList));
			response = httpClient.execute(targetHost, method);
			//log.info("HTTP STATUS:[" + methods.getStatusLine() + "]");
			if(response == null){
				log.error("HttpClient未收到返回！！！");
				throw new CommunicationException("pe07");
			}
//			try {
			StatusLine  statusLine =  response.getStatusLine();
			log.info("httpStatus:["+statusLine.getStatusCode()+"]");
			if(HttpStatus.SC_OK ==statusLine.getStatusCode()){
					HttpEntity entity = response.getEntity();
		            byte[] result = IOUtils.toByteArray(entity.getContent());
		            log.info(IOUtils.toString(entity.getContent()));
		            EntityUtils.consume(entity);
		            return result;
		     }else{
		    	 log.error("HttpClient收到响应，但是状态不正确！！！["+statusLine.getStatusCode()+"]");
		    	 throw new CommunicationException("pe07");
		     }
//            } finally {
//                response.close();
//            }
			//HTTP状态200为正常
//			if (HttpStatus.SC_OK == methods.getStatusCode()) {
//				byte[] result = methods.getResponseBody();
//				
//				log.info("RESPONSE MESSAGE:" + new String(result, "UTF-8"));
//				
//				return result;
//			} else {
//				throw new CommunicationException("pe07");
//			}
		} catch (Exception ex) {
			if (ex instanceof CommunicationException) {
				throw (CommunicationException) ex;
			}
			throw new CommunicationException("pe07", ex);
		} finally {
			if(response != null){
				try {
					response.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					response = null;
					log.error("关闭响应留response异常！！",e1);
				}
			}
			if(httpClient!=null){
				try {
					httpClient.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					httpClient = null;
					log.error("关闭异常！！",e);
				}
			}
			
		}
	}

	private void downloadpost(HttpClient httpClient,String fileName) throws CommunicationException {
		PostMethod methods = new PostMethod(target);
		int BUFFER = 1024;
		//queryString)) {
			methods.setQueryString(queryString);
		//}
		
		try {
			httpClient.executeMethod(methods);
			log.info("HTTP STATUS:[" + methods.getStatusLine() + "]");
			//HTTP状态200为正常
			if (HttpStatus.SC_OK == methods.getStatusCode()) {
				//byte[] result = methods.getResponseBody();
				InputStream in = methods.getResponseBodyAsStream();
				FileOutputStream out = new FileOutputStream(fileName);
				byte[] b = new byte[BUFFER];
	            int len = 0;
	            try{
		            while ((len = in.read(b)) != -1) {
		                out.write(b, 0, len);
		            }
	            }catch(Exception e) {
		        	log.error("下载对账文件失败",e);
		        	throw new CommunicationException("pe07");
		        }finally{
		        	 in.close();
		             out.close();
		        }
	            log.info("下载对账文件成功"+fileName);
			} else {
				throw new CommunicationException("pe07");
			}
		} catch (Exception ex) {
			if (ex instanceof CommunicationException) {
				throw (CommunicationException) ex;
			}
			throw new CommunicationException("pe08", ex);
		} finally {
			methods.releaseConnection();
		}
	}
	
	private byte[] post(HttpClient httpClient, File file) throws CommunicationException {
		PostMethod methods = new PostMethod(target);
		
//		if (!MiscUtil.isNullOrEmpty(queryString)) {
			methods.setQueryString(queryString);
//		}
		
		try {
			Part[] parts = { new FilePart(file.getName(), file) };
			methods.setRequestEntity(new MultipartRequestEntity(parts, methods.getParams()));
			
			httpClient.executeMethod(methods);
			log.info("HTTP STATUS:[" + methods.getStatusLine() + "]");
			
			//HTTP状态200为正常
			if (HttpStatus.SC_OK == methods.getStatusCode()) {
				byte[] result = methods.getResponseBody();
				
				log.info("RESPONSE MESSAGE:" + new String(result, "UTF-8"));
				
				return result;
			} else {
				log.error("httstatus=="+methods.getStatusCode());
				throw new CommunicationException("pe07");
			}
		} catch (Exception ex) {
			if (ex instanceof CommunicationException) {
				throw (CommunicationException) ex;
			}
			throw new CommunicationException("pe07", ex);
		} finally {
			methods.releaseConnection();
		}
	}
	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	
	/**
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * @param proxy the proxy to set
	 */
	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	/**
	 * @param proxyHost the proxyHost to set
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * @param proxyPort the proxyPort to set
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @param userPassword the userPassword to set
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	/**
	 * @param keystoreUrl the keystoreUrl to set
	 */
	public void setKeystoreUrl(String keystoreUrl) {
		this.keystoreUrl = keystoreUrl;
	}

	/**
	 * @param keystorePassword the keystorePassword to set
	 */
	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	/**
	 * @param truststoreUrl the truststoreUrl to set
	 */
	public void setTruststoreUrl(String truststoreUrl) {
		this.truststoreUrl = truststoreUrl;
	}

	/**
	 * @param truststorePassword the truststorePassword to set
	 */
	public void setTruststorePassword(String truststorePassword) {
		this.truststorePassword = truststorePassword;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
