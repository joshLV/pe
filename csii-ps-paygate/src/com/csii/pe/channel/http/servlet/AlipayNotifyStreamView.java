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

import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactory;
import com.csii.pp.util.MiscUtil;

public class AlipayNotifyStreamView extends StreamView {


	@Override
	public void render(String viewReferer, Object model, Locale locale, HttpServletRequest request, HttpServletResponse response) {
		String repsMsg =null;
		super.preventCaching(response);
		try {
			Map data = new HashMap((Map) model);
			
			 repsMsg= (String) data.get("ReturnCode");
			
			if(MiscUtil.isNullOrEmpty(repsMsg)) {
				repsMsg="fail";
			}

		} catch (Exception ex) {
			log.error("format response packet error.", ex);
		} finally {
			ServletOutputStream output = null;
			try {
				output = response.getOutputStream();
				response.setContentLength(repsMsg.length());
				response.setContentType(getContentType());

				log.info("RESPONSE MESSAGE:" + repsMsg );

				output.write(repsMsg.getBytes());
				
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




}
