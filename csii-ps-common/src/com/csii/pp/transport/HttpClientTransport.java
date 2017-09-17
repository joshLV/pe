package com.csii.pp.transport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.service.comm.CommunicationException;
import com.csii.pe.service.comm.Transport;
import com.csii.pp.dict.ErrorConstants;
import com.csii.pp.transport.ssl.AuthSSLProtocolSocketFactory;
import com.csii.pp.transport.ssl.EasySSLProtocolSocketFactory;
import com.csii.pp.util.MiscUtil;

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
public class HttpClientTransport implements Transport {
	
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
	private boolean userProxy;
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
		params.setConnectionTimeout(30000);
		params.setSoTimeout(60000);
		connectionManager.setParams(params);
		
		HttpClient httpClient = new HttpClient(connectionManager);
		
		init(httpClient);
		
		if (data instanceof byte[]) {
			return send(httpClient, (byte[]) data);
		} else if (data instanceof Map) {
			return send(httpClient, (Map) data);
		} else {
			throw new CommunicationException(ErrorConstants.INVALID_PACKET_DATA);
		}
	}
	
	public Object upload(Object data) throws CommunicationException {
		HttpClient httpClient = new HttpClient();
		
		init(httpClient);
		
		return post(httpClient, (File) data);
	}
	
	/*
	 * fileName 是全路径文件名
	 */
	public void download(String fileName) throws CommunicationException {
		HttpClient httpClient = new HttpClient();
		
		init(httpClient);
		
		downloadpost(httpClient,fileName);
	}
	
	private void init(HttpClient httpClient) throws CommunicationException {
		try {
			if ("http".equals(protocol)) {
				httpClient.getHostConfiguration().setHost(host, port);
			} else if ("https".equals(protocol)) {
				if(!MiscUtil.isNullOrEmpty(this.truststoreUrl) && !MiscUtil.isNullOrEmpty(this.truststorePassword)){
					AuthSSLProtocolSocketFactory authSSLProtocolSocketFactory = null;
					try {
						authSSLProtocolSocketFactory = new AuthSSLProtocolSocketFactory(null, null, new URL("file:" + truststoreUrl), truststorePassword);
					} catch (Exception ex) {
						log.error("Exception.", ex);
						throw new IllegalArgumentException(ex);
					}
					Protocol https = new Protocol(protocol, authSSLProtocolSocketFactory, 443);
					httpClient.getHostConfiguration().setHost(host, port, https);
				}else{
					ProtocolSocketFactory protocolSocketFactory = null;
					try {
						protocolSocketFactory = new EasySSLProtocolSocketFactory();
					} catch (Exception ex) {
						log.error("Exception.", ex);
						throw new IllegalArgumentException(ex);
					}
					Protocol https = new Protocol(protocol, protocolSocketFactory, 443);
					httpClient.getHostConfiguration().setHost(host, port, https);
				}
			} else {
				throw new IllegalArgumentException("unsupported protocol: '" + protocol + "'");
			}

			if (userProxy) {
				httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
//				String host = null;
//				try {
//					host = InetAddress.getLocalHost().getHostName();
//				} catch (UnknownHostException ex) {
//					log.error("unknown host:'" + host + "'", ex);
//					throw new IllegalArgumentException(ex);
//				}
//
//				NTCredentials ntc = new NTCredentials(userName, userPassword, host, domain);
//				AuthScope as = new AuthScope(proxyHost, proxyPort);
//
//				httpClient.getState().setProxyCredentials(as, ntc);
			}
		} catch (Exception ex) {
			throw new CommunicationException(ErrorConstants.SYSTEM_INNER_ERROR, ex);
		}
	}
	
	private byte[] send(HttpClient httpClient, final byte[] bytes) throws CommunicationException {
		PostMethod methods = new PostMethod(target);
		
		methods.setRequestHeader("Content-Type", contentType);
		
		if (!MiscUtil.isNullOrEmpty(queryString)) {
			methods.setQueryString(queryString);
		}
		
		methods.setRequestEntity(new RequestEntity() {
			
			public void writeRequest(OutputStream outputStream) throws IOException {
				outputStream.write(bytes);
			}
			
			public boolean isRepeatable() {
				return false;
			}
			
			public String getContentType() {
				return contentType;
			}
			
			public long getContentLength() {
				return bytes.length;
			}
		});
		
		try {
			log.info("REQUEST MESSAGE:" + new String(bytes, "UTF-8"));
			
			httpClient.executeMethod(methods);
			log.info("HTTP STATUS:[" + methods.getStatusLine() + "]");
			
			//HTTP状态200为正常
			if (HttpStatus.SC_OK == methods.getStatusCode()) {
				byte[] result = methods.getResponseBody();
				
				log.info("RESPONSE MESSAGE:" + new String(result, "UTF-8"));
				
				return result;
			} else {
				throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR);
			}
		} catch (Exception ex) {
			if (ex instanceof CommunicationException) {
				throw (CommunicationException) ex;
			}
			throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR, ex);
		} finally {
			methods.releaseConnection();
		}
	}
	
	private byte[] send(HttpClient httpClient, Map data) throws CommunicationException {
		PostMethod methods = new PostMethod(target);
		
		methods.setRequestHeader("Content-Type", contentType);
		
		if (!MiscUtil.isNullOrEmpty(queryString)) {
			methods.setQueryString(queryString);
		}
		
		if (data != null && !data.isEmpty()) {
			log.info("REQUEST MESSAGE:" + data);
			
			for (Iterator iterator = data.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				methods.addParameter(key, (String) data.get(key));
			}
		}
		
		try {
			httpClient.executeMethod(methods);
			log.info("HTTP STATUS:[" + methods.getStatusLine() + "]");
			
			//HTTP状态200为正常
			if (HttpStatus.SC_OK == methods.getStatusCode()) {
				byte[] result = methods.getResponseBody();
				
				log.info("RESPONSE MESSAGE:" + new String(result, "UTF-8"));
				
				return result;
			} else {
				throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR);
			}
		} catch (Exception ex) {
			if (ex instanceof CommunicationException) {
				throw (CommunicationException) ex;
			}
			throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR, ex);
		} finally {
			methods.releaseConnection();
		}
	}

	private void downloadpost(HttpClient httpClient,String fileName) throws CommunicationException {
		PostMethod methods = new PostMethod(target);
		int BUFFER = 1024;
		if (!MiscUtil.isNullOrEmpty(queryString)) {
			methods.setQueryString(queryString);
		}
		
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
		        	throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR);
		        }finally{
		        	 in.close();
		             out.close();
		        }
	            log.info("下载对账文件成功"+fileName);
			} else {
				throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR);
			}
		} catch (Exception ex) {
			if (ex instanceof CommunicationException) {
				throw (CommunicationException) ex;
			}
			throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR, ex);
		} finally {
			methods.releaseConnection();
		}
	}
	
	private byte[] post(HttpClient httpClient, File file) throws CommunicationException {
		PostMethod methods = new PostMethod(target);
		
		if (!MiscUtil.isNullOrEmpty(queryString)) {
			methods.setQueryString(queryString);
		}
		
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
				throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR);
			}
		} catch (Exception ex) {
			if (ex instanceof CommunicationException) {
				throw (CommunicationException) ex;
			}
			throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR, ex);
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
	public void setUserProxy(boolean userProxy) {
		this.userProxy = userProxy;
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
