package com.csii.alipay.zhilian.trans.action;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.id.IdFactory;
import com.csii.pp.dict.Constants;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.merchant.MerchantSett;
import com.csii.pp.model.User;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;

/**
 * 准备申请退款订单
 */
public class PrepareRefundOfZLOrderAction extends AbstractAction {

	private OrderManager orderManager;

	private IdFactory refundOrderSeqNoGenerator;

	public void execute(Context ctx) throws PeException {
		String outRefundNo = MiscUtil.toStringAndTrim(ctx.getData("out_refund_no"));

		Order order = null;

		if (MiscUtil.isNullOrEmpty(outRefundNo)) {
			order = new Order();
			// 流水号
			String transSeqNo = refundOrderSeqNoGenerator.generate().toString();
			order.setTransSeqNo(transSeqNo);
			// 支付平台交易日期
			Date transDate = new Date(ctx.getTimestamp().getTime());
			order.setTransDate(transDate);
			// 支付平台交易时间
			Timestamp transDateTime = new Timestamp(ctx.getTimestamp().getTime());
			order.setTransDateTime(transDateTime);
			order.setTransId(MiscUtil.toStringAndTrim(ctx.getTransactionId()));
			order.setAppid(MiscUtil.toStringAndTrim(ctx.getString("appid")));
			String subMchId = MiscUtil.toStringAndTrim(ctx.getString("sub_mch_id"));
			if (MiscUtil.isNullOrEmpty(subMchId)) {
				log.error(new StringBuffer("支付平台流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("子商户ID未上送").toString());
				throw new PeException("E00002");
			}
			order.setSubMerchantId(subMchId);
			
			order.setOrgTransSeqNo(MiscUtil.toStringAndTrim(ctx.getData("out_trade_no")));

			String ramountStr =MiscUtil.DivideHundred(ctx.getData("refund_fee"));
			BigDecimal ramount =  new BigDecimal(ramountStr).setScale(2, BigDecimal.ROUND_HALF_UP);
			//币种
			order.setCurrency(Constants.Default_Currency);
			// 退款金额
			order.setAmount(ramount);
			// 预留金额1
			order.setAmount1(Constants.ZERO);
			// 预留金额2
			order.setAmount2(Constants.ZERO);
			// 预留金额3
			order.setAmount3(ramount);
			// 预留金额4
			order.setAmount4(Constants.ZERO);
			// 手续费金额
			order.setStep("0");
			order.setProcStatus("0");
			order.setTransType("01");//退款
			order.setRemark1(MiscUtil.toStringAndTrim(ctx.getString("remark1")));
			order.setRemark2(MiscUtil.toStringAndTrim(ctx.getString("remark2")));
		} else {
			order = orderManager.getOrder(outRefundNo);
			if(order == null){
				throw new PeException("E00012", new Object[] { outRefundNo });
			}
		}

		order.setDepartmentId(Constants.DEPARTMENTID_ALIPAY);
		//mchid
		Merchant merInfo = (Merchant)this.getSqlMap().queryForObject("pp.core.qryParentMerId", ctx.getDataMap());
		if (merInfo == null) {
			log.error(new StringBuffer("支付平台流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("商户信息不存在").toString());
			throw new PeException("E00003");
		}
		String parentMerId = merInfo.getMerParentId();
		order.setMerchantId(parentMerId);
		order.setMerchant(merInfo);

		DepartmentEntity departmentEntity = (DepartmentEntity) getSqlMap().queryForObject(Constants.QUERY_DEPARTMENT_ENTITY_SQL, Constants.DEPARTMENTID_ALIPAY);
		order.setClearDate(departmentEntity.getDepartmentDate());

		order.setCashierId(MiscUtil.toStringAndTrim(ctx.getData("cashierId")));
		if (!MiscUtil.isNullOrEmpty(order.getCashierId())) {

			Map data = new HashMap();
			data.put("userId", order.getCashierId());
			User user = (User) getSqlMap().queryForObject("pp.core.qryUserInfo", data);

			if (user == null) {
				log.error("收银员不存在，无法交易");
				throw new PeException("E00018");
			}
			order.setCashierName(MiscUtil.toStringAndTrim(user.getUserName()));
			if("1".equals(user.getUserStatus())){
				throw new PeException("E00021");
			}
		}

		// 获取原支付订单
		Order orgOrder = orderManager.getOriginalOrder(order);
		if (orgOrder == null) {
			log.info(new StringBuffer("网关流水[").append(order.getTransSeqNo()).append("]").append("商户号[").append(order.getMerchantId()).append("]").append("******原支付订单信息不存在******").toString());
			throw new PeException("E00010");
		}
		order.setOrgOrder(orgOrder);
		order.setOrgTransDate(orgOrder.getTransDate());
		order.setOrgTransDateTime(orgOrder.getTransDateTime());
		order.setOrgHostSeqNo(orgOrder.getHostSeqNo());
		order.setOrgHostDate(orgOrder.getHostDate());
		order.setAppid(orgOrder.getAppid());
		order.setBankId(orgOrder.getBankId());
		order.setBankType(orgOrder.getBankType());
		order.setPayType(orgOrder.getPayType());
		order.setOpenid(orgOrder.getOpenid());
		order.setAgentid(orgOrder.getAgentid());
		order.setAmount4(orgOrder.getAmount3());
		order.setMerWxId(orgOrder.getMerWxId());
		Map para = new HashMap();
		para.put("merId", order.getSubMerchantId());
		para.put("payType", order.getPayType());
		para.put("departmentId", order.getDepartmentId());
		MerchantSett merchantSett =(MerchantSett) this.getSqlMap().queryForObject("pp.core.queryMerchantSett", para);
		BigDecimal reservedAmt = orgOrder.getAmount2();//可退金额
        //可退金额+此笔退款金额的商户手续费
		BigDecimal reservedFeeAmt1 = reservedAmt.multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
		//本次退货后需要保留的商户手续费手续费金额 
		BigDecimal reservedFeeAmt = (orgOrder.getAmount2().subtract(order.getAmount())).multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
		
		if (reservedFeeAmt.compareTo(reservedFeeAmt1) > 0) {
			reservedFeeAmt = reservedFeeAmt1;
		}
		//本次退货需要返还的手续费金额
		BigDecimal feeAmount = reservedFeeAmt1.subtract(reservedFeeAmt);
		order.setFeeAmount(feeAmount.negate());//商户手续费
		
		Map para1 = new HashMap();
		para1.put("bankId", order.getBankId());
		para1.put("payType", order.getPayType());
		para1.put("departmentId", order.getDepartmentId());
		Map passageway = (Map) this.getSqlMap().queryForObject("pp.core.queryPassageWay",para1);
		//可退金额+此笔退款金额的商户手续费
		BigDecimal reservedCostFeeAmt1 = reservedAmt.multiply(new BigDecimal(MiscUtil.toStringAndTrim(passageway.get("rate")))).setScale(2,BigDecimal.ROUND_HALF_UP);
				//本次退货后需要保留的商户手续费手续费金额 
		BigDecimal reservedCostFeeAmt = (orgOrder.getAmount2().subtract(order.getAmount())).multiply(new BigDecimal(MiscUtil.toStringAndTrim(passageway.get("rate")))).setScale(2,BigDecimal.ROUND_HALF_UP);
				
		if (reservedCostFeeAmt.compareTo(reservedCostFeeAmt1) > 0) {
			reservedCostFeeAmt = reservedCostFeeAmt1;
		}
				//本次退货需要返还的手续费金额
		BigDecimal costFeeAmount = reservedCostFeeAmt1.subtract(reservedCostFeeAmt);
		order.setCostFeeAmount(costFeeAmount.negate());
		if(!MiscUtil.isNullOrEmpty(order.getAgentid())){
			Map para2 = new HashMap();
			para2.put("merId", order.getAgentid());
			para2.put("payType", order.getPayType());
			para2.put("departmentId", order.getDepartmentId());
			MerchantSett merchantSett1 =(MerchantSett) this.getSqlMap().queryForObject("pp.core.queryMerchantSett", para2);
			//可退金额+此笔退款金额的商户手续费
			BigDecimal reservedAgentFeeAmt1 = reservedAmt.multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt().subtract(merchantSett1.getFeeAmt())))).setScale(2,BigDecimal.ROUND_HALF_UP);
					//本次退货后需要保留的商户手续费手续费金额 
			BigDecimal reservedAgentFeeAmt = (orgOrder.getAmount2().subtract(order.getAmount())).multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt().subtract(merchantSett1.getFeeAmt())))).setScale(2,BigDecimal.ROUND_HALF_UP);
					
			if (reservedAgentFeeAmt.compareTo(reservedAgentFeeAmt1) > 0) {
				reservedAgentFeeAmt = reservedAgentFeeAmt1;
			}
			BigDecimal agentFeeAmount = reservedAgentFeeAmt1.subtract(reservedAgentFeeAmt);
			order.setAgentFeeAmount(agentFeeAmount.negate());
		}
		
		ctx.setVariable(order);
	}

	public IdFactory getRefundOrderSeqNoGenerator() {
		return refundOrderSeqNoGenerator;
	}

	public void setRefundOrderSeqNoGenerator(IdFactory refundOrderSeqNoGenerator) {
		this.refundOrderSeqNoGenerator = refundOrderSeqNoGenerator;
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
	
	
}
