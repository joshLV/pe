package com.csii.pp.clearing.book;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;
import com.csii.pe.core.PeException;
import com.csii.pp.clearing.MerchantTransManager;
import com.csii.pp.clearing.SettlementBooker;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.merchant.MerchantSett;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;

public abstract class AbstractSettlementBooker implements SettlementBooker,SqlMapAware{

	protected MerchantTransManager merchantTransManager;

	protected OrderManager orderManager;

	protected Log log;
	
	protected SqlMapClientOperations sqlMap;

	public AbstractSettlementBooker() {
		super();
		log = LogFactory.getLog(this.getClass());
	}

	/**
	 * 商户支付结算处理
	 * 
	 * @param bookerMap
	 * @param merchant
	 * @param order
	 * 
	 * @version 1.0
	 * @since 1.0
	 */
	protected void merchantPaySettlement(Order order) throws PeException {
		Map<String, Object> bookerMap = new HashMap<String, Object>();
		bookerMap.put(Dict.PPTRANSSEQNO, order.getTransSeqNo());
		
		bookerMap.put(Dict.MERCHANTSEQNO, order.getMerSeqNo());
		bookerMap.put(Dict.MERCHANTID, order.getSubMerchantId());
		bookerMap.put(Dict.PPDEPARTMENTID, order.getDepartmentId());
		bookerMap.put(Dict.PPCLEARDATE, order.getClearDate());
		bookerMap.put(Dict.PPTRANSDATE, order.getTransDate());
		bookerMap.put(Dict.PPBRIEFCODE, Constants.BRIEFCODE_MERCHANT_PAY);
		bookerMap.put(Dict.MERCHANTTRANSTYPE, Constants.MERCHANT_TRANS_PAY);
		Map para = new HashMap();
		para.put("merId", order.getSubMerchantId());
		para.put("payType", order.getPayType());
		para.put("departmentId", order.getDepartmentId());
		MerchantSett merchantSett =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", para);
		Map merPara = new HashMap();
		merPara.put("mer_id",order.getSubMerchantId());
		Merchant merchant = (Merchant)this.sqlMap.queryForObject("pp.core.qryMerId", merPara);
		bookerMap.put(Dict.PPSETTLEMENTSTATUS, Constants.SETT_STATUS_NO);

		bookerMap.put(Dict.PPTRANSAMOUNT, order.getAmount3().subtract(order.getFeeAmount()));

		/*
		 * 收款方帐户信息 商户结算账户
		 */
		bookerMap.put(Dict.PPSETTACCTDEPTID, merchant.getMerSettAcctBankNo());
		bookerMap.put(Dict.PPSETTACCTNO, merchant.getMerSettAcctNo());
		bookerMap.put(Dict.PPSETTACCTKIND, Constants.ACCT_KIND_CURRENT);
		bookerMap.put(Dict.PPSETTACCTTYPE, Constants.ACCT_TYPE_COMMPANY);

		// 结算日期为商户结算周期，结算时根据日期索引查询效率更高
		bookerMap.put(Dict.PPSETTLEMENTDATE, MiscUtil.getSettlementDate(merchantSett.getSettPeriod(), order.getClearDate()));
		bookerMap.put(Dict.PPBANKID, order.getBankId());
		// 记商户结算 中间账户->商户
		merchantTransManager.insertData(bookerMap);
	}
	
	
	
	
	/**
	 * 商户退货结算 处理未结算退货 从商户账户->中间账户
	 * 
	 * @param order
	 * @throws PeException
	 * 
	 * @version 1.0
	 * @since 1.0
	 */
	protected void merchantWithdrawSettlement(Order order) throws PeException {
		/*
		 * 原交易没有结算退货交易，记录给商户转帐的商户台帐明细 （将退货的金额从商户结算帐户转入银行中间帐户）， 需要将结算状态记录为－未结算
		 */
		Map<String, Object> bookerMap = new HashMap<String, Object>();
		bookerMap.put(Dict.PPTRANSSEQNO, order.getTransSeqNo());
		bookerMap.put(Dict.MERCHANTSEQNO, order.getMerSeqNo());
		bookerMap.put(Dict.MERCHANTID, order.getSubMerchantId());
		
		//事实上在记录商户的支付结算的时候，所有的商户的支付结算机构都是核心
		bookerMap.put(Dict.PPDEPARTMENTID, order.getDepartmentId());
		bookerMap.put(Dict.PPCLEARDATE, order.getClearDate());
		bookerMap.put(Dict.PPTRANSDATE, order.getTransDate());
		bookerMap.put(Dict.PPTRANSAMOUNT, (order.getAmount3().subtract(order.getFeeAmount().negate())).negate());

		bookerMap.put(Dict.PPBRIEFCODE,Constants.BRIEFCODE_MERCHANT_WITHDRAW);
		bookerMap.put(Dict.MERCHANTTRANSTYPE, Constants.MERCHANT_TRANS_WITHDRAW);
		Map para = new HashMap();
		para.put("merId", order.getSubMerchantId());
		para.put("payType", order.getPayType());
		para.put("departmentId", order.getDepartmentId());
		MerchantSett merchantSett =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", para);
		Map merPara = new HashMap();
		merPara.put("mer_id",order.getSubMerchantId());
		Merchant merchant = (Merchant)this.sqlMap.queryForObject("pp.core.qryMerId", merPara);

		bookerMap.put(Dict.PPSETTLEMENTSTATUS, Constants.SETT_STATUS_NO);
		/*
		 * 从商户帐户到中间帐户
		 */
		// 付款方信息
		bookerMap.put(Dict.PPSETTACCTDEPTID, merchant.getMerSettAcctBankNo());
		bookerMap.put(Dict.PPSETTACCTNO, merchant.getMerSettAcctNo());
		bookerMap.put(Dict.PPSETTACCTKIND, Constants.ACCT_KIND_CURRENT);
		bookerMap.put(Dict.PPSETTACCTTYPE, Constants.ACCT_TYPE_COMMPANY);
		// 结算日期为商户结算周期，结算时根据日期索引查询效率更高
		bookerMap.put(Dict.PPSETTLEMENTDATE, MiscUtil.getSettlementDate(merchantSett.getSettPeriod(), order.getClearDate()));
		bookerMap.put(Dict.PPBANKID, order.getBankId());
		merchantTransManager.insertData(bookerMap);
	}
	
	

	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}

	/**
	 * @param manager
	 */
	public void setMerchantTransManager(MerchantTransManager manager) {
		merchantTransManager = manager;
	}

	/**
	 * @param manager
	 */
	public void setOrderManager(OrderManager manager) {
		orderManager = manager;
	}

}
