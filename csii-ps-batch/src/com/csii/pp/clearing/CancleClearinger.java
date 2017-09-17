/*
 * @(#)CashClearor.java	1.0 2008-6-19
 *
 * Copyright 2008 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */ 
package com.csii.pp.clearing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.merchant.MerchantSett;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;

/**
 * CashClearor.java
 * <p>
 * Created on 2008-6-19 
 * Modification history	
 * <p>
 * @author	Li Hui, Electronic Payment System Group, CSII
 * @version	1.0
 * @since	1.0
 */
public class CancleClearinger extends AbstractClearinger {

	/**
	 * 
	 */
	public CancleClearinger() {
		super();
	}
	
	public void clear(Object data) throws PeException {

		Order order = (Order) data;
		Map para = new HashMap();
		para.put("merId", order.getSubMerchantId());
		para.put("payType", order.getPayType());
		para.put("departmentId", order.getDepartmentId());
		MerchantSett merchantSett =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", para);
		Merchant  merchant = (Merchant)this.sqlMap.queryForObject("pp.core.queryMerchant", order.getSubMerchantId());
//		BigDecimal feeAmount = feeCalcFactory.getFeeCalc(merchantSett).calculate(merchantSett, order.getAmount());
//		BigDecimal feeAmount = order.getAmount().multiply(merchantSett.getFeeAmt()).setScale(2,BigDecimal.ROUND_HALF_UP);
//		order.setFeeAmount(feeAmount.negate());
//		Map para1 = new HashMap();
//		para1.put("bankId", order.getBankId());
//		para1.put("payType", order.getPayType());
//		para1.put("departmentId", order.getDepartmentId());
//		Map passageway = (Map) this.sqlMap.queryForObject("pp.core.queryPassageWay",para1);
//		BigDecimal costFeeAmount = order.getAmount().multiply((BigDecimal) passageway.get("rate"));
//		order.setCostFeeAmount(costFeeAmount.negate());
//		if(!MiscUtil.isNullOrEmpty(order.getAgentid())){
//			Map para2 = new HashMap();
//			para2.put("merId", order.getAgentid());
//			para2.put("payType", order.getPayType());
//			para2.put("departmentId", order.getDepartmentId());
//			MerchantSett merchantSett1 =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", para2);
//			BigDecimal agentFeeAmount = order.getAmount().multiply(merchantSett.getFeeAmt().subtract(merchantSett1.getFeeAmt())).setScale(2,BigDecimal.ROUND_HALF_UP);
//			order.setAgentFeeAmount(agentFeeAmount.negate());
//		}
		Date settDate = MiscUtil.getSettlementDate(merchantSett.getSettPeriod(), order.getClearDate());
		Map map = new HashMap();
		map.put("TransSeqNo", order.getTransSeqNo());
		map.put("TransDate", order.getTransDate());
		map.put("TransDateTime", order.getTransDateTime());
		map.put("MerchantId", order.getMerchantId());
		map.put("SubMerchantId", order.getSubMerchantId());
		map.put("TransAmount", order.getAmount().negate());
		map.put("FeeAmount", order.getFeeAmount());
		map.put("TransType", order.getTransType());
		map.put("ClearDate", order.getClearDate());
		map.put("DepartmentId", order.getDepartmentId());
		map.put("SettStatus", "0");
		map.put("PayType", order.getPayType());
		map.put("SettAcctNo", merchant.getMerSettAcctNo());
		map.put("SettAcctBankNo", merchant.getMerSettAcctBankNo());
		map.put("SettDate", MiscUtil.rolDate(order.getClearDate(), -1));
		map.put("BankId", merchant.getMerBankId());
		sqlMap.insert("pp.batch.insertBtMerTrans",map);

		//如果订单状态没有被设置为已结算,则置为清分成功
		if(!Constants.TRANS_STEP_SETT_OK.equals(order.getStep())){
			order.setStep(Constants.TRANS_STEP_CLEAR_OK);
		}
		

		//更新清算明细表的处理阶段
		orderManager.updateClearOrder(order);

		//更新历史交易明细表的处理阶段
		orderManager.updateHistoryOrder(order);
	}

}
