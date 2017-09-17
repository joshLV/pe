package com.corefire.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.SortedMap;

public class CorefireHttpPost {
	public static String connect(String reqUrl, SortedMap<String, String> map) throws Exception {
		String res = null;
		try {
			// https单向认证
			HttpURLConnection connection = SSLTrustManager.connect(reqUrl);

			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setReadTimeout(30000);

			byte data[] = XmlUtils.parseXML(map).getBytes("utf-8");
			OutputStream out = connection.getOutputStream();
			out.write(data);
			StringBuffer receivedData = new StringBuffer();
			InputStreamReader inReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
			BufferedReader aReader = new BufferedReader(inReader);
			String aLine;
			while ((aLine = aReader.readLine()) != null) {
				receivedData.append(aLine);
			}
			res = receivedData.toString();
			aReader.close();
			connection.disconnect();

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return res;
	}

	public static String connect(String reqUrl, String reqJson) throws Exception {
		OutputStream out = null;
		HttpURLConnection connection = null;
		BufferedReader aReader = null;
		InputStreamReader inReader = null;
		String res = "";
		try {
			connection = SSLTrustManager.connect(reqUrl);

			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(90000);

			byte data[] = reqJson.getBytes("UTF-8");
			out = connection.getOutputStream();

			out.write(data);

			inReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
			aReader = new BufferedReader(inReader);

			String aLine;
			StringBuffer receivedData = new StringBuffer();
			while ((aLine = aReader.readLine()) != null) {
				receivedData.append(aLine);
			}

			res = receivedData.toString();

		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (aReader != null) {
				aReader.close();
			}
			if (inReader != null) {
				inReader.close();
			}
			if (out != null) {
				out.close();
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		return res;
	}
}