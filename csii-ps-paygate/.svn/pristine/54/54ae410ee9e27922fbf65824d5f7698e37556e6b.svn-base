package com.csii.pe.channel.http.servlet;

/*
 * @(#)DispatcherServlet.java	1.0 2011-4-15
 *
 * Copyright 2004-2010 Client Service International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.throwaway.ThrowawayControllerHandlerAdapter;
import org.springframework.web.util.UrlPathHelper;

import com.csii.pe.common.util.FileProperties;
import com.csii.pe.core.PeException;
import com.csii.pe.core.PeRuntimeException;

/**
 * DispatcherServlet.java
 * 
 * 根据Spring DispatcherServlet修改csii MainServlet
 * 根据不同URL选择不同MainController
 *
 * @author cuiyi
 *         <p>
 *         Created on 2011-4-18 Modification history
 *         </p>
 *         <p>
 *         IBS Product Expert Group, CSII Powered by CSII PowerEngine 6.0
 *         </p>
 * @version 1.0
 * @since 1.0
 * @see org.springframework.web.servlet.DispatcherServlet
 * @see com.csii.pe.channel.http.servlet.MainServlet
 * @see com.csii.pe.channel.http.servlet.PowerEngineDispatcher
 */
public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = -5980602597887851846L;

	public static final String SERVLET_CONTEXT_PREFIX = MainServlet.SERVLET_CONTEXT_PREFIX;
	public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = MainServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE;
	public static final String HANDLER_EXECUTION_CHAIN_ATTRIBUTE = com.csii.pe.channel.http.servlet.DispatcherServlet.class.getName() + ".HANDLER";
	public static final Class DEFAULT_CONTEXT_CLASS = org.springframework.web.context.support.XmlWebApplicationContext.class;

	public static final String MULTIPART_RESOLVER_BEAN_NAME = "multipartResolver";
	public static final String LOCALE_RESOLVER_BEAN_NAME = "localeResolver";
	public static final String DEFAULT_CONTROLLER_BEAN_NAME = "mainController";
	public static final String VIEW_RESOLVER_BEAN_NAME = "mainViewResolver";
	public static final String VIEW_REFERER_ATTRIBUTE = "_viewReferer";
	public static final String DEFAULT_NAMESPACE_SUFFIX = "-servlet";
	public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";
	public static final String HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";
	public static final String DEFAULT_EXCEPTION_ATTRIBUTE = "_exception";
	public static final String DEFAULT_EXCEPTION_MSG_ATTRIBUTE = "_exceptionMessage";
	public static final String URL_MAPPING_BEAN_NAME = "urlMapping";
	public static AntPathMatcher ANT_PATH_MATCHER;

	protected Log logger;

	private WebApplicationContext applicationContext;

	private MultipartResolver multipartResolver;

	private LocaleResolver localeResolver;

	private CsiiViewResolver viewResolver;

	private String contextConfigLocation;

	private Class contextClass;

	private String nameSpace;

	private String localeParamName;

	private boolean isPublishContext = true;

	private boolean synchronizeOnSession = false;

	private boolean refreshEnabled = false;

	private boolean detectAllHandlerMappings = true;

	private boolean detectAllHandlerAdapters = true;

	private List handlerMappings;

	private List handlerAdapters;

	public DispatcherServlet() {
		contextClass = DEFAULT_CONTEXT_CLASS;
		this.localeParamName = "_locale";
	}

	@Override
	public final void init() throws ServletException {
		ServletConfig servletconfig = getServletConfig();
		if (servletconfig.getInitParameter("org.apache.commons.logging.LogFactory") != null) {
			System.setProperty("org.apache.commons.logging.LogFactory", servletconfig.getInitParameter("org.apache.commons.logging.LogFactory"));
		}

		logger = LogFactory.getLog(getClass());

		logger.info("Initializing servlet '" + getServletName() + "'");

		if (servletconfig.getInitParameter("localeParamName") != null) {
			localeParamName = servletconfig.getInitParameter("localeParamName");
		}

		if (servletconfig.getInitParameter("synchronizeOnSession") != null) {
			synchronizeOnSession = Boolean.valueOf(servletconfig.getInitParameter("synchronizeOnSession")).booleanValue();
		}

		if (servletconfig.getInitParameter("refreshEnabled") != null) {
			refreshEnabled = Boolean.valueOf(servletconfig.getInitParameter("refreshEnabled")).booleanValue();
		}

		contextConfigLocation = servletconfig.getInitParameter("contextConfigLocation");

		if (contextConfigLocation == null) {
			try {
				contextConfigLocation = resolveConfigLocation(null, "config/pe-configs.xml");
				logger.info("Current contextConfigLocation is: " + contextConfigLocation);
			} catch (Exception ex) {
				logger.error("can't load pe-configs.xml", ex);
			}
		}
		if (contextConfigLocation != null) {
			applicationContext = initWebApplicationContext();
		} else {
			applicationContext = getWebApplicationContext(getServletContext());
		}

		if (applicationContext != null) {
			initMultipartResolver();
			initLocaleResolver();
			initViewResolver();
			initUrlMapping();
			initHandlerAdapters();

			logger.info("Servlet '" + getServletName() + "' configured successfully");
		} else {
			logger.error("init error. contextConfigLocation should be set or another servlet should have loaded ApplicationContext before this servlet");
		}

		if (ANT_PATH_MATCHER == null) {
			ANT_PATH_MATCHER = new AntPathMatcher();
		}

		System.gc();
	}

	public final synchronized void refresh() throws ServletException {
		logger.info("Refreshing  servlet '" + getServletName() + "'");
		if (applicationContext != null) {
			((ConfigurableWebApplicationContext) applicationContext).refresh();
			initMultipartResolver();
			initLocaleResolver();
			initViewResolver();
			initUrlMapping();
			initHandlerAdapters();

			logger.info("Servlet '" + getServletName() + "' refreshed successfully");
		} else {
			logger.error("refresh error. contextConfigLocation should be set or another servlet should have loaded ApplicationContext before this servlet");
		}
		System.gc();
	}

	private void initMultipartResolver() throws BeansException {
		try {
			multipartResolver = (MultipartResolver) getWebApplicationContext().getBean(MULTIPART_RESOLVER_BEAN_NAME);
			if (logger.isInfoEnabled()) {
				logger.info("Using MultipartResolver [" + multipartResolver + "]");
			}
		} catch (NoSuchBeanDefinitionException nosuchbeandefinitionexception) {
			multipartResolver = null;
			if (logger.isInfoEnabled()) {
				logger.info("Unable to locate MultipartResolver with name [multipartResolver]: no multipart handling provided");
			}
		}
	}

	private void initLocaleResolver() throws BeansException {
		try {
			localeResolver = (LocaleResolver) getWebApplicationContext().getBean(LOCALE_RESOLVER_BEAN_NAME);
			if (logger.isInfoEnabled()) {
				logger.info("Using LocaleResolver [" + localeResolver + "]");
			}
		} catch (NoSuchBeanDefinitionException nosuchbeandefinitionexception) {
			localeResolver = new AcceptHeaderLocaleResolver();
			if (logger.isInfoEnabled()) {
				logger.info("Unable to locate LocaleResolver with name 'localeResolver': using default [" + localeResolver + "]");
			}
		}
	}

	private void initViewResolver() throws BeansException {
		try {
			viewResolver = (CsiiViewResolver) getWebApplicationContext().getBean(VIEW_RESOLVER_BEAN_NAME);
		} catch (NoSuchBeanDefinitionException nosuchbeandefinitionexception) {
			logger.fatal("Unable to locate view resolver with name 'mainViewResolver");
		}
	}

	private void initUrlMapping() throws BeansException {
		handlerMappings = null;

		if (detectAllHandlerMappings) {
			// Find all HandlerMappings in the ApplicationContext,
			// including ancestor contexts.
			Map matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(getWebApplicationContext(), HandlerMapping.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerMappings = new ArrayList(matchingBeans.values());
				// We keep HandlerMappings in sorted order.
				Collections.sort(this.handlerMappings, new OrderComparator());
			}
		} else {
			try {
				Object hm = getWebApplicationContext().getBean(URL_MAPPING_BEAN_NAME, HandlerMapping.class);
				handlerMappings = Collections.singletonList(hm);
			} catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerMapping later.
			}
		}

		if (handlerMappings == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No HandlerMappings found in servlet '" + getServletName() + "': using default");
			}
			throw new FatalBeanException("No HandlerMappings found in servlet '" + getServletName());
		}
	}

	private void initHandlerAdapters() {
		this.handlerAdapters = null;

		if (detectAllHandlerAdapters) {
			// Find all HandlerAdapters in the ApplicationContext,
			// including ancestor contexts.
			Map matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(getWebApplicationContext(), HandlerAdapter.class, true, false);
			if (!matchingBeans.isEmpty()) {
				handlerAdapters = new ArrayList(matchingBeans.values());
				// We keep HandlerAdapters in sorted order.
				Collections.sort(this.handlerAdapters, new OrderComparator());
			}
		} else {
			try {
				Object ha = getWebApplicationContext().getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
				handlerAdapters = Collections.singletonList(ha);
			} catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerAdapter later.
			}
		}

		// Ensure we have at least some HandlerAdapters, by registering
		// default HandlerAdapters if no other adapters are found.
		if ((handlerAdapters == null) || handlerAdapters.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("No HandlerAdapters found in servlet '" + getServletName() + "': using default");
			}
			handlerAdapters = new ArrayList(2);
			handlerAdapters.add(new SimpleControllerHandlerAdapter());
			handlerAdapters.add(new ThrowawayControllerHandlerAdapter());
			logger.info("No HandlerAdapters found in servlet '" + getServletName() + "': using default");
		} else {
			Collections.sort(this.handlerAdapters, new OrderComparator());
		}
	}

	protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		Iterator it = handlerAdapters.iterator();
		while (it.hasNext()) {
			HandlerAdapter ha = (HandlerAdapter) it.next();
			if (ha.supports(handler)) {
				return ha;
			}
		}
		throw new ServletException("No adapter for handler [" + handler + "]: Does your handler implement a supported interface like Controller?");
	}

	private HandlerExecutionChain getHandler(HttpServletRequest request, boolean cache) throws Exception {
		HandlerExecutionChain handler = (HandlerExecutionChain) request.getAttribute(HANDLER_EXECUTION_CHAIN_ATTRIBUTE);
		if (handler != null) {
			if (!cache) {
				request.removeAttribute(HANDLER_EXECUTION_CHAIN_ATTRIBUTE);
			}
			return handler;
		}

		Iterator it = handlerMappings.iterator();
		while (it.hasNext()) {
			HandlerMapping hm = (HandlerMapping) it.next();

			if (hm instanceof SimpleUrlHandlerMapping) {
				String requestPath = request.getRequestURI();
				requestPath = requestPath.substring(request.getContextPath().length());

				Map pathMap = ((SimpleUrlHandlerMapping) hm).getUrlMap();

				Iterator iterator = pathMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry entry = (Map.Entry) iterator.next();
					if (ANT_PATH_MATCHER.match((String) entry.getKey(), requestPath)) {
						return new HandlerExecutionChain(applicationContext.getBean((String) entry.getValue()));
					}
				}
			}

			handler = hm.getHandler(request);
			if (handler != null) {
				if (cache) {
					request.setAttribute(HANDLER_EXECUTION_CHAIN_ATTRIBUTE, handler);
				}
				return handler;
			}
		}
		return null;
	}

	public WebApplicationContext getWebApplicationContext(ServletContext servletcontext) {
		for (Enumeration enumeration = servletcontext.getAttributeNames(); enumeration.hasMoreElements();) {
			String s = (String) enumeration.nextElement();
			Object obj = servletcontext.getAttribute(s);
			if (obj instanceof WebApplicationContext) {
				WebApplicationContext webapplicationcontext = (WebApplicationContext) obj;
				return webapplicationcontext;
			}
		}

		return null;
	}

	@Override
	protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (refreshEnabled) {
			String refresh = request.getParameter("refresh");
			if (refresh != null) {
				refresh();
			}
		}
		request.setAttribute("_METHOD", MainController.METHOD_GET);
		if (synchronizeOnSession) {
			javax.servlet.http.HttpSession httpsession = request.getSession(false);
			if (httpsession != null) {
				synchronized (httpsession) {
					doService(request, response);
				}
				return;
			}
		}
		doService(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object obj = request;
		if ((multipartResolver != null) && multipartResolver.isMultipart(request)) {
			if (request instanceof MultipartHttpServletRequest) {
				logger.info("Request is already a MultipartHttpServletRequest - if not in a forward, this typically results from an additional MultipartFilter in web.xml");
			} else {
				obj = multipartResolver.resolveMultipart(request);
			}
		}

		if (refreshEnabled) {
			String refresh = ((HttpServletRequest) (obj)).getParameter("refresh");
			if (refresh != null) {
				refresh();
			}
		}
		((HttpServletRequest) (obj)).setAttribute("_METHOD", MainController.METHOD_POST);

		if (synchronizeOnSession) {
			javax.servlet.http.HttpSession httpsession = ((HttpServletRequest) (obj)).getSession(false);
			if (httpsession != null) {
				synchronized (httpsession) {
					doService(((HttpServletRequest) (obj)), response);
				}
				return;
			}
		}
		doService(((HttpServletRequest) (obj)), response);
	}

	private void doService(HttpServletRequest request, HttpServletResponse response) {
		long beginTime = System.currentTimeMillis();

		java.util.Map paras = request.getParameterMap();
		for (java.util.Iterator it = paras.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			String[] value = (String[]) paras.get(key);
			logger.info(key + "=" + value[0]);
		}

		try {
			// iframe引起的内部cookie丢失
			response.setHeader("P3P", "CP=CAO PSA OUR");

			String localeName = request.getParameter(localeParamName);
			if (localeName != null) {
				LocaleEditor localeeditor = new LocaleEditor();
				localeeditor.setAsText(localeName);
				localeResolver.setLocale(request, response, (Locale) localeeditor.getValue());
			}

			Locale locale = localeResolver.resolveLocale(request);

			request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
			request.setAttribute(LOCALE_RESOLVER_BEAN_NAME, localeResolver);

			// Object model = controller.process(request, response, locale);

			HandlerExecutionChain mappedHandler = getHandler(request, false);

			if ((mappedHandler == null) || (mappedHandler.getHandler() == null)) {
				noHandlerFound(request, response);
				return;
			}

			int interceptorIndex = -1;

			// Apply preHandle methods of registered interceptors.
			HandlerInterceptor[] interceptors = mappedHandler.getInterceptors();
			if (interceptors != null) {
				for (int i = 0; i < interceptors.length; i++) {
					HandlerInterceptor interceptor = interceptors[i];
					if (!interceptor.preHandle(request, response, mappedHandler.getHandler())) {
						triggerAfterCompletion(mappedHandler, interceptorIndex, request, response, null);
						return;
					}
					interceptorIndex = i;
				}
			}

			response.setLocale(locale);

			// Actually invoke the handler.
			HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
			ModelAndView mv = ha.handle(request, response, mappedHandler.getHandler());
			if (mv != null) {
				renderView(mv.getModel(), request, response, locale);
			} else {
				renderView(null, request, response, locale);
			}
		} catch (Exception exception) {
			logger.error("process error:", exception);
		} finally {
			if (logger.isDebugEnabled()) {
				logger.debug("request process end.total time:" + (System.currentTimeMillis() - beginTime) + "ms.");
			}
		}
	}

	private void triggerAfterCompletion(HandlerExecutionChain mappedHandler, int interceptorIndex, HttpServletRequest request, HttpServletResponse response, Exception ex) {
		// Apply afterCompletion methods of registered interceptors.
		if (mappedHandler != null) {
			HandlerInterceptor[] interceptors = mappedHandler.getInterceptors();
			if (interceptors != null) {
				for (int i = interceptorIndex; i >= 0; i--) {
					HandlerInterceptor interceptor = interceptors[i];
					try {
						interceptor.afterCompletion(request, response, mappedHandler.getHandler(), ex);
					} catch (Throwable ex2) {
						logger.error("HandlerInterceptor.afterCompletion threw exception", ex2);
					}
				}
			}
		}
	}

	protected WebApplicationContext initWebApplicationContext() throws BeansException {
		getServletContext().log("Loading WebApplicationContext for servlet '" + getServletName() + "'");
		WebApplicationContext webapplicationcontext = createWebApplicationContext(null);
		if (logger.isInfoEnabled()) {
			logger.info("Using context class [" + webapplicationcontext.getClass().getName() + "] for servlet '" + getServletName() + "'");
		}
		if (isPublishContext) {
			String contextAttributeName = getServletContextAttributeName();
			getServletContext().setAttribute(contextAttributeName, webapplicationcontext);
			if (logger.isInfoEnabled()) {
				logger.info("Published WebApplicationContext of servlet '" + getServletName() + "' as ServletContext attribute with name [" + contextAttributeName + "]");
			}
		}
		return webapplicationcontext;
	}

	protected WebApplicationContext createWebApplicationContext(WebApplicationContext webapplicationcontext) throws BeansException {
		if (logger.isInfoEnabled()) {
			logger.info("Servlet with name '" + getServletName() + "' will try to create custom WebApplicationContext context of class '" + getContextClass().getName() + "'"
					+ " using parent context [" + webapplicationcontext + "]");
		}
		if (!org.springframework.web.context.ConfigurableWebApplicationContext.class.isAssignableFrom(getContextClass())) {
			throw new ApplicationContextException("Fatal initialization error in servlet with name '" + getServletName() + "': custom WebApplicationContext class [" + getContextClass().getName()
					+ "] is not of type ConfigurableWebApplicationContext");
		}
		ConfigurableWebApplicationContext configurablewebapplicationcontext = (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(getContextClass());
		configurablewebapplicationcontext.setParent(webapplicationcontext);
		configurablewebapplicationcontext.setServletContext(getServletContext());
		configurablewebapplicationcontext.setNamespace(getNamespace());
		if (contextConfigLocation != null) {
			configurablewebapplicationcontext.setConfigLocations(StringUtils.tokenizeToStringArray(contextConfigLocation, ",; \t\n", true, true));
		}
		configurablewebapplicationcontext.refresh();
		return configurablewebapplicationcontext;
	}

	@Override
	public void destroy() {
		getServletContext().log("Closing WebApplicationContext of servlet '" + getServletName() + "'");
		if ((contextConfigLocation != null) && (applicationContext instanceof ConfigurableApplicationContext)) {
			((ConfigurableApplicationContext) applicationContext).close();
		}
	}

	private void renderView(Object obj, HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String viewReferer = (String) request.getAttribute(VIEW_REFERER_ATTRIBUTE);
		String views[] = getView(viewReferer);
		CsiiView csiiview = viewResolver.resolveView(views[0]);
		if (csiiview == null) {
			throw new PeRuntimeException("pe.cannot_resolve_view", new Object[] { views[0], viewReferer });
		}

		// logger.error("=-----content="+(new String((byte[])())));
		csiiview.render(views[1], obj, locale, request, response);
	}

	private String[] getView(String viewReferer) {
		String views[] = new String[2];
		String as1[] = StringUtils.delimitedListToStringArray(viewReferer, ",");
		if (as1.length == 1) {
			views[0] = "default";
			views[1] = as1[0];
		} else if (as1.length == 2) {
			views[0] = as1[0];
			views[1] = as1[1];
		}
		return views;
	}

	protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (logger.isWarnEnabled()) {
			String requestUri = new UrlPathHelper().getRequestUri(request);
			logger.warn("No mapping for [" + requestUri + "] in DispatcherServlet with name '" + getServletName() + "'");
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private String resolveConfigLocation(String mode, String configFileName) throws IOException, PeException {
		InputStream inputstream = getClass().getClassLoader().getResourceAsStream(configFileName);
		try {
			if (inputstream == null) {
				throw new PeRuntimeException("pe.cannot_load_peconfig");
			}

			FileProperties fileproperties = new FileProperties();
			fileproperties.loadXML(inputstream);
			inputstream.close();
			String currentMode = (String) fileproperties.get("CurrentMode");
			if (mode != null) {
				currentMode = mode;
			}
			if (logger.isInfoEnabled()) {
				logger.info("Config: " + configFileName + " -> " + "Current Mode is: " + currentMode);
			}
			String contextConfigLocation = (String) fileproperties.get(currentMode);
			if (contextConfigLocation != null) {
				contextConfigLocation = contextConfigLocation.trim();
				if (contextConfigLocation.charAt(0) == '$') {
					contextConfigLocation = resolveConfigLocation(null, contextConfigLocation.substring(1));
				}
			} else {
				contextConfigLocation = "";
			}
			String modulesString = (String) fileproperties.get("Modules");
			if ((modulesString != null) && (modulesString.trim().length() > 0)) {
				String modules[] = modulesString.split(",");
				for (String module : modules) {
					if (module.trim().length() > 0) {
						String moduleConfig = (String) fileproperties.get(module.trim());
						if ((moduleConfig != null) && (moduleConfig.trim().length() > 0)) {
							if (contextConfigLocation.length() > 0) {
								contextConfigLocation = moduleConfig + ", " + contextConfigLocation;
							} else {
								contextConfigLocation = moduleConfig;
							}
						}
					}
				}
			}
			String commonConfig = (String) fileproperties.get("CommonConfig");
			if ((commonConfig != null) && (commonConfig.trim().length() > 0)) {
				if (contextConfigLocation.length() > 0) {
					contextConfigLocation = commonConfig + ", " + contextConfigLocation;
				} else {
					contextConfigLocation = commonConfig;
				}
			}
			return contextConfigLocation;
		} catch (Exception ex) {
			if (logger.isErrorEnabled()) {
				logger.error("CSII PowerEngine can't find " + configFileName);
			}
			throw new PeRuntimeException("pe.cannot_load_peconfig");
		} finally {
			if (inputstream != null) {
				try {
					inputstream.close();
				} catch (IOException ex) {
					// ex.printStackTrace();
					throw ex;
				}
			}

		}

	}

	public void setContextClass(Class contextClass) {
		this.contextClass = contextClass;
	}

	public Class getContextClass() {
		return contextClass;
	}

	public String getNamespace() {
		return nameSpace == null ? getServletName() + "-servlet" : nameSpace;
	}

	public void setNamespace(String namespace) {
		this.nameSpace = namespace;
	}

	public String getServletContextAttributeName() {
		return SERVLET_CONTEXT_PREFIX + getServletName();
	}

	public WebApplicationContext getWebApplicationContext() {
		return applicationContext;
	}

	public void setWebApplicationContext(WebApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setLocaleParamName(String localeParamName) {
		this.localeParamName = localeParamName;
	}
}
