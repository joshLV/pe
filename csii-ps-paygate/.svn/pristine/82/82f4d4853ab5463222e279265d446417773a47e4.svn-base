package com.csii.pe.channel.http;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class QrTransactionIdResolver implements IdentityResolver {

	private String defaultTransName;

	@Override
	public String getIdentity(HttpServletRequest httpservletrequest, Map map) {
		return defaultTransName;
	}

	public void setDefaultTransName(String defaultTransName) {
		this.defaultTransName = defaultTransName;
	}
}