package com.csii.pe.channel.http.servlet;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ApplicationObjectSupport;

import com.csii.pe.channel.http.IdentityResolver;
import com.csii.pe.channel.http.LocalServletContext;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.core.TransactionConfig;
import com.csii.pe.core.TransactionConfigAware;

public class QrContextResolver extends ApplicationObjectSupport implements ContextResolver {

	private static Logger logger = LoggerFactory.getLogger(QrContextResolver.class);

	private String defaultTransaction;

	/**
	 * 创建context
	 */
	private Context creatConext(String transactionId, HttpServletRequest request, Locale locale) {
		Context context = new LocalServletContext(transactionId, request, locale);
		if (context instanceof TransactionConfigAware) {
			TransactionConfig transactionconfig = (TransactionConfig) getApplicationContext().getBean(defaultTransaction);
			((TransactionConfigAware) context).setTransactionConfig(transactionconfig);
		}
		return context;
	}

	@Override
	public Context resolveContext(HttpServletRequest request, Locale locale, IdentityResolver identityresolver) throws PeException {
		Context context = creatConext(identityresolver.getIdentity(request, null), request, locale);
		resolveContext(context, request, locale);
		return context;
	}

	protected void resolveContext(Context context, HttpServletRequest request, Locale locale) throws PeException {
		parse(context, request, locale);
	}

	/**
	 * 将字符数组解析为context
	 */
	private void parse(Context context, HttpServletRequest request, Locale locale) throws PeException {

		String contextPath = request.getContextPath();

		String requestUri = request.getRequestURI();
        logger.info(requestUri);
		String merToken = requestUri.substring(contextPath.length());

		if ((merToken == null) || "".equals(merToken.trim())) {
			logger.error("商户Token为空.");
			throw new PeException("商户Token为空.");
		}
		String[] result = merToken.split("/");
		if (result.length != 3) {
			logger.error("商户Token格式非法.");
			throw new PeException("商户Token格式非法.");
		}

		logger.info("[公众号支付]-商户Token[{}].", result[2]);

		context.setData("merToken", result[2]);
	}

	/**
	 * 获取到客户端IP地址
	 * 
	 * @param request
	 * @return
	 */
	public String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}

	public void setDefaultTransaction(String defaultTransaction) {
		this.defaultTransaction = defaultTransaction;
	}

}
