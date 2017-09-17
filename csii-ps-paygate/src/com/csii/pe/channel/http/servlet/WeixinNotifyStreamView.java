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

import com.csii.pp.util.WxUtil;

public class WeixinNotifyStreamView extends StreamView {

	private WxUtil wxUtil;

	@Override
	public void render(String viewReferer, Object model, Locale locale, HttpServletRequest request, HttpServletResponse response) {
		super.preventCaching(response);
		byte[] result = null;
		try {
			Map data = new HashMap((Map) model);

			String returnCode = (String) data.get("ReturnCode");
			String returnMsg = (String) data.get("ReturnMsg");

			Map para = new HashMap();
			para.put("return_code", returnCode);
			para.put("return_msg", returnMsg);
			String xml = wxUtil.parseXML(para);
			result = xml.getBytes("UTF-8");

		} catch (Exception ex) {
			log.error("format response packet error.", ex);
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
			}
		}

	}

	public void setWxUtil(WxUtil wxUtil) {
		this.wxUtil = wxUtil;
	}

}
