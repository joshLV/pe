/*
 * PayProfitAssigner.java Created on 2007-10-9 13:19:59
 * 
 * Copyright 2004 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pp.clearing.profit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.merchant.MerchantSett;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;

public class WithdrawProfitAssigner extends AbstractProfitAssigner {
	

	/* (non-Javadoc)
	 * @see com.csii.pp.profit.ProfitAssigner#assign(java.lang.Object)
	 */
	public void assign(Object data) throws PeException {
		Order order = (Order) data;
		Order orgOrder = order.getOrgOrder();
		Map<String, Object> transData = new HashMap<String, Object>();
		//计算渠道成本费率
		Map para1 = new HashMap();
		para1.put("bankId", order.getBankId());
		para1.put("payType", order.getPayType());
		para1.put("departmentId", order.getDepartmentId());
		Map passageway = (Map) this.sqlMap.queryForObject("pp.core.queryPassageWay",para1);
		BigDecimal costFeeAmount = order.getAmount().multiply((BigDecimal) passageway.get("rate"));
//		order.setCostFeeAmount(costFeeAmount);
		BigDecimal  parentFeeAmount = order.getFeeAmount().negate();//parentFeeAmount字段代表下级手续费  各级分润金额=下级手续费-本级手续费
//		//计算大商户的分润费用
//		if(!MiscUtil.isNullOrEmpty(order.getMerchantId())){
//			para1.put("merId", order.getMerchantId());
//			MerchantSett merchantSett =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", para1);
//			Merchant  merchant = (Merchant)this.sqlMap.queryForObject("pp.core.queryMerchant", order.getMerchantId());
//			BigDecimal reservedAmt = orgOrder.getAmount2();//可退金额
//	        //剩余保留手续费金额
//			BigDecimal reservedFeeAmt1 = reservedAmt.multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
//			//本次退货后需要保留的商户手续费手续费金额 
//			BigDecimal reservedFeeAmt = (orgOrder.getAmount2().subtract(order.getAmount3())).multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
//			
//			if (reservedFeeAmt.compareTo(reservedFeeAmt1) > 0) {
//				reservedFeeAmt = reservedFeeAmt1;
//			}
//			//本次退货需要返还的分润手续费金额
//			BigDecimal merchantFeeAmount =reservedFeeAmt1.subtract(reservedFeeAmt);
//			BigDecimal merchantProfitAmt =parentFeeAmount.subtract(merchantFeeAmount);
//			parentFeeAmount = merchantFeeAmount;
//			if(merchantProfitAmt.compareTo(Constants.ZERO)!=0){
//			transData.put(Dict.MERCHANTID, order.getMerchantId());
//			transData.put(Dict.MERCHANTTRANSTYPE, Constants.MERCHANT_TRANS_WITHDRAW_PROFIT);
//			transData.put(Dict.PPTRANSAMOUNT, merchantProfitAmt);
//			transData.put(Dict.PPSETTACCTDEPTID,merchant.getMerSettAcctBankNo());   //chb 修改支持多个核心
//			transData.put(Dict.PPSETTACCTNO, merchant.getMerSettAcctNo());    /*chb   修改    2011-10-13*/
//			transData.put(Dict.PPSETTACCTKIND, Constants.ACCT_KIND_CURRENT);
//			transData.put(Dict.PPSETTACCTTYPE, Constants.ACCT_TYPE_COMMPANY);
//			transData.put(Dict.PPSETTLEMENTDATE, MiscUtil.getSettlementDate(merchantSett.getSettPeriod(), order.getClearDate()));
//			if(merchantProfitAmt.compareTo(Constants.ZERO)>0){
//				transData.put(Dict.PPBRIEFCODE, Constants.BRIEFCODE_WITHDRAW_ASSIGN_PROFIT);	
//			}else{
//				transData.put(Dict.PPBRIEFCODE, Constants.BRIEFCODE_SUBSIDIES_ASSIGN_PROFIT_RTN);	
//			}
//			insertMerTrans(order,transData);
//			}else{
//				log.info("["+order.getMerchantId()+"]"+"大商户分润金额为0，不插入记录");
//			}
//		}
		
		//计算金服收益
		Map innerInfo = (Map)this.sqlMap.queryForObject("pp.core.queryInnerInfo",order.getBankId());
		BigDecimal innerProfit = Constants.ZERO;
		Merchant  merchant = (Merchant)this.sqlMap.queryForObject("pp.core.queryMerchant", order.getMerchantId());
		if(merchant==null){
			merchant = (Merchant)this.sqlMap.queryForObject("pp.core.queryMerchant", order.getSubMerchantId());
		}
		if(merchant.getMerInnerFeeAmt()==null){
			throw new PeException(order.getSubMerchantId()+"无金服收益参数");
		}
		BigDecimal reservAmt = orgOrder.getAmount2();//可退金额
		//剩余保留手续费金额
		BigDecimal reservFeeAmt1 = reservAmt.multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchant.getMerInnerFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
		//本次退货后需要保留的商户手续费手续费金额 
		BigDecimal reservFeeAmt = (orgOrder.getAmount2().subtract(order.getAmount3())).multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchant.getMerInnerFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
				
		if (reservFeeAmt.compareTo(reservFeeAmt1) > 0) {
			reservFeeAmt = reservFeeAmt1;
		}
		//本次退货需要返还的分润手续费金额
		innerProfit = reservFeeAmt1.subtract(reservFeeAmt).setScale(2,BigDecimal.ROUND_HALF_UP);
						
		if(innerProfit.compareTo(Constants.ZERO)!=0){
			transData.put(Dict.MERCHANTID, Constants.CSII_JF_MERID);
					
			transData.put(Dict.PPTRANSAMOUNT, innerProfit.negate());
			transData.put(Dict.PPSETTACCTDEPTID,innerInfo.get("acctbankno"));   
			transData.put(Dict.PPSETTACCTNO, innerInfo.get("acctno"));    /*chb   修改    2011-10-13*/
			transData.put(Dict.PPSETTACCTKIND, Constants.ACCT_KIND_CURRENT);
			transData.put(Dict.PPSETTACCTTYPE, Constants.ACCT_TYPE_COMMPANY);
			transData.put(Dict.PPBANKID, order.getBankId());
			transData.put(Dict.PPSETTLEMENTDATE, MiscUtil.getSettlementDate("1", order.getClearDate()));
			transData.put(Dict.PPBRIEFCODE, Constants.BRIEFCODE_WITHDRAW_ASSIGN_PROFIT);	
			transData.put(Dict.MERCHANTTRANSTYPE, Constants.MERCHANT_TRANS_INNER_PROFIT);
			insertMerTrans(order,transData);
		}else{
			log.info("金服支付分润金额为0，不插入记录");
		}
		
		
		//循环计算代理分润金额
		boolean  flag = true;
		Map agtParaMap = new HashMap();
		agtParaMap.put("agentId",order.getAgentid());
		if(!MiscUtil.isNullOrEmpty(order.getAgentid())){
			//获取代理手续费类型
			Map para2 = new HashMap();
			para2.put("merId", order.getAgentid());
			para2.put("payType", order.getPayType());
			para2.put("departmentId", order.getDepartmentId());
			MerchantSett merchantSett1 =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", para2);
			//获取代理的结算账户
			Map agentMap = (Map)this.sqlMap.queryForObject("pp.core.queryAgentInfoById",agtParaMap);
			BigDecimal agentProfitAmount = Constants.ZERO;
			if(!MiscUtil.isNullOrEmpty(agentMap.get("agtSettAcctDeptNo"))&&!"1".equals(agentMap.get("agtHighestFlag"))){
				
				//计算收取代理的费用
				BigDecimal reservedAmt = orgOrder.getAmount2();//可退金额
		        //剩余保留手续费金额0
				BigDecimal reservedFeeAmt1 = reservedAmt.multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett1.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
				//本次退货后需要保留的商户手续费手续费金额 -30
				BigDecimal reservedFeeAmt = (orgOrder.getAmount2().subtract(order.getAmount3())).multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett1.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
				
				if (reservedFeeAmt.compareTo(reservedFeeAmt1) > 0) {
					reservedFeeAmt = reservedFeeAmt1;
				}
				//本次退货需要返还的分润手续费金额
				BigDecimal agentFeeAmount = reservedFeeAmt1.subtract(reservedFeeAmt);
				agentProfitAmount = parentFeeAmount.subtract(agentFeeAmount).setScale(2,BigDecimal.ROUND_HALF_UP);
				parentFeeAmount = agentFeeAmount;
			}
			else{
				//返还 = 当前订单手续费-成本收学费-分润手续费
				BigDecimal amt = parentFeeAmount.subtract(costFeeAmount);
				agentProfitAmount = amt.subtract(innerProfit).setScale(2,BigDecimal.ROUND_HALF_UP);
				flag=false;
			}
			if(agentProfitAmount.compareTo(Constants.ZERO)!=0){
				transData.put(Dict.MERCHANTID, order.getAgentid());
				transData.put(Dict.MERCHANTTRANSTYPE, Constants.MERCHANT_TRANS_PAY_PROFIT);
				transData.put(Dict.PPTRANSAMOUNT, agentProfitAmount.negate());
				transData.put(Dict.PPSETTACCTDEPTID,agentMap.get("agtSettAcctBankNo"));   //chb 修改支持多个核心
				transData.put(Dict.PPSETTACCTNO, agentMap.get("agtSettAcctNo"));    /*chb   修改    2011-10-13*/
				transData.put(Dict.PPSETTACCTKIND, Constants.ACCT_KIND_CURRENT);
				transData.put(Dict.PPSETTACCTTYPE, Constants.ACCT_TYPE_COMMPANY);
				transData.put(Dict.PPBANKID, order.getBankId());
				transData.put(Dict.PPSETTLEMENTDATE, MiscUtil.getSettlementDate(merchantSett1.getSettPeriod(), order.getClearDate()));
				if(agentProfitAmount.compareTo(Constants.ZERO)>0){
					transData.put(Dict.PPBRIEFCODE, Constants.BRIEFCODE_WITHDRAW_ASSIGN_PROFIT);	
				}else{
					transData.put(Dict.PPBRIEFCODE, Constants.BRIEFCODE_SUBSIDIES_ASSIGN_PROFIT_RTN);	
				}
				insertMerTrans(order,transData);	
			}else{
				log.info("["+order.getAgentid()+"]"+"代理分润金额为0，不插入记录");
			}
		}else{
			flag=false;
		}
		//判断代理是否存在上级代理如果存在计算分润，直至最上级代理
		while(flag){
			//获取代理信息
			Map agentMap = (Map)this.sqlMap.queryForObject("pp.core.queryAgentInfoById",agtParaMap);
			//是否存在上级代理
			if(!MiscUtil.isNullOrEmpty(agentMap.get("agtSettAcctDeptNo"))){
				//上机代理存在获取上级代理相关清算信息
				Map tmpMap = new HashMap();
				tmpMap.put("merId", agentMap.get("agtSettAcctDeptNo"));
				tmpMap.put("payType", order.getPayType());
				tmpMap.put("departmentId", order.getDepartmentId());
				MerchantSett tmpSett =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", tmpMap);
				//获取上级代理结算账户信息
				agtParaMap.put("agentId",  agentMap.get("agtSettAcctDeptNo"));
				Map parentAgentMap = (Map)this.sqlMap.queryForObject("pp.core.queryAgentInfoById",agtParaMap);
				BigDecimal transAmt =Constants.ZERO;
				if("1".equals(parentAgentMap.get("agtHighestFlag"))){
					BigDecimal amt = parentFeeAmount.subtract(costFeeAmount);
					transAmt = amt.subtract(innerProfit).setScale(2,BigDecimal.ROUND_HALF_UP);
				}else{
					BigDecimal reservedAmt = orgOrder.getAmount2();//可退金额
			        //剩余保留手续费金额
					BigDecimal reservedFeeAmt1 = reservedAmt.multiply(new BigDecimal(MiscUtil.toStringAndTrim(tmpSett.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
					//本次退货后需要保留的商户手续费手续费金额 
					BigDecimal reservedFeeAmt = (orgOrder.getAmount2().subtract(order.getAmount3())).multiply(new BigDecimal(MiscUtil.toStringAndTrim(tmpSett.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
					
					if (reservedFeeAmt.compareTo(reservedFeeAmt1) > 0) {
						reservedFeeAmt = reservedFeeAmt1;
					}
					//本次退货需要返还的分润手续费金额
					BigDecimal tmpAmt = reservedFeeAmt1.subtract(reservedFeeAmt);
					//每级代理的手续费收入=下级代理手续费-本级代理手续费 
					transAmt = parentFeeAmount.subtract(tmpAmt).setScale(2,BigDecimal.ROUND_HALF_UP);
					parentFeeAmount = tmpAmt;
				}
				
				if(transAmt.compareTo(Constants.ZERO)!=0){
					transData.put(Dict.MERCHANTID, parentAgentMap.get("agtId"));
					
					transData.put(Dict.PPTRANSAMOUNT, transAmt.negate());
					transData.put(Dict.PPSETTACCTDEPTID,parentAgentMap.get("agtSettAcctBankNo"));   //chb 修改支持多个核心
					transData.put(Dict.PPSETTACCTNO, parentAgentMap.get("agtSettAcctNo"));    /*chb   修改    2011-10-13*/
					transData.put(Dict.PPSETTACCTKIND, Constants.ACCT_KIND_CURRENT);
					transData.put(Dict.PPSETTACCTTYPE, Constants.ACCT_TYPE_COMMPANY);
					transData.put(Dict.PPBANKID, order.getBankId());
					transData.put(Dict.PPSETTLEMENTDATE, MiscUtil.getSettlementDate(tmpSett.getSettPeriod(), order.getClearDate()));
					if(transAmt.compareTo(Constants.ZERO)>0){
						transData.put(Dict.PPBRIEFCODE, Constants.BRIEFCODE_WITHDRAW_ASSIGN_PROFIT);	
						transData.put(Dict.MERCHANTTRANSTYPE, Constants.MERCHANT_TRANS_PAY_PROFIT);
					}else{
						transData.put(Dict.PPBRIEFCODE, Constants.BRIEFCODE_SUBSIDIES_ASSIGN_PROFIT_RTN);
						transData.put(Dict.MERCHANTTRANSTYPE, Constants.MERCHANT_TRANS_SUBSIDIES_PROFIT);
					}
					insertMerTrans(order,transData);
				}else{
					log.info("["+parentAgentMap.get("agtId")+"]"+"代理分润金额为0，不插入记录");
				}
				
			}else{
				flag=false;
			}
			
			
		}
		
	}
	
	public void insertMerTrans(Order order,Map transData){
		transData.put(Dict.PPDEPARTMENTID, order.getDepartmentId());
		transData.put(Dict.PPTRANSSEQNO, order.getTransSeqNo());
		transData.put(Dict.PPTRANSDATE, order.getTransDate());
		transData.put(Dict.MERCHANTSEQNO, order.getMerSeqNo());
		transData.put(Dict.PPSETTLEMENTSTATUS, Constants.SETT_STATUS_NO);
		transData.put(Dict.PPCLEARDATE, order.getClearDate());
		transData.put(Dict.PPBANKID, order.getBankId());
		merchantTransManager.insertData(transData);
	}

	/**
	 * @param profitAssigners the profitAssigners to set
	 */

}
