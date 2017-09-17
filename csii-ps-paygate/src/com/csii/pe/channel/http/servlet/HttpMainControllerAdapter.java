/*
 * @(#)HttpMainControllerAdapter.java	1.0 2011-4-15
 *
 * Copyright 2004-2010 Client Service International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pe.channel.http.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

/**
 * HttpMainControllerAdapter.java
 * 
 * HttpMainController的适配器
 *
 * @author cuiyi
 * <p>
 *   Created on 2011-4-18
 *   Modification history
 * </p>
 * <p>
 *   IBS Product Expert Group, CSII
 *   Powered by CSII PowerEngine 6.0
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class HttpMainControllerAdapter implements org.springframework.web.servlet.mvc.Controller  {
	
	private Controller controller;
	
	private static final String ATT_VIEWREFERER_KEY = "_viewReferer";

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Object obj = controller.process(request, response, response.getLocale());
		ModelAndView mav = new ModelAndView();
		mav.addAllObjects((Map)obj);
		if(mav.getModel() == null){
			mav.setViewName((String)request.getAttribute(ATT_VIEWREFERER_KEY));
		}else{
			mav.setViewName((String) mav.getModel().get(ATT_VIEWREFERER_KEY));
		}
		return mav;
	}
	
	

	public void setController(Controller controller) {
		this.controller = controller;
	}
}
