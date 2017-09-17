package com.csii.pp.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.csii.pp.merchant.Merchant;

public class Order {

	private Merchant merchant;// 商户信息

	private String merchantId;
	private String subMerchantId;

	private Order orgOrder;// 退货原订单
	private String transSeqNo;// 支付平台流水号
	private Date transDate;// 支付平台交易日期
	private Timestamp transDateTime;// 支付平台交易时间
	private String merSeqNo; // 商户订单号
	private Date merTransDate;// 商户交易日期
	private Timestamp merTransDateTime;// 商户交易时间
	private String hostSeqNo;// 核心系统流水号
	private Date hostDate;// 核心交易日期
	private Timestamp hostDateTime;// 核心交易时间
	private String orgMerSeqNo; // 原交易流水号
	private Date orgMerTransDate;// 原商户交易日期
	private String orgTransSeqNo;// 原支付平台流水号
	private Date orgTransDate;// 原支付平台日期
	private Date orgTransDateTime;// 原支付平台日期时间
	private String orgHostSeqNo;// 核心系统流水号
	private Date orgHostDate;// 核心交易日期
	private String transId; // 交易代码
	private BigDecimal amount;// 交易金额
	private String currency;// 币种
	private BigDecimal amount1;// 已退货金额
	private BigDecimal amount2;// 未退货金额
	private BigDecimal amount3;// 预留金额1
	private BigDecimal amount4;// 预留金额2
	private BigDecimal feeAmount;// 手续费金额
	private BigDecimal costFeeAmount;//成本手续费
	private BigDecimal agentFeeAmount;//代理商分润
	private String returnCode;// 支付平台响应码
	private String returnMsg;// 订单错误信息
	private String status;
	private String transType;// 交易类型
	private String step;// 交易处理步骤
	private String remark1;// 备注1
	private String remark2;// 备注2
	private String appid;
	private String openid;
	private String bankType;
	private String bankId;
	private Date clearDate;
	private String departmentId;
	private String agentid;
	private String payType;
	private String cashierId;
	private String procStatus;
	private String cashierName;
	private String merWxId;
	private String merMemId;

	public String getMerMemId() {
		return merMemId;
	}

	public void setMerMemId(String merMemId) {
		this.merMemId = merMemId;
	}

	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerSeqNo() {
		return merSeqNo;
	}

	public void setMerSeqNo(String merSeqNo) {
		this.merSeqNo = merSeqNo;
	}

	public Date getMerTransDate() {
		return merTransDate;
	}

	public void setMerTransDate(Date merTransDate) {
		this.merTransDate = merTransDate;
	}

	public Timestamp getMerTransDateTime() {
		return merTransDateTime;
	}

	public void setMerTransDateTime(Timestamp merTransDateTime) {
		this.merTransDateTime = merTransDateTime;
	}

	public String getHostSeqNo() {
		return hostSeqNo;
	}

	public void setHostSeqNo(String hostSeqNo) {
		this.hostSeqNo = hostSeqNo;
	}

	public Date getHostDate() {
		return hostDate;
	}

	public void setHostDate(Date hostDate) {
		this.hostDate = hostDate;
	}

	public Timestamp getHostDateTime() {
		return hostDateTime;
	}

	public void setHostDateTime(Timestamp hostDateTime) {
		this.hostDateTime = hostDateTime;
	}

	public String getOrgMerSeqNo() {
		return orgMerSeqNo;
	}

	public void setOrgMerSeqNo(String orgMerSeqNo) {
		this.orgMerSeqNo = orgMerSeqNo;
	}

	public Date getOrgMerTransDate() {
		return orgMerTransDate;
	}

	public void setOrgMerTransDate(Date orgMerTransDate) {
		this.orgMerTransDate = orgMerTransDate;
	}

	public String getOrgHostSeqNo() {
		return orgHostSeqNo;
	}

	public void setOrgHostSeqNo(String orgHostSeqNo) {
		this.orgHostSeqNo = orgHostSeqNo;
	}

	public Date getOrgHostDate() {
		return orgHostDate;
	}

	public void setOrgHostDate(Date orgHostDate) {
		this.orgHostDate = orgHostDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount1() {
		return amount1;
	}

	public void setAmount1(BigDecimal amount1) {
		this.amount1 = amount1;
	}

	public BigDecimal getAmount2() {
		return amount2;
	}

	public void setAmount2(BigDecimal amount2) {
		this.amount2 = amount2;
	}

	public BigDecimal getAmount3() {
		return amount3;
	}

	public void setAmount3(BigDecimal amount3) {
		this.amount3 = amount3;
	}

	public BigDecimal getAmount4() {
		return amount4;
	}

	public void setAmount4(BigDecimal amount4) {
		this.amount4 = amount4;
	}

	public BigDecimal getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public Order getOrgOrder() {
		return orgOrder;
	}

	public void setOrgOrder(Order orgOrder) {
		this.orgOrder = orgOrder;
	}

	public String getTransSeqNo() {
		return transSeqNo;
	}

	public void setTransSeqNo(String transSeqNo) {
		this.transSeqNo = transSeqNo;
	}

	public Date getTransDate() {
		return transDate;
	}

	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}

	public Timestamp getTransDateTime() {
		return transDateTime;
	}

	public void setTransDateTime(Timestamp transDateTime) {
		this.transDateTime = transDateTime;
	}

	public String getOrgTransSeqNo() {
		return orgTransSeqNo;
	}

	public void setOrgTransSeqNo(String orgTransSeqNo) {
		this.orgTransSeqNo = orgTransSeqNo;
	}

	public Date getOrgTransDate() {
		return orgTransDate;
	}

	public void setOrgTransDate(Date orgTransDate) {
		this.orgTransDate = orgTransDate;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public String getSubMerchantId() {
		return subMerchantId;
	}

	public void setSubMerchantId(String subMerchantId) {
		this.subMerchantId = subMerchantId;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public Date getClearDate() {
		return clearDate;
	}

	public void setClearDate(Date clearDate) {
		this.clearDate = clearDate;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getCashierId() {
		return cashierId;
	}

	public void setCashierId(String cashierId) {
		this.cashierId = cashierId;
	}

	public Date getOrgTransDateTime() {
		return orgTransDateTime;
	}

	public void setOrgTransDateTime(Date orgTransDateTime) {
		this.orgTransDateTime = orgTransDateTime;
	}

	public String getProcStatus() {
		return procStatus;
	}

	public void setProcStatus(String procStatus) {
		this.procStatus = procStatus;
	}

	public BigDecimal getCostFeeAmount() {
		return costFeeAmount;
	}

	public void setCostFeeAmount(BigDecimal costFeeAmount) {
		this.costFeeAmount = costFeeAmount;
	}

	public BigDecimal getAgentFeeAmount() {
		return agentFeeAmount;
	}

	public void setAgentFeeAmount(BigDecimal agentFeeAmount) {
		this.agentFeeAmount = agentFeeAmount;
	}

	public String getCashierName() {
		return cashierName;
	}

	public void setCashierName(String cashierName) {
		this.cashierName = cashierName;
	}

	public String getMerWxId() {
		return merWxId;
	}

	public void setMerWxId(String merWxId) {
		this.merWxId = merWxId;
	}
	
	
	
}
