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
//import com.csii.pp.clearing.fee.CancelFeeReceiver;
/**
 * 
 * 退货交易清分
 * 
 */
public class WithdrawClearinger extends AbstractClearinger {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csii.pp.clearing.Clearinger#clear(java.lang.Object)
	 */
	public void clear(Object data) throws PeException {
		
		
		Order order = (Order) data;
		Order orgOrder = orderManager.getOrder(order.getOrgTransSeqNo());
		order.setOrgOrder(orgOrder);
//		收取商户手续费
		feeReceiver.receive(order);

		
//		商户支付结算
		settlementBooker.book(order);

//		交易手续费分润
		profitAssigner.assign(order);
		
//		如果订单状态没有被设置为已结算,则置为清分成功
		if(!MiscUtil.trimAndEquals(Constants.TRANS_STEP_SETT_OK,order.getStep())){
			order.setStep(Constants.TRANS_STEP_CLEAR_OK);
		}

//		更新清算明细表的处理阶段
//		orderManager.updateDayClearOrder(order);
		orderManager.updateClearOrder(order);

//		更新历史交易明细表的处理阶段
		orderManager.updateHistoryOrder(order);
		
		
		
//		Order order = (Order) data;
//		Order orgOrder = orderManager.getHisOrgOrder(order.getOrgTransSeqNo());
//		order.setOrgOrder(orgOrder);
//		Map para = new HashMap();
//		para.put("merId", order.getSubMerchantId());
//		para.put("payType", order.getPayType());
//		para.put("departmentId", order.getDepartmentId());
//		MerchantSett merchantSett =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", para);
//		Merchant  merchant = (Merchant)this.sqlMap.queryForObject("pp.core.queryMerchant", order.getSubMerchantId());
////		BigDecimal feeAmount = feeCalcFactory.getFeeCalc(merchantSett).calculate(merchantSett, order.getAmount());
////		BigDecimal reservedAmt = orgOrder.getAmount2();//可退金额
//        //可退金额+此笔退款金额的手续费
////		BigDecimal reservedFeeAmt1 = (orgOrder.getAmount2().add(order.getAmount())).multiply(merchantSett.getFeeAmt()).setScale(2,BigDecimal.ROUND_HALF_UP);
//
//		//本次退货后需要保留的手续费金额 
////		BigDecimal reservedFeeAmt = reservedAmt.multiply(merchantSett.getFeeAmt()).setScale(2,BigDecimal.ROUND_HALF_UP);
////		
////		if (reservedFeeAmt.compareTo(reservedFeeAmt1) > 0) {
////			reservedFeeAmt = reservedFeeAmt1;
////		}
//
//		//本次退货需要返还的手续费金额
////		BigDecimal feeAmount = reservedFeeAmt1.subtract(reservedFeeAmt);
////		BigDecimal feeAmount = order.getAmount().multiply(merchantSett.getFeeAmt()).setScale(2,BigDecimal.ROUND_HALF_UP);
////		order.setFeeAmount(feeAmount.negate());
////		Map para1 = new HashMap();
////		para1.put("bankId", order.getBankId());
////		para1.put("payType", order.getPayType());
////		para1.put("departmentId", order.getDepartmentId());
////		Map passageway = (Map) this.sqlMap.queryForObject("pp.core.queryPassageWay",para1);
////		BigDecimal costFeeAmount = order.getAmount().multiply((BigDecimal) passageway.get("rate"));
////		order.setCostFeeAmount(costFeeAmount.negate());
////		if(!MiscUtil.isNullOrEmpty(order.getAgentid())){
////			Map para2 = new HashMap();
////			para2.put("merId", order.getAgentid());
////			para2.put("payType", order.getPayType());
////			para2.put("departmentId", order.getDepartmentId());
////			MerchantSett merchantSett1 =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", para2);
////			BigDecimal agentFeeAmount = order.getAmount().multiply(merchantSett.getFeeAmt().subtract(merchantSett1.getFeeAmt())).setScale(2,BigDecimal.ROUND_HALF_UP);
////			order.setAgentFeeAmount(agentFeeAmount.negate());
////		}
//		Date settDate = MiscUtil.getSettlementDate(merchantSett.getSettPeriod(), order.getClearDate());
//		Map map = new HashMap();
//		map.put("TransSeqNo", order.getTransSeqNo());
//		map.put("TransDate", order.getTransDate());
//		map.put("TransDateTime", order.getTransDateTime());
//		map.put("MerchantId", order.getMerchantId());
//		map.put("SubMerchantId", order.getSubMerchantId());
//		map.put("TransAmount", order.getAmount().negate());
//		map.put("FeeAmount", order.getFeeAmount());
//		map.put("TransType", order.getTransType());
//		map.put("ClearDate", order.getClearDate());
//		map.put("DepartmentId", order.getDepartmentId());
//		map.put("SettStatus", "0");
//		map.put("PayType", order.getPayType());
//		map.put("SettAcctNo", merchant.getMerSettAcctNo());
//		map.put("SettAcctBankNo", merchant.getMerSettAcctBankNo());
//		map.put("SettDate", MiscUtil.rolDate(order.getClearDate(), -1));
//		map.put("BankId", merchant.getMerBankId());
//		sqlMap.insert("pp.batch.insertBtMerTrans",map);
//
//		//如果订单状态没有被设置为已结算,则置为清分成功
//		if(!Constants.TRANS_STEP_SETT_OK.equals(order.getStep())){
//			order.setStep(Constants.TRANS_STEP_CLEAR_OK);
//		}
//		
//
//		//更新清算明细表的处理阶段
//		orderManager.updateClearOrder(order);
//
//		//更新历史交易明细表的处理阶段
//		orderManager.updateHistoryOrder(order);
	}
	
}
