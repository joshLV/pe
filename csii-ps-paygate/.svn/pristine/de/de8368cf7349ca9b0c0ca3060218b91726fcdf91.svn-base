package com.csii.pe.channel.http.servlet;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.csii.pe.accesscontrol.dc.ResubmitControlItem;
import com.csii.pe.accesscontrol.dc.ResubmitException;
import com.csii.pe.core.Context;
import com.csii.pe.core.Messageable;
import com.csii.pe.core.OriginalDataInterface;
import com.csii.pe.core.PeException;
import com.csii.pe.core.TransactionConfig;
import com.csii.pe.core.User;
import com.csii.pe.core.ValidationMessage;

public class HttpJsonViewExceptionHandler implements ExceptionHandler {
	private Log logger;
	private Map mapping;
	private String defaultErrorView;
	private String defaultPublicErrorView;
	private String defaultAjaxErrorView;
	private String exceptionAttribute;
	private String exceptionMessageCodeAttribute;
	private String exceptionMessageAttribute;
	private boolean cacheDataForErrorPage;
	private boolean backToInputForValidationError;
	private Map messageCodeMapping;
	private String defaultMappingCode;
	private int maxErrorMessageLength;
	private String errorMessageEncoding;
	private Map uncaughtExceptionCodeMapping;
	private String defaultUncaughtExceptioinCode;

	public HttpJsonViewExceptionHandler() {
		logger = LogFactory.getLog(getClass());

		defaultErrorView = "weixin.streamView,";
		defaultPublicErrorView = "weixin.streamView,";
		defaultAjaxErrorView = "weixin.streamView,";

		exceptionAttribute = "_exception";

		exceptionMessageCodeAttribute = "_exceptionMessageCode";
		exceptionMessageAttribute = "_exceptionMessage";

		cacheDataForErrorPage = false;
		backToInputForValidationError = true;

		maxErrorMessageLength = 0;

		errorMessageEncoding = "UTF-8";

		defaultUncaughtExceptioinCode = "pe.error.uncaught";
	}

	protected String resolveViewName(Exception exception, Context context) {
		String resolveViewName = null;
		if (exception instanceof Messageable) {
			String s = ((Messageable) exception).getMessageKey();
			if ((s != null) && (context != null)) {
				TransactionConfig transactionconfig = context.getTransactionConfig();
				if ((transactionconfig != null) && (transactionconfig.getChannels() != null)) {
					Map map = (Map) transactionconfig.getChannels().get("http");
					if (map != null) {
						String s1 = (String) map.get(s);
						if (s1 != null) {
							resolveViewName = s1;
						}
					}
				}
				if (mapping != null) {
					resolveViewName = (String) mapping.get(s);
				}
			}
		}

		return resolveViewName;
	}

	@SuppressWarnings("null")
	public Object process(ApplicationContext applicationcontext, HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, Locale locale, Context context, Exception exception)
			throws PeException {

		String s = resolveViewName(exception, context);

		if (backToInputForValidationError && (exception instanceof ValidationMessage)) {

			if (logger.isDebugEnabled()) {
				logger.debug(exception);
			}

			if (s != null) {
				httpservletrequest.setAttribute("_viewReferer", s);
			} else {
				String s1 = httpservletrequest.getHeader("PE-AJAX");
				if (s1 != null) {
					httpservletrequest.setAttribute("_viewReferer", defaultAjaxErrorView);
				}
			}

			String s2 = (String) httpservletrequest.getAttribute("_viewReferer");

			if ((s2 != null) && (context instanceof OriginalDataInterface)) {
				Map map = ((OriginalDataInterface) context).getOriginalData();
				Map map4 = resolverRejectMessages(applicationcontext, httpservletrequest, locale, exception, context);
				map.putAll(map4);
				return map;
			}

		} else if (exception instanceof ResubmitException) {
			ResubmitControlItem resubmitcontrolitem = (ResubmitControlItem) context.getData("_resubmitControlItem");
			httpservletrequest.setAttribute("_viewReferer", resubmitcontrolitem.getViewName());
			return resubmitcontrolitem.getModel();
		}

		if (cacheDataForErrorPage) {
			logger.error(exception.getMessage(), exception);

			if (s == null) {
				if (context != null) {
					User user = context.getUser();
					if ((user == null) || user.isLogout()) {
						httpservletrequest.setAttribute("_viewReferer", defaultPublicErrorView);
					} else {
						httpservletrequest.setAttribute("_viewReferer", defaultErrorView);
					}
				} else {
					httpservletrequest.setAttribute("_viewReferer", defaultErrorView);
				}
			} else {
				httpservletrequest.setAttribute("_viewReferer", s);
			}

			if (context instanceof OriginalDataInterface) {

				Object obj;

				if (context != null) {
					obj = context.getDataMap();
				} else {
					obj = new HashMap(3);
				}

				Map map1 = resolverRejectMessages(applicationcontext, httpservletrequest, locale, exception, context);
				((Map) (obj)).putAll(map1);

				Map map5 = ((OriginalDataInterface) context).getOriginalData();
				map5.put("_viewReferer", httpservletrequest.getParameter("_viewReferer"));

				((Map) (obj)).put("_dataMap", map5);

				return obj;
			}

			Object obj1;

			if (context != null) {
				obj1 = context.getDataMap();
			} else {
				obj1 = new HashMap(3);
			}

			Map map2 = resolverRejectMessages(applicationcontext, httpservletrequest, locale, exception, context);
			((Map) (obj1)).putAll(map2);

			return obj1;
		}
		if (s == null) {
			if (context != null) {
				User user1 = context.getUser();
				if ((user1 == null) || user1.isLogout()) {
					httpservletrequest.setAttribute("_viewReferer", defaultPublicErrorView);
				} else {
					httpservletrequest.setAttribute("_viewReferer", defaultErrorView);
				}
			} else {
				httpservletrequest.setAttribute("_viewReferer", defaultErrorView);
			}
		} else {
			httpservletrequest.setAttribute("_viewReferer", s);
		}

		logger.error(httpservletrequest.getAttribute("_viewReferer"), exception);

		Object obj2;

		if (context != null) {
			TransactionConfig transactionconfig = context.getTransactionConfig();
			if (transactionconfig == null) {
				obj2 = new HashMap(3);
			} else {
				obj2 = context.getDataMap();
			}
		} else {
			obj2 = new HashMap(3);
		}

		Map map3 = resolverRejectMessages(applicationcontext, httpservletrequest, locale, exception, context);
		((Map) (obj2)).putAll(map3);

		return obj2;
	}

	protected Map resolverRejectMessages(ApplicationContext applicationcontext, HttpServletRequest httpservletrequest, Locale locale, Exception exception, Context context) {
		HashMap hashmap = new HashMap(3);

		hashmap.put(exceptionAttribute, exception);
		String errorCode = exception.getMessage();

		String errorMsg = null;

		try {
			Object aobj[] = null;
			String s3 = exception.getMessage();

			if (exception instanceof Messageable) {
				Messageable messageable = (Messageable) exception;

				if (messageable.getMessageKey() != null) {
					errorCode = messageable.getMessageKey();
				}

				s3 = messageable.getDefaultMessage();

				aobj = messageable.getArgs();

				if ((aobj != null) && (aobj.length >= 1)) {
					try {
						aobj[0] = translateFieldName(applicationcontext, aobj[0].toString(), locale, context);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}

				if ((errorCode != null) && (errorCode.length() > 0) && (!messageable.hasDefaultMessage() || s3.equals(errorCode))) {
					try {
						errorMsg = getMessage(applicationcontext, errorCode, aobj, locale);
					} catch (Exception exception5) {
						try {
							errorMsg = getMessage(applicationcontext, defaultUncaughtExceptioinCode, new String[] { errorCode }, locale);
						} catch (Exception exception7) {
							errorMsg = "undefined message, exception code: " + errorCode;
						}
					}
				}
			} else {
				try {
					errorMsg = getMessage(applicationcontext, errorCode, aobj, locale);
				} catch (Exception exception3) {

					errorCode = exception.getClass().getName();

					if (uncaughtExceptionCodeMapping != null) {
						String s5 = (String) uncaughtExceptionCodeMapping.get(errorCode);
						if ((s5 != null) && (s5.length() > 0)) {
							errorCode = s5;
						}
					}

					try {
						errorMsg = getMessage(applicationcontext, errorCode, new String[] { errorCode }, locale);
					} catch (Exception exception6) {
						try {
							errorMsg = getMessage(applicationcontext, defaultUncaughtExceptioinCode, new String[] { errorCode }, locale);
						} catch (Exception exception8) {
							errorMsg = "undefined message, exception type: " + errorCode;
						}
					}
				}
			}
			if (errorMsg == null) {
				if (s3 != null) {
					errorMsg = s3;
				} else {
					errorMsg = errorCode;
				}
			}
		} catch (Exception exception1) {
			errorMsg = errorCode;
		}

		String s2 = context.getClientInfo().getChannelId();

		if (s2.equals("HTTP.STREAM")) {
			if (messageCodeMapping != null) {
				String s4 = (String) messageCodeMapping.get(errorCode);
				if (s4 != null) {
					errorCode = s4;
				} else if (defaultMappingCode != null) {
					errorCode = defaultMappingCode;
				}
			}

			if (maxErrorMessageLength > 0) {
				try {
					if (errorMsg.getBytes(errorMessageEncoding).length > maxErrorMessageLength) {
						byte abyte0[] = errorMsg.getBytes(errorMessageEncoding);
						errorMsg = new String(abyte0, 0, maxErrorMessageLength);
					}
				} catch (Exception exception2) {
					errorMsg = errorCode;
				}
			}
		}

		hashmap.put(exceptionMessageCodeAttribute, errorCode);
		hashmap.put(exceptionMessageAttribute, errorMsg);

		return hashmap;
	}

	protected String translateFieldName(ApplicationContext applicationcontext, String s, Locale locale, Context context) {
		int j = s.indexOf('[');

		if (j > 0) {
			String s1 = s.substring(0, j);
			String s2 = getMessage(applicationcontext, s1, null, s1, locale);
			String s3 = applicationcontext.getMessage("[", null, "No.", locale);
			String s4 = applicationcontext.getMessage("]", null, "Record", locale);
			String s5 = applicationcontext.getMessage(".", null, " ", locale);

			int k = s.indexOf(']');

			if ((k > 0) && (k > j)) {
				s2 = s3 + (Integer.parseInt(s.substring(j + 1, k)) + 1) + s4 + s2 + s5 + translateFieldName(applicationcontext, s.substring(k + 2), locale, context);
			} else {
				s2 = s2 + s.substring(j);
			}

			return s2;

		} else {
			return getFieldNameMessage(applicationcontext, s, locale);
		}
	}

	protected String getFieldNameMessage(ApplicationContext applicationcontext, String s, Locale locale) {
		return applicationcontext.getMessage(s, null, s, locale);
	}

	protected String getMessage(ApplicationContext applicationcontext, String s, Object aobj[], String s1, Locale locale) {
		return applicationcontext.getMessage(s, aobj, s1, locale);
	}

	protected String getMessage(ApplicationContext applicationcontext, String s, Object aobj[], Locale locale) {
		return applicationcontext.getMessage(s, aobj, locale);
	}

	public void setDefaultErrorView(String defaultErrorView) {
		this.defaultErrorView = defaultErrorView;
	}

	public void setMapping(Map map) {
		mapping = map;
	}

	public void setExceptionAttribute(String s) {
		exceptionAttribute = s;
	}

	public void setExceptionMessageAttribute(String s) {
		exceptionMessageAttribute = s;
	}

	public void setDefaultAjaxErrorView(String s) {
		defaultAjaxErrorView = s;
	}

	public void setCacheDataForErrorPage(boolean flag) {
		cacheDataForErrorPage = flag;
	}

	public void setBackToInputForValidationError(boolean flag) {
		backToInputForValidationError = flag;
	}

	public void setDefaultPublicErrorView(String s) {
		defaultPublicErrorView = s;
	}

	public void setDefaultMappingCode(String s) {
		defaultMappingCode = s;
	}

	public void setErrorMessageEncoding(String s) {
		errorMessageEncoding = s;
	}

	public void setExceptionMessageCodeAttribute(String s) {
		exceptionMessageCodeAttribute = s;
	}

	public void setMaxErrorMessageLength(int j) {
		maxErrorMessageLength = j;
	}

	public void setMessageCodeMapping(Map map) {
		messageCodeMapping = map;
	}

	public void setUncaughtExceptionCodeMapping(Map map) {
		uncaughtExceptionCodeMapping = map;
	}

	public void setDefaultUncaughtExcetpionCode(String s) {
		defaultUncaughtExceptioinCode = s;
	}
}
