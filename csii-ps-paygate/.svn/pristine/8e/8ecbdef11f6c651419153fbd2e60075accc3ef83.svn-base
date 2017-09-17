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

public class HttpViewExceptionHandler implements ExceptionHandler {

	public void setUncaughtExceptionCodeMapping(Map map) {
		uncaughtExceptionCodeMapping = map;
	}

	public void setDefaultUncaughtExcetpionCode(String s) {
		defaultuncaughtExceptionCode = s;
	}

	public HttpViewExceptionHandler() {
		defaultErrorView = "defaultError";
		defaultPublicErrorView = "defaultError";
		defaultAjaxErrorView = "validationError";
		exceptionAttribute = "_exception";
		exceptionMessageCaodeAttribute = "_exceptionMessageCode";
		exceptionMessageAttribute = "_exceptionMessage";
		log = LogFactory.getLog(getClass());
		cacheDateForErrorPage = false;
		backToInputValidationError = true;
		maxErrorMessageLength = 0;
		errorMessageEncoding = "UTF-8";
		defaultuncaughtExceptionCode = "pe.error.uncaught";
	}

	protected String resolveViewName(Exception exception, Context context) {
		if (exception instanceof Messageable) {
			String s = ((Messageable) exception).getMessageKey();
			if ((s != null) && (context != null)) {
				TransactionConfig transactionconfig = context.getTransactionConfig();
				if ((transactionconfig != null) && (transactionconfig.getChannels() != null)) {
					Map map = (Map) transactionconfig.getChannels().get("http");
					if (map != null) {
						String s1 = (String) map.get(s);
						if (s1 != null) {
							return s1;
						}
					}
				}
				if (mapping != null) {
					return (String) mapping.get(s);
				}
			}
		}
		return null;
	}

	@Override
	public Object process(ApplicationContext applicationcontext, HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, Locale locale, Context context, Exception exception)
			throws PeException {
		String s = resolveViewName(exception, context);
		if (backToInputValidationError && (exception instanceof ValidationMessage)) {
			if (log.isDebugEnabled()) {
				log.debug(exception);
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
		if (cacheDateForErrorPage) {
			log.error("", exception);
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
		log.error(httpservletrequest.getAttribute("_viewReferer"), exception);
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
		String s = exception.getMessage();
		String s1 = null;
		try {
			Object aobj[] = null;
			String s3 = exception.getMessage();
			if (exception instanceof Messageable) {
				Messageable messageable = (Messageable) exception;
				if (messageable.getMessageKey() != null) {
					s = messageable.getMessageKey();
				}
				s3 = messageable.getDefaultMessage();
				aobj = messageable.getArgs();
				if ((aobj != null) && (aobj.length >= 1)) {
					try {
						aobj[0] = translateFieldName(applicationcontext, aobj[0].toString(), locale, context);
					} catch (Exception exception4) {

					}
				}
				if ((s != null) && (s.length() > 0) && (!messageable.hasDefaultMessage() || s3.equals(s))) {
					try {
						s1 = getMessage(applicationcontext, s, aobj, locale, context);
					} catch (Exception exception5) {
						try {
							s1 = getMessage(applicationcontext, defaultuncaughtExceptionCode, new String[] { s }, locale, context);
						} catch (Exception exception7) {
							s1 = "undefined message, exception code: " + s;
						}
					}
				}
			} else {
				try {
					s1 = getMessage(applicationcontext, s, aobj, locale, context);
				} catch (Exception exception3) {
					s = exception.getClass().getName();
					if (uncaughtExceptionCodeMapping != null) {
						String s5 = (String) uncaughtExceptionCodeMapping.get(s);
						if ((s5 != null) && (s5.length() > 0)) {
							s = s5;
						}
					}
					try {
						s1 = getMessage(applicationcontext, s, new String[] { s }, locale, context);
					} catch (Exception exception6) {
						try {
							s1 = getMessage(applicationcontext, defaultuncaughtExceptionCode, new String[] { s }, locale, context);
						} catch (Exception exception8) {
							s1 = "undefined message, exception type: " + s;
						}
					}
				}
			}
			if (s1 == null) {
				if (s3 != null) {
					s1 = s3;
				} else {
					s1 = s;
				}
			}
		} catch (Exception exception1) {
			s1 = s;
		}
		String s2 = context.getClientInfo().getChannelId();
		if (s2.equals("HTTP.STREAM")) {
			if (messageCodeMapping != null) {
				String s4 = (String) messageCodeMapping.get(s);
				if (s4 != null) {
					s = s4;
				} else if (defaultMappingCode != null) {
					s = defaultMappingCode;
				}
			}
			if (maxErrorMessageLength > 0) {
				try {
					if (s1.getBytes(errorMessageEncoding).length > maxErrorMessageLength) {
						byte abyte0[] = s1.getBytes(errorMessageEncoding);
						s1 = new String(abyte0, 0, maxErrorMessageLength);
					}
				} catch (Exception exception2) {
					s1 = s;
				}
			}
		}
		hashmap.put(exceptionMessageCaodeAttribute, s);
		hashmap.put(exceptionMessageAttribute, s1);
		return hashmap;
	}

	protected String translateFieldName(ApplicationContext applicationcontext, String s, Locale locale, Context context) {
		int j = s.indexOf('[');
		if (j > 0) {
			String s1 = s.substring(0, j);
			String s2 = getMessage(applicationcontext, s1, null, s1, locale, context);
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
			return getFieldNameMessage(applicationcontext, s, locale, context);
		}
	}

	protected String getFieldNameMessage(ApplicationContext applicationcontext, String s, Locale locale, Context context) {
		return applicationcontext.getMessage(s, null, s, locale);
	}

	protected String getMessage(ApplicationContext applicationcontext, String s, Object aobj[], String s1, Locale locale, Context context) {
		return applicationcontext.getMessage(s, aobj, s1, locale);
	}

	protected String getMessage(ApplicationContext applicationcontext, String s, Object aobj[], Locale locale, Context context) {
		return applicationcontext.getMessage(s, aobj, locale);
	}

	public void setDefaultErrorView(String s) {
		defaultErrorView = s;
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
		cacheDateForErrorPage = flag;
	}

	public void setBackToInputForValidationError(boolean flag) {
		backToInputValidationError = flag;
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
		exceptionMessageCaodeAttribute = s;
	}

	public void setMaxErrorMessageLength(int j) {
		maxErrorMessageLength = j;
	}

	public void setMessageCodeMapping(Map map) {
		messageCodeMapping = map;
	}

	private Map mapping;
	private String defaultErrorView;
	private String defaultPublicErrorView;
	private String defaultAjaxErrorView;
	private String exceptionAttribute;
	private String exceptionMessageCaodeAttribute;
	private String exceptionMessageAttribute;
	private Log log;
	private boolean cacheDateForErrorPage;
	private boolean backToInputValidationError;
	private Map messageCodeMapping;
	private String defaultMappingCode;
	private int maxErrorMessageLength;
	private String errorMessageEncoding;
	private Map uncaughtExceptionCodeMapping;
	private String defaultuncaughtExceptionCode;
}
