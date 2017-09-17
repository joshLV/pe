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
/**
 * 
 * @author DJG
 * 支付宝订单准备类
 *
 */
public class PrepareMybankOrderAction extends AbstractAction {

	private IdFactory paymentSeqNoGenerator;

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		//获取平台流水
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
		// 交易码 
		order.setTransId(MiscUtil.toStringAndTrim(ctx.getTransactionId()));
		// 交易方商户号
		order.setSubMerchantId(MiscUtil.toStringAndTrim(ctx.getString("sub_mch_id")));
		//交易金额
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
		// 查询数据库商户信息mchid
		log.info("执行pp.core.qryParentMerId进行商户查询入参为"+order.getSubMerchantId());
		Merchant merInfo = (Merchant) this.getSqlMap().queryForObject("pp.core.qryParentMerId", ctx.getDataMap());
		if(MiscUtil.isNullOrEmpty(merInfo)){
			log.info("查询商户信息失败");
			throw new PeException("E00003");
		}
		String parentMerId = merInfo.getMerParentId();
		if(Constants.CFG_MERID.equals(merInfo.getAgentId())){
			order.setMerMemId(ctx.getString("MemId"));
		}
		order.setMerchantId(parentMerId);

		order.setDepartmentId("mybank");
		//设置机构id
		order.setBankId(merInfo.getMerBankId());
		
		//设置代理商
		order.setAgentid(merInfo.getAgentId());
		//设置支付类型
		order.setPayType(MiscUtil.toStringAndTrim(ctx.getString("trade_type")));
		if ("ALBS".equals(order.getTransId())) {
			order.setPayType("MICROPAY");//刷卡
		}else if("ALZS".equals(order.getTransId())){
			order.setPayType("JSAPI");//门店二维码H5支付
		}
		//设置交易类型
		order.setTransType("00");
		//设置清结算步骤
		order.setStep("0");
		//设置清算对账状态
		order.setProcStatus("0");
		//备注1
		order.setRemark1(MiscUtil.toStringAndTrim(ctx.getString("remark1")));
		//备注2
		order.setRemark2(MiscUtil.toStringAndTrim(ctx.getString("remark2")));
		
		//
		order.setOpenid(MiscUtil.toStringAndTrim(ctx.getString("openid")));
		
		log.info("查询机构信息 pp.core.queryDepartmentEntity 入参为"+order.getDepartmentId());
		DepartmentEntity departmentEntity = (DepartmentEntity) getSqlMap().queryForObject(Constants.QUERY_DEPARTMENT_ENTITY_SQL, order.getDepartmentId());
		//系统清算日期
		order.setClearDate(departmentEntity.getDepartmentDate());
		//商户信息
		order.setMerchant(merInfo);
		//收银员
		order.setCashierId(MiscUtil.toStringAndTrim(ctx.getData("cashierId")));
		if (!MiscUtil.isNullOrEmpty(order.getCashierId())) {

			Map data = new HashMap();
			data.put("userId", order.getCashierId());
			log.info("查询收银员信息pp.core.qryUserInfo ，入参为"+data);
			User user = (User) getSqlMap().queryForObject("pp.core.qryUserInfo", data);

			if (user == null) {
				log.error("收银员不存在，无法交易");
				throw new PeException("E00018");
			}
			order.setCashierName(MiscUtil.toStringAndTrim(user.getUserName()));
			if("1".equals(user.getUserStatus())){
				log.info("收银员状态不正确");
				throw new PeException("E00021");
			}
		}
		
		Map para = new HashMap();
		para.put("merId", order.getSubMerchantId());
		para.put("payType", order.getPayType());
		para.put("departmentId", order.getDepartmentId());
		para.put("bankId", order.getBankId());
		//查询商户权限
		log.info("执行商户费率查询pp.core.queryMerchantSett，入参为"+para);
		MerchantSett merchantSett =(MerchantSett) this.getSqlMap().queryForObject("pp.core.queryMerchantSett", para);
		if(merchantSett == null){
			log.error("商户支付类型["+order.getPayType()+"]未开通，无法交易");
			throw new PeException("E00034");
		}
		String deptmerid = (String) this.getSqlMap().queryForObject("pp.core.queryDepartMerId", para);
		order.setMerWxId(deptmerid);
		log.info("查询银行商户参数信息pp.core.queryMerBankParameter，入参"+order.getBankId());
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", para);
		//APPID
		order.setAppid(MiscUtil.toStringAndTrim(MerBankParameterMap.get("mybankAppId")));
		//获取软证书
		ctx.setData("MerBankParameterMap", MerBankParameterMap);
		//根据商户设置手续费规则计算手续费
		BigDecimal feeAmount = order.getAmount().multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt()))).setScale(2,BigDecimal.ROUND_HALF_UP);
		//设置手续费金额
		order.setFeeAmount(feeAmount);
		Map para1 = new HashMap();
		para1.put("bankId", order.getBankId());
		para1.put("payType", order.getPayType());
		para1.put("departmentId", order.getDepartmentId());
		log.info("查询通道信息pp.core.queryPassageWay，入参为"+para1);
		Map passageway = (Map) this.getSqlMap().queryForObject("pp.core.queryPassageWay",para1);
		//计算通道手续费
		BigDecimal costFeeAmount = order.getAmount().multiply(new BigDecimal(MiscUtil.toStringAndTrim(passageway.get("rate"))));
		order.setCostFeeAmount(costFeeAmount);
		//判断是否有代理商
		if(!MiscUtil.isNullOrEmpty(order.getAgentid())){
			Map para2 = new HashMap();
			para2.put("merId", order.getAgentid());
			para2.put("payType", order.getPayType());
			para2.put("departmentId", order.getDepartmentId());
			//查询代理商手续费费率
			log.info("执行商户费率查询pp.core.queryMerchantSett，入参为"+para2);
			MerchantSett merchantSett1 =(MerchantSett) this.getSqlMap().queryForObject("pp.core.queryMerchantSett", para2);
			BigDecimal agentFeeAmount = order.getAmount().multiply(new BigDecimal(MiscUtil.toStringAndTrim(merchantSett.getFeeAmt().subtract(merchantSett1.getFeeAmt())))).setScale(2,BigDecimal.ROUND_HALF_UP);
			//代理商手续费
			order.setAgentFeeAmount(agentFeeAmount);
		}
		ctx.setVariable(order);

	}

	public void setPaymentSeqNoGenerator(IdFactory paymentSeqNoGenerator) {
		this.paymentSeqNoGenerator = paymentSeqNoGenerator;
	}

}
