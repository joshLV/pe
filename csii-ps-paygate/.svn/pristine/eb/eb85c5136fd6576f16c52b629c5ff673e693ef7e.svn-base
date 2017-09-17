/*
 * @(#)TenpayStreamView.java	1.0 2011-5-27 下午05:35:05
 *
 * Copyright 2004-2010 Client Service International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pe.channel.http.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.csii.pp.util.MiscUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class WeixinStreamView extends StreamView {

	private String key;

	@Override
	public void render(String viewReferer, Object model, Locale locale, HttpServletRequest request, HttpServletResponse response) {

		super.preventCaching(response);

		byte[] result = null;
		try {
			Map data = new HashMap((Map) model);

			Object jsonData = checkErrorDataMap(data);

			if (jsonData instanceof Map) {
				Map map = (Map) jsonData;
				map.put("sign", MiscUtil.sign((Map) jsonData, key));
			}

			ObjectMappingCustomer objectMapper = new ObjectMappingCustomer();

			objectMapper.setSerializationInclusion(Include.NON_NULL);

			String jsonStr = objectMapper.writeValueAsString(jsonData);

			result = jsonStr.getBytes("UTF-8");

		} catch (Exception ex) {
			log.error("FORMAT RESPONSE PACKET ERROR.", ex);
		} finally {
			ServletOutputStream output = null;
			try {
				output = response.getOutputStream();

				response.setContentLength(result.length);

				response.setContentType(getContentType());

				log.info("RESPONSE MESSAGE:" + new String(result, "UTF-8"));

				output.write(result);

				output.flush();
			} catch (IOException ex) {
				log.error("response error.", ex);
			} finally {
				if (output != null) {
					try {
						output.close();
					} catch (IOException ex) {
						log.error("output stream close error.", ex);
					}
				}
				log.info("process end.");
			}
		}
	}

	public Object checkErrorDataMap(Map map) {
		Map resMap = new HashMap();
		String exceptionMessageCode = (String) map.get("_exceptionMessageCode");
		String exceptionMessage = (String) map.get("_exceptionMessage");

		if ((exceptionMessageCode == null) || (exceptionMessage == null)) {
			Object obj = map.get(getContentField());
			if (obj instanceof Map) {
				resMap = (Map) obj;
			} else {
				return obj;
			}
		} else {
			resMap.put("ReturnCode", exceptionMessageCode);
			resMap.put("ReturnMsg", exceptionMessage);
		}

		resMap.put("nonceStr", MiscUtil.getRandom());
		return resMap;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
