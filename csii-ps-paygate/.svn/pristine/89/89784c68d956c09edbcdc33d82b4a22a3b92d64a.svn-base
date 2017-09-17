package com.csii.weixin.common.action;

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
import com.csii.pp.util.MiscUtil;

public class PrepareTenpayOrderAction extends AbstractAction {

	private IdFactory paymentSeqNoGenerator;

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Order order = new Order();
		String transSeqNo = paymentSeqNoGenerator.generate().toString();
		order.setTransSeqNo(transSeqNo);
		ctx.setData("out_trade_no", transSeqNo);
		// 支付平台交易日期
		Date transDate = new Date(ctx.getTimestamp().getTime());
		order.setTransDate(transDate);
		// 支付平台交易时间
		Timestamp transDateTime = new Timestamp(ctx.getTimestamp().getTime());
		order.setTransDateTime(transDateTime);
		order.setTransId(MiscUtil.toStringAndTrim(ctx.getTransactionId()));
		order.setSubMerchantId(MiscUtil.toStringAndTrim(ctx.getString("sub_mch_id")));
		String amountStr = MiscUtil.DivideHundred(ctx.getData("total_fee"));
		BigDecimal amount = new BigDecimal(amountStr).setScale(2, BigDecimal.ROUND_HALF_UP);
		// 交易金额-前端传送
		order.setAmount(amount);
		// 币种
		order.setCurrency(Constants.Default_Currency);
		// 已退金额
		order.setAmount1(Constants.ZERO);
		// 未退货的金额
		order.setAmount2(amount);
		// 预留金额1
		order.setAmount3(amount);
		// 预留金额2
		order.setAmount4(Constants.ZERO);
		// 手续费金额
//		order.setFeeAmount(Constants.ZERO);
		// mchid
		Merchant merInfo = (Merchant) this.getSqlMap().queryForObject("pp.core.qryParentMerId", ctx.getDataMap());
		if(MiscUtil.isNullOrEmpty(merInfo)){
			throw new PeException("E00003");
		}
		String parentMerId = merInfo.getMerParentId();
		if(Constants.CFG_MERID.equals(merInfo.getAgentId())){
			order.setMerMemId(ctx.getString("MemId"));
		}
		order.setMerchantId(parentMerId);
		order.setDepartmentId("weixin");
		Map param = new HashMap();
		param.put("merId", order.getSubMerchantId());
		param.put("departmentId", order.getDepartmentId());
		String deptmerid = (String) this.getSqlMap().queryForObject("pp.core.queryDepartMerId", param);
		order.setMerWxId(deptmerid);
		order.setBankId(merInfo.getMerBankId());
		
		Map paraMap = new HashMap();
		paraMap.put("bankId", order.getBankId());
		paraMap.put("departmentId", order.getDepartmentId());
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", paraMap);
		ctx.setData("paysignkey", MerBankParameterMap.get("paysignkey"));
		ctx.setData("MerBankParameterMap", MerBankParameterMap);
		order.setAgentid(merInfo.getAgentId());
		order.setPayType(MiscUtil.toStringAndTrim(ctx.getString("trade_type")));
		if ("MPCP".equals(order.getTransId())) {
			order.setPayType("MICROPAY");
		}
		order.setTransType("00");
		order.setStep("0");
		order.setProcStatus("0");
		order.setRemark1(MiscUtil.toStringAndTrim(ctx.getString("remark1")));
		order.setRemark2(MiscUtil.toStringAndTrim(ctx.getString("remark2")));
		order.setAppid(MiscUtil.toStringAndTrim(MerBankParameterMap.get("appId")));
		order.setOpenid(MiscUtil.toStringAndTrim(ctx.getString("openid")));
		
		DepartmentEntity departmentEntity = (DepartmentEntity) getSqlMap().queryForObject(Constants.QUERY_DEPARTMENT_ENTITY_SQL, "weixin");
		order.setClearDate(departmentEntity.getDepartmentDate());
		order.setMerchant(merInfo);
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
		
		Map para = new HashMap();
		para.put("merId", order.getSubMerchantId());
		para.put("payType", order.getPayType());
		para.put("departmentId", order.getDepartmentId());
		MerchantSett merchantSett =(MerchantSett) this.getSqlMap().queryForObject("pp.core.queryMerchantSett", para);
		if(merchantSett == null){
			log.error("商户支付类型["+order.getPayType()+"]未开通，无法交易");
			throw new PeException("E00034");
		}
		BigDecimal feeAmount = order.getAmount().multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
		order.setFeeAmount(feeAmount);
		Map para1 = new HashMap();
		para1.put("bankId", order.getBankId());
		para1.put("payType", order.getPayType());
		para1.put("departmentId", order.getDepartmentId());
		Map passageway = (Map) this.getSqlMap().queryForObject("pp.core.queryPassageWay",para1);
		BigDecimal costFeeAmount = order.getAmount().multiply(new BigDecimal(MiscUtil.toStringAndTrim(passageway.get("rate"))));
		order.setCostFeeAmount(costFeeAmount);
		if(!MiscUtil.isNullOrEmpty(order.getAgentid())){
			Map para2 = new HashMap();
			para2.put("merId", order.getAgentid());
			para2.put("payType", order.getPayType());
			para2.put("departmentId", order.getDepartmentId());
			MerchantSett merchantSett1 =(MerchantSett) this.getSqlMap().queryForObject("pp.core.queryMerchantSett", para2);
			BigDecimal agentFeeAmount = order.getAmount().multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt().subtract(merchantSett1.getFeeAmt())))).setScale(2,BigDecimal.ROUND_HALF_UP);
			order.setAgentFeeAmount(agentFeeAmount);
		}
		ctx.setVariable(order);

	}

	public void setPaymentSeqNoGenerator(IdFactory paymentSeqNoGenerator) {
		this.paymentSeqNoGenerator = paymentSeqNoGenerator;
	}

}
