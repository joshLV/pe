package com.csii.weixin.trans.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.csii.pe.core.PeException;
import com.csii.pp.transport.HttpSSLClientTransport;
import com.csii.pp.util.DESHelper;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;

public class TestAddMerchant {

	public static void main(String[] args) throws PeException {
		// TODO Auto-generated method stub
//        send();
		String ss = "1.通过QQ添加好友-找服务-输入客服QQ号\n2.添加通过后，即可与QQ客服进行沟通";
		System.out.print(ss);
	}
	
	public static void send() throws PeException{
		Map sendMap = new HashMap();
		sendMap.put("appid", "wx9a684aae0b524f3d");
		sendMap.put("mch_id", "1430459002");
		sendMap.put("merchant_name", "胥冬食品店");
		sendMap.put("merchant_shortname", "胥冬之家");
		sendMap.put("service_phone", "18511864511");
		sendMap.put("contact", "胥冬");
		sendMap.put("contact_phone", "18511864511");
		sendMap.put("contact_email", "969064906@qq.com");
		sendMap.put("business", "123");
		sendMap.put("merchant_remark", "备注");

		sendMap.put("sign", sign(sendMap));

		String xmlData = parseXML(sendMap);
		System.out.print(xmlData);
		byte[] bytea = httpSSLSend(xmlData.getBytes(), "https://api.mch.weixin.qq.com/secapi/mch/submchmanage?action=add");

		String str = MiscUtil.getStrMsg(bytea, "UTF-8");
		System.out.print(str);
	}
	
	public static String sign(Map map) {
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
		sf.append("key=" + "Bos95594201701131375110615595594");
		String stringA = sf.toString();
		return DESHelper.getFileDigest(stringA.getBytes());

	}
	
	public static String parseXML(Map map) {
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
	
	public static byte[] httpSSLSend(byte[] bytes, String urls) {
		HttpSSLClientTransport httpClientTransport = null;
		try {
			httpClientTransport = new HttpSSLClientTransport();
			httpClientTransport.setTarget(urls);
			httpClientTransport.setKeystoreUrl("e:/app/epay/jks/apiclient_cert.p12");
			httpClientTransport.setKeystorePassword("1430459002");
			httpClientTransport.setProxy(false);
			httpClientTransport.setProxyHost("");
			httpClientTransport.setProxyPort(8080);

			byte[] bytea = (byte[]) httpClientTransport.submit(bytes);

			return bytea;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

}
