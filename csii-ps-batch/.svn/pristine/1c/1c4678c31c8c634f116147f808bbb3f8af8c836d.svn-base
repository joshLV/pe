package com.csii.pp.clearing.fee;

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

/**
 * 支付交易手续费收取
 */
public class PayFeeReceiver extends AbstractFeeReceiver {
	
	public void receive(Object data) throws PeException {
		Order order = (Order) data;
		Map para = new HashMap();
		para.put("merId", order.getSubMerchantId());
		para.put("payType", order.getPayType());
		para.put("departmentId", order.getDepartmentId());
		MerchantSett merchantSett =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", para);
		Merchant  merchant = (Merchant)this.sqlMap.queryForObject("pp.core.queryMerchant", order.getSubMerchantId());
		BigDecimal feeAmount = feeCalcFactory.getFeeCalc(merchantSett).calculate(merchantSett, order.getAmount3());
		
		if (order.getAmount3().compareTo(feeAmount) < 0) {
			//差额结算如果交易金额小于手续费金额，则全部收取
			feeAmount = order.getAmount3();
		}
		order.setFeeAmount(feeAmount);

//			Map<String, Object> transData = new HashMap<String, Object>();
//			transData.put(Dict.PPTRANSSEQNO, order.getTransSeqNo());
//			transData.put(Dict.MERCHANTSEQNO, order.getMerSeqNo());
//			transData.put(Dict.MERCHANTID, order.getSubMerchantId());
//			transData.put(Dict.MERCHANTTRANSTYPE, Constants.MERCHANT_TRANS_FEE_RCV);
//			transData.put(Dict.PPTRANSAMOUNT, feeAmount);
//			transData.put(Dict.PPSETTLEMENTSTATUS, Constants.SETT_STATUS_NO);
//			transData.put(Dict.PPTRANSDATE, order.getTransDate());
//			transData.put(Dict.PPDEPARTMENTID, order.getDepartmentId());
//			transData.put(Dict.PPCLEARDATE, order.getClearDate());
//			transData.put(Dict.PPBANKID, order.getBankId());
//			
//			//收款方帐户信息
//			//收款方账户为中间账户
////			transData.put(Dict.PPRCVACCTDEPTID, Constants.HOST_SYSTEM_DEPARTMENTID);
////			transData.put(Dict.PPRCVACCTDEPTID,departmentEntity.getDepartmentId());   //chb 修改支持多个核心
////			transData.put(Dict.PPRCVACCTNO, order.getInnerAccNo());    /*chb   修改    2011-10-13*/
////			transData.put(Dict.PPRCVACCTKIND, Constants.ACCT_KIND_CURRENT);
////			transData.put(Dict.PPRCVACCTTYPE, Constants.ACCT_TYPE_INNER);
//
//			//付款方帐户信息
//			//这里付款方帐户机构记载的是商户的开户机构，有可能和商户结算帐户的实际机构不一致
//			//付款帐号为商户的手续费结算帐号
//			transData.put(Dict.PPPAYACCTDEPTID, merchant.getMerSettAcctBankNo());
//			transData.put(Dict.PPPAYACCTNO, merchant.getMerSettAcctNo());
//			transData.put(Dict.PPPAYACCTKIND, Constants.ACCT_KIND_CURRENT);  //chb  需要注意和业务确认
//			transData.put(Dict.PPPAYACCTTYPE, Constants.ACCT_TYPE_COMMPANY); //chb  需要注意和业务确认
//			
//			//根据商户手续费结算周期计算结算日期，结算时根据日期索引查询效率更高
//			transData.put(Dict.PPSETTLEMENTDATE, MiscUtil.getSettlementDate("0", order.getClearDate()));
//			transData.put(Dict.PPBRIEFCODE, Constants.BRIEFCODE_MERCHANT_FEE_RCV);
//			
//			merchantTransManager.insertData(transData);
		}
	}
