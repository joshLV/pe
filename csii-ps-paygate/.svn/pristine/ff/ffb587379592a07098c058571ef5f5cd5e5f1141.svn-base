package com.csii.alipay.zhilian.trans.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alipay.api.AlipayApiException;
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
import com.csii.pp.util.AliPayUtil;
import com.csii.pp.util.MiscUtil;
/**
 * 
/***
 * 支付宝直连异步通知验签类
 * @author Bin
 *@create time 时间
 */
public class PreAlipayVerifyAction extends AbstractAction {
	
	private OrderManager orderManager;


	@Override
	public void execute(Context ctx) throws PeException {
		
		Map alipayMap = (Map) ctx.getData("aliNotifyMap");
		
		String outTradeNo = (String) alipayMap.get("out_trade_no");
		
		
		//获得订单
		Order order = orderManager.getOrder(outTradeNo);
		if (order == null) {
			log.info("支付宝支付通知 - 商户订单号[{}]未找到"+ outTradeNo);
			ctx.setData("Content", "FAIL");
			ctx.setData("ReturnCode", "FAIL");
			ctx.setData("ReturnMsg", "商户订单号未找到");
			throw new PeException("E00012", new Object[] { outTradeNo });
		}
		Map para = new HashMap();
		para.put("merId", order.getSubMerchantId());
		para.put("payType", order.getPayType());
		para.put("departmentId", order.getDepartmentId());
		para.put("bankId", order.getBankId());
		
		log.info("查询银行商户参数信息pp.core.queryMerBankParameter，入参"+order.getBankId());
		
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", para);

		
		//支付宝验签
		String sign = alipayMap.get("sign").toString();
		alipayMap.remove("sign");
		alipayMap.remove("sign_type");
		alipayMap.remove("trans_id");
		
		boolean signCheck = false;
	
		String publicKey = (String) MerBankParameterMap.get("alipayPubKey");
		try {
			signCheck = AliPayUtil.signCheck(alipayMap,sign,publicKey );
		} catch (AlipayApiException e) {
			log.error("验签过程异常，"+e);
		} catch (IOException e) {
			log.error(e);
		}
	
		if(!signCheck)
			throw new PeException("支付宝支付结果通知交易验签失败");
		
		ctx.setDataMap(alipayMap);
		
		ctx.setVariable(order);
		
	}


	public OrderManager getOrderManager() {
		return orderManager;
	}


	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
	
	


}
