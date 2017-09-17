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
public class WithdrawFeeReceiver extends AbstractFeeReceiver {
	
	public void receive(Object data) throws PeException {
		Order order = (Order) data;
		Order orgOrder = order.getOrgOrder();
		Map para = new HashMap();
		para.put("merId", order.getSubMerchantId());
		para.put("payType", order.getPayType());
		para.put("departmentId", order.getDepartmentId());
		MerchantSett merchantSett =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", para);
		Merchant  merchant = (Merchant)this.sqlMap.queryForObject("pp.core.queryMerchant", order.getSubMerchantId());
		
		BigDecimal reservedAmt = orgOrder.getAmount2();//可退金额
        //可退金额+此笔退款金额的商户手续费
		BigDecimal reservedFeeAmt1 = reservedAmt.multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
		//本次退货后需要保留的商户手续费手续费金额 
		BigDecimal reservedFeeAmt = (orgOrder.getAmount2().subtract(order.getAmount3())).multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
		
		if (reservedFeeAmt.compareTo(reservedFeeAmt1) > 0) {
			reservedFeeAmt = reservedFeeAmt1;
		}
		//本次退货需要返还的手续费金额
		BigDecimal feeAmount = reservedFeeAmt1.subtract(reservedFeeAmt);
//		order.setFeeAmount(feeAmount);//商户手续费
		

//			Map<String, Object> transData = new HashMap<String, Object>();
//			transData.put(Dict.PPTRANSSEQNO, order.getTransSeqNo());
//			transData.put(Dict.MERCHANTSEQNO, order.getMerSeqNo());
//			transData.put(Dict.MERCHANTID, order.getSubMerchantId());
//			transData.put(Dict.MERCHANTTRANSTYPE, Constants.MERCHANT_TRANS_FEE_RETURN);
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
//			//收款方帐户信息
//			transData.put(Dict.PPRCVACCTDEPTID, merchant.getMerSettAcctBankNo());
//			transData.put(Dict.PPRCVACCTNO, merchant.getMerSettAcctNo());
//			transData.put(Dict.PPRCVACCTKIND, Constants.ACCT_KIND_CURRENT);  //chb  需要注意和业务确认
//			transData.put(Dict.PPRCVACCTTYPE, Constants.ACCT_TYPE_COMMPANY); //chb  需要注意和业务确认
//			
//			//根据商户手续费结算周期计算结算日期，结算时根据日期索引查询效率更高
//			transData.put(Dict.PPSETTLEMENTDATE, MiscUtil.getSettlementDate(merchantSett.getSettPeriod(), order.getClearDate()));
//			transData.put(Dict.PPBRIEFCODE, Constants.BRIEFCODE_MERCHANT_FEE_RTN);
//			
//			merchantTransManager.insertData(transData);
		}
	}
