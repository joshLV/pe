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

public class MybankNotifyStreamView extends StreamView {

	private String formatName;
	private TransformerFactory transformerFactory;

	@Override
	public void render(String viewReferer, Object model, Locale locale, HttpServletRequest request, HttpServletResponse response) {
		super.preventCaching(response);
		byte[] result = null;
		try {
			Map data = new HashMap((Map) model);

			Transformer transformer = this.transformerFactory.getTransformer(formatName);
			result = (byte[]) transformer.format(model, data);
			

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

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	public void setTransformerFactory(TransformerFactory transformerFactory) {
		this.transformerFactory = transformerFactory;
	}



}
