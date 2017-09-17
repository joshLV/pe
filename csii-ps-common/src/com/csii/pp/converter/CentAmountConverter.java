package com.csii.pp.converter;

import java.math.BigDecimal;

import com.csii.pe.validation.convert.AbstractNumberConverter;

public class CentAmountConverter extends AbstractNumberConverter {

	public CentAmountConverter() {
		setType(java.math.BigDecimal.class);
	}

	@Override
	protected Object internalConvert(Object obj) throws Exception {
		obj = super.internalConvert(obj);
		return new BigDecimal(obj.toString());
	}

	public static String getCent(String amount) {
		if (com.csii.pp.util.MiscUtil.isNullOrEmpty(amount)) {
			return "0";
		}
		String value = new BigDecimal(amount).multiply(new BigDecimal(100)).toString();
		return value.indexOf(".") == -1 ? value : value.substring(0, value.indexOf("."));
	}

	public static String getCent(BigDecimal amount) {
		String stramount = amount.multiply(new BigDecimal(100)).toString();
		return stramount.indexOf(".") == -1 ? stramount : stramount.substring(0, stramount.indexOf("."));
	}
}
