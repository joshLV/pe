package com.csii.pp.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.CommunicationException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.transport.HttpClientTransport;
import com.csii.pp.transport.HttpSSLClientTransport;

public class WxUtil {

	private static Logger logger = LoggerFactory.getLogger(WxUtil.class);

	private boolean userProxy;

	private String proxyHost;

	private int proxyPort;

	private String key;// 微信支付key

	private URL url;

	/**
	 * 参数名ASCII码从小到大排序
	 * 
	 * @param object
	 * @return
	 */
	public static Set sortedmap(Map map) {
		SortedMap<String, String> sort = new TreeMap<String, String>(map);
		Set<Entry<String, String>> entry1 = sort.entrySet();
		return entry1;
	}

	/***
	 * 微信支付签名
	 * 
	 * @param map
	 * @return
	 */
	public String sign(Map map) {
		Set<Entry<String, String>> entry1 = WxUtil.sortedmap(map);
		Iterator<Entry<String, String>> it = entry1.iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String k = entry.getKey();
			Object v = entry.getValue();
			if ((null != v) && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sf.append(k + "=" + v + "&");
			}
		}
		sf.append("key=" + key);
		String stringA = sf.toString();
		return DESHelper.getFileDigest(stringA.getBytes());

	}

	public byte[] httpSend(byte[] bytes, String urls) throws CommunicationException {
		logger.info("REQUEST WeiXin Url =>>> [{}]", urls);
		HttpClientTransport httpClientTransport = null;
		try {
			url = new URL(urls);
			httpClientTransport = new HttpClientTransport();
			httpClientTransport.setProtocol(url.getProtocol());
			httpClientTransport.setHost(url.getHost());
			httpClientTransport.setPort(url.getPort() == -1 ? url.getDefaultPort() : url.getPort());
			httpClientTransport.setTarget(url.getPath());
			httpClientTransport.setQueryString(url.getQuery());
			httpClientTransport.setUserProxy(userProxy);
			httpClientTransport.setProxyHost(proxyHost);
			httpClientTransport.setProxyPort(proxyPort);

			byte[] bytea = (byte[]) httpClientTransport.submit(bytes);
			return bytea;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new CommunicationException();
		}
	}

	public byte[] httpSSLSend(byte[] bytes, String urls,String keystorePath,String keystorePassword) {
		logger.info("REQUEST WeiXin Url =>>> [{}]", urls);
		HttpSSLClientTransport httpClientTransport = null;
		try {
			httpClientTransport = new HttpSSLClientTransport();
			httpClientTransport.setTarget(urls);
			httpClientTransport.setKeystoreUrl(keystorePath);
			httpClientTransport.setKeystorePassword(keystorePassword);
			httpClientTransport.setProxy(userProxy);
			httpClientTransport.setProxyHost(proxyHost);
			httpClientTransport.setProxyPort(proxyPort);

			byte[] bytea = (byte[]) httpClientTransport.submit(bytes);

			return bytea;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	public String parseXML(Map map) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		Set es = WxUtil.sortedmap(map);
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if ((null != v) && !"".equals(v) && !"appkey".equals(k)) {
				sb.append("<" + k + ">" + v + "</" + k + ">\n");
			}
		}
		sb.append("</xml>");
		return sb.toString();
	}

	/**
	 * 
	 * @return
	 * @throws PeException
	 * @author ZhangJianBing
	 * @date 2016年9月18日
	 */
	public Map read(File sourceName, File targetName) throws PeException {

		RandomAccessFile rf = null;
		BufferedWriter bw = null;
		Map map = new HashMap();
		int transNum = 0;
		try {
			rf = new RandomAccessFile(sourceName, "r");
			long len = rf.length();
			if (0 == len) {
				bw = new BufferedWriter(new FileWriter(targetName, true));
				bw.write("");
				map.put("transNum", transNum);
				map.put("Amount", "0");
				map.put("PayCharge", "0");
				map.put("RefundAmount", "0");
			} else {
				long start = rf.getFilePointer();
				long nextend = (start + len) - 1;
				int lineNum = 0;
				String line;
				rf.seek(nextend);
				int c = -1;
				while (nextend > start) {
					c = rf.read();
					if ((c == '\n') || (c == '\r')) {
						lineNum++;
						line = rf.readLine();
						if (line != null) {
							if (lineNum == 2) {
								String[] transInfo = line.split(",");
								String num = transInfo[0].substring(1);
								String TatolAmount = transInfo[1].substring(1);
								transNum = Integer.parseInt(num);
								String RefundAmount = transInfo[2].substring(1);
								String paycharge = transInfo[4].substring(1);
								map.put("Amount", TatolAmount);
								map.put("PayCharge", paycharge);
								map.put("RefundAmount", RefundAmount);
								map.put("transNum", transNum);
							}
						}
						nextend--;
					}
					if (lineNum == 3) {
						nextend = 0;
						rf.seek(nextend);
						bw = new BufferedWriter(new FileWriter(targetName, true));
						for (int i = 0; i <= transNum; i++) {
							line = rf.readLine();
							bw.write(new String(line.getBytes("ISO-8859-1"), "UTF-8"));
							bw.newLine();
							map.put("transNum", transNum);
						}
						break;
					} else {
						nextend--;
					}
					rf.seek(nextend);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new PeException("解析出错");
		} finally {
			try {
				if (rf != null) {
					rf.close();
				}
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new PeException("解析出错");
			}
		}

		return map;
	}

	/**
	 * 获取对应的状态码
	 * 
	 * @param resultCode
	 * @return
	 */
	public static String getTransStatus(String resultCode) {
		Map map = new HashMap();
		map.put("SUCCESS", Constants.TRANS_STATUS_OK); // 支付成功
		map.put("REFUND", Constants.TRANS_STATUS_REFUND_OK); // 转入退款：表示已退回给客户
		map.put("NOTPAY", Constants.TRANS_STATUS_UNPAY); // 未支付状态： 按照失败返回
		map.put("CLOSED", Constants.TRANS_STATUS_ORDER_CLOSE); // 按照失败返回
		map.put("REVOKED", Constants.TRANS_STATUS_CANCELED); // 已撤销：（刷卡支付）
		map.put("USERPAYING", Constants.TRANS_STATUS_PAYING); // 状态未名：支付中
		map.put("PAYERROR", Constants.TRANS_STATUS_ERROR);// 失败返回
		map.put("FAIL", Constants.TRANS_STATUS_ERROR);// 失败返回
		map.put("PROCESSING", Constants.TRANS_STATUS_PROCESSING);// 退款处理中
		map.put("CHANGE", Constants.TRANS_STATUS_CHANGE);// 转入代发
		map.put("succ", Constants.TRANS_STATUS_OK); // 支付成功
		map.put("fail", Constants.TRANS_STATUS_ERROR);// 失败返回
		map.put("paying", Constants.TRANS_STATUS_PAYING); // 状态未名：支付中
		map.put("closed", Constants.TRANS_STATUS_ORDER_CLOSE); // 按照失败返回
		map.put("cancel", Constants.TRANS_STATUS_CANCELED); // 已撤销：（刷卡支付）
		map.put("refunding", Constants.TRANS_STATUS_PROCESSING);// 退款处理中

		map.put(Constants.TRANS_STATUS_OK, "SUCCESS"); // 支付成功
		map.put(Constants.TRANS_STATUS_REFUND_OK, "REFUND"); // 转入退款：表示已退回给客户
		map.put(Constants.TRANS_STATUS_UNPAY, "NOTPAY"); // 未支付状态： 按照失败返回
		map.put(Constants.TRANS_STATUS_ORDER_CLOSE, "CLOSED"); // 按照失败返回
		map.put(Constants.TRANS_STATUS_CANCELED, "REVOKED"); // 已撤销：（刷卡支付）
		map.put(Constants.TRANS_STATUS_PAYING, "USERPAYING"); // 状态未名：支付中
		map.put(Constants.TRANS_STATUS_ERROR, "PAYERROR");// 失败返回
		map.put(Constants.TRANS_STATUS_ERROR, "FAIL");// 失败返回
		map.put(Constants.TRANS_STATUS_PROCESSING, "PROCESSING");// 退款处理中
		map.put(Constants.TRANS_STATUS_CHANGE, "CHANGE");// 转入代发
		map.put(Constants.TRANS_STATUS_SUB_WITHDRAW,"SUBREFUND");
		map.put(Constants.TRANS_STATUS_ALL_WITHDRAW,"ALLREFUND");
		return (String) (map.get(resultCode) == null ? Constants.TRANS_STATUS_ERROR : map.get(resultCode));
	}

	/**
	 * 获取对应的状态码(和上面方法就是 notPAY 状态不一样)
	 * 
	 * @param resultCode
	 * @return
	 */
	public String getTransStatus2(String resultCode) {
		Map map = new HashMap();
		map.put("SUCCESS", Constants.TRANS_STATUS_OK); // 支付成功
		map.put("REFUND", Constants.TRANS_STATUS_REFUND_OK); // 转入退款：表示已退回给客户
		map.put("NOTPAY", Constants.TRANS_STATUS_UNPAY); // 未支付状态： 按照失败返回
		map.put("CLOSED", Constants.TRANS_STATUS_ERROR); // 按照失败返回
		map.put("REVOKED", Constants.TRANS_STATUS_CANCELED); // 已撤销：（刷卡支付）
		map.put("USERPAYING", Constants.TRANS_STATUS_PAYING); // 状态未名：支付中
		map.put("NOPAY", Constants.TRANS_STATUS_ERROR); // 按照失败返回
		map.put("PAYERROR", Constants.TRANS_STATUS_ERROR);// 失败返回

		map.put(Constants.TRANS_STATUS_OK, "SUCCESS"); // 支付成功
		map.put(Constants.TRANS_STATUS_REFUND_OK, "REFUND"); // 转入退款：表示已退回给客户
		map.put(Constants.TRANS_STATUS_UNPAY, "NOTPAY"); // 未支付状态： 按照失败返回
		map.put(Constants.TRANS_STATUS_ERROR, "CLOSED"); // 按照失败返回
		map.put(Constants.TRANS_STATUS_CANCELED, "REVOKED"); // 已撤销：（刷卡支付）
		map.put(Constants.TRANS_STATUS_PAYING, "USERPAYING"); // 状态未名：支付中
		map.put(Constants.TRANS_STATUS_ERROR, "NOPAY"); // 按照失败返回
		map.put(Constants.TRANS_STATUS_ERROR, "PAYERROR");// 失败返回

		return (String) (map.get(resultCode) == null ? Constants.TRANS_STATUS_ERROR : map.get(resultCode));
	}

	/**
	 * 获取对应的状态
	 * 
	 * @param status
	 * @return
	 */
	public String getTransResult(String status) {
		if ((status == null) || status.equals("")) {
			return "FAIL";
		} else if (status.equals(Constants.TRANS_STATUS_OK)) {
			return "SUCCESS";
		} else {
			return "FAIL";
		}

	}

	public String getAttach(Order order) {
		StringBuffer sb = new StringBuffer("`store_appid=");
		sb.append(order.getMerchantId()).append("#store_name=").append(order.getMerchant().getName()).append("#op_user=000001");
		return sb.toString();
	}

	public void setUserProxy(boolean userProxy) {
		this.userProxy = userProxy;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
