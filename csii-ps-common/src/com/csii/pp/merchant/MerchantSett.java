package com.csii.pp.merchant;

import java.math.BigDecimal;

import com.csii.pp.dict.Constants;

public class MerchantSett {
	/*
	 * 商户结算周期
	 */
	public static final String SETT_PERIOD_REALTIME = Constants.PERIOD_REALTIME; // 实时

	public static final String SETT_PERIOD_DAY = Constants.PERIOD_DAY; // 日

	public static final String SETT_PERIOD_WEEK = Constants.PERIOD_WEEK; // 周

	public static final String SETT_PERIOD_HALFMONTH = Constants.PERIOD_HALFMONTH; // 半月

	public static final String SETT_PERIOD_MONTH = Constants.PERIOD_MONTH; // 月

	public static final String SETT_PERIOD_SEASON = Constants.PERIOD_SEASON; // 季

	public static final String SETT_PERIOD_HALFYEAR = Constants.PERIOD_HALFYEAR; // 半年

	public static final String SETT_PERIOD_YEAR = Constants.PERIOD_YEAR; // 年
	
	private String id;
	private String transId;
	private String feeType;
	private BigDecimal feeAmt;
	private String settPeriod;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public BigDecimal getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(BigDecimal feeAmt) {
		this.feeAmt = feeAmt;
	}
	public String getSettPeriod() {
		return settPeriod;
	}
	public void setSettPeriod(String settPeriod) {
		this.settPeriod = settPeriod;
	}
	
	
	

}
