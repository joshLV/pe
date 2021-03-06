package com.csii.alipay.zhilian.trans.action;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.SubMerchant;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.csii.mybank.trans.action.AbstractTwoPhaseAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.core.AlipayClientManager;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.ErrorConstants;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;

/**
 * 支付宝被扫交易
 * @author Administrator
 *
 */

public class APMPAction extends AbstractTwoPhaseAction {
	
	
	//支付宝通讯工具类
	private AlipayClientManager alipayClientManager;
	
	private OrderManager orderManager;
	//支付宝异步通知地址
	private String notifyUrl;
	
	
	
	@Override
	public void execute(Context ctx) throws PeException {
		
		Order order = (Order) ctx.getVariable();
		
	    log.info("请求支付宝刷卡支付接口开始，平台流水["+order.getTransSeqNo()+"]");
		
		Merchant merchant = order.getMerchant();
	
		
		String alipayAppId = order.getAppid();
		
		if(MiscUtil.isNullOrEmpty(alipayAppId)) {
			log.error("appId未查到，暂时无法进行交易！");
			throw new PeException(ErrorConstants.APPID_NOT_EXIST);
		}
		
		AlipayClient alipayClient = alipayClientManager.getAlipayClient(alipayAppId);
		
		if(MiscUtil.isNullOrEmpty(alipayAppId)) {
			log.error("appId[{}]未查到对应的通讯工具类，暂时无法进行交易！"+alipayAppId);
			throw new PeException(ErrorConstants.E10002);
		}
		
			
		AlipayTradePayRequest alipay_request = new AlipayTradePayRequest();
		
		AlipayTradePayModel model = new AlipayTradePayModel();
		//平台流水号
	    model.setOutTradeNo(order.getTransSeqNo());
	    
		model.setScene("bar_code"); //條形碼
		
		model.setAuthCode(ctx.getString("auth_code"));
	    
	    //子商户号
	    SubMerchant subMerchant = new SubMerchant();
	    //第三方商户号
	    subMerchant.setMerchantId(order.getMerWxId());
	   
	    model.setSubMerchant(subMerchant);
	      
	    model.setTotalAmount(order.getAmount().toString()); //以元为单位
	    
	    //店铺号
	    model.setStoreId(order.getSubMerchantId());
	    
	    //商品名称
	    model.setSubject(MiscUtil.isNullOrEmpty(order.getRemark1())?order.getMerchant().getMerShortName():order.getRemark1());
	    
	    alipay_request.setNotifyUrl(notifyUrl);
	   
	    alipay_request.setBizModel(model);
	    
	    //请求支付宝端
	    AlipayTradePayResponse  alipay_response = null;
	    
	    try {
			alipay_response = (AlipayTradePayResponse) alipayClient.execute(alipay_request);
		} catch (AlipayApiException e) {
			log.error("发送支付宝系统失败,错误信息为[{}]"+ e);
			
			//发起一次查询交易
			queryOrder(ctx, alipayClient, order);
			
		}
		
	    
	    log.info("支付宝返回数据"+alipay_response.getBody());
	    //调用支付宝接口失败
	    if(alipay_response.getCode()==null ||!alipay_response.getCode().equals("10000")) {
	    	 log.info("请求支付宝二维码生成接口发生异常，平台流水["+order.getTransSeqNo()+"]，支付宝返回码为:["+alipay_response.getCode()+"],信息描述为["+
	    			 alipay_response.getSubMsg()+"]");
	    	order.setReturnCode(alipay_response.getCode());	    	 
		    order.setReturnMsg(alipay_response.getSubMsg());
		    throw new PeException(alipay_response.getCode());
	    }
		
		
	    order.setHostSeqNo(alipay_response.getTradeNo());

	    ctx.setData("transaction_id", alipay_response.getTradeNo());	//支付宝流水号
		
		ctx.setData("openid", alipay_response.getBuyerLogonId());		//买家账号
		
		ctx.setData("ReturnCode", Constants.AAAAAAA);
		
		ctx.setVariable(order);
		
	}

	
	private void queryOrder(Context ctx,AlipayClient alipayClient,Order order) throws PeException {
		

		AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
		
		AlipayTradeQueryModel model=new AlipayTradeQueryModel();
		//平台流水号
	    model.setOutTradeNo(MiscUtil.toStringAndTrim(order.getTransSeqNo()));
	    
	    alipay_request.setBizModel(model);
	    
	    AlipayTradeQueryResponse alipay_response =null;
		try {
			alipay_response = (AlipayTradeQueryResponse) alipayClient.execute(alipay_request);
		} catch (AlipayApiException e) {
			log.error("请求支付宝查询接口异常,[{}]"+e);
			throw new PeException("999999"); //系统异常
		}

		log.info("支付宝交易,订单["+order.getTransSeqNo()+"]，处理成功，返回结果码为["+alipay_response.getTradeStatus()+"],结果信息为:["+ alipay_response.getMsg()+"]");
	    
		if(alipay_response.getCode().equals("10000") && alipay_response.getTradeStatus().equals("TRADE_SUCCESS")) {
	    	
			ctx.setData("transaction_id", alipay_response.getTradeNo());	//支付宝流水号
			ctx.setData("openid", alipay_response.getBuyerLogonId());		//买家账号
			ctx.setData("time_end",MiscUtil.getDateFormat(MiscUtil.DEFAULT_TIME_PATTERN).format(alipay_response.getSendPayDate()));//格式化日期

	    }else {
	    	throw new PeException("SYSTEMERROR");
	    }
		
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}


	public AlipayClientManager getAlipayClientManager() {
		return alipayClientManager;
	}


	public void setAlipayClientManager(AlipayClientManager alipayClientManager) {
		this.alipayClientManager = alipayClientManager;
	}



	public String getNotifyUrl() {
		return notifyUrl;
	}


	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}


	public OrderManager getOrderManager() {
		return orderManager;
	}



}
