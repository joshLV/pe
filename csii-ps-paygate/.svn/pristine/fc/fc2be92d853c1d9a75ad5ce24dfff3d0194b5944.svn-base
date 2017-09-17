package com.csii.alipay.zhilian.trans.action;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.SubMerchant;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.csii.mybank.trans.action.AbstractTwoPhaseAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.CommunicationException;
import com.csii.pp.core.AlipayClientManager;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.ErrorConstants;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;
/**
 * 
 * 
 * @author hbczyue
 * 支付宝刷卡支付 客户被扫
 */
public class APCPAction extends AbstractTwoPhaseAction {
	
	
	//支付宝通讯工具类
	private AlipayClientManager alipayClientManager;
	
	private OrderManager orderManager;
	//支付宝异步通知地址
	private String notifyUrl;
	
	
	
	@Override
	public void execute(Context ctx) throws PeException {
		
		
		Order order = (Order) ctx.getVariable();

		log.info("请求支付宝二维码生成接口开始，平台流水["+order.getTransSeqNo()+"]");
		
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
			
		AlipayTradePrecreateRequest alipay_request = new AlipayTradePrecreateRequest();
		
		AlipayTradePrecreateModel model=new AlipayTradePrecreateModel();
		//平台流水号
	    model.setOutTradeNo(order.getTransSeqNo());
	    
	    //子商户号
	    SubMerchant subMerchant = new SubMerchant();
	    //第三方
	    subMerchant.setMerchantId(order.getMerWxId());
	 
	    model.setSubMerchant(subMerchant);
	      
	    model.setTotalAmount(order.getAmount().toString()); //以元为单位
	    
	   //30分粥之内 ，不进行支付的话，自动关闭订单。
	    model.setTimeoutExpress("30m");
	    //店铺号
	    model.setStoreId(order.getSubMerchantId());
	    
	    //商品名称
	    model.setSubject(MiscUtil.isNullOrEmpty(order.getRemark1())?order.getMerchant().getMerShortName():order.getRemark1());
	    
	    alipay_request.setNotifyUrl(notifyUrl);
	   
	    alipay_request.setBizModel(model);
	    
	    //请求支付宝端
	    AlipayTradePrecreateResponse alipay_response =null;
		try {
			alipay_response = (AlipayTradePrecreateResponse) alipayClient.execute(alipay_request);
		} catch (AlipayApiException e) {
			log.error("发送支付宝系统失败,错误信息为[{}]"+ e);
			throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR);
		}
		
	    
	
	    //调用支付宝接口失败
	    if(alipay_response.getCode()==null ||!alipay_response.getCode().equals("10000")) {
	    	 log.info("请求支付宝二维码生成接口发生异常，平台流水["+order.getTransSeqNo()+"]，支付宝返回码为:["+alipay_response.getCode()+"],业务错误码为：["+alipay_response.getSubCode()+"]信息描述为["+
	    			 alipay_response.getSubMsg()+"]");
	    	 order.setReturnCode(alipay_response.getCode());	    	 
		    	order.setReturnMsg(alipay_response.getSubMsg());
		    	throw new PeException(alipay_response.getCode());
	    }
		
		
		ctx.setData("ReturnCode", Constants.AAAAAAA);
		
		ctx.setData("code_url", alipay_response.getQrCode());//支付宝条形码
		
		
		
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
