package com.csii.alipay.zhilian.trans.action;

import java.util.Date;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
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
 * 支付宝申请退款接口action
 * */
public class APRFAction extends AbstractTwoPhaseAction {

	//支付宝通讯工具类
	private AlipayClientManager alipayClientManager;
	
	private OrderManager orderManager;
	//支付宝异步通知地址
	private String notifyUrl;

	
	public void execute(Context ctx) throws PeException {
		Order order = (Order) ctx.getVariable();
		

		log.info("请求支付宝退款接口开始，平台流水["+order.getTransSeqNo()+"]");
		
		
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
		
			
		AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();
		
		AlipayTradeRefundModel model=new AlipayTradeRefundModel();
		//平台流水号
	    model.setOutTradeNo(order.getOrgTransSeqNo());
	    
	    //退款单号
	    model.setOutRequestNo(order.getTransSeqNo());
	    
	    model.setRefundAmount(order.getAmount().toString()); //以元为单位
	    
	   
	    alipay_request.setBizModel(model);
	    
	    //请求支付宝端
	    AlipayTradeRefundResponse alipay_response =null;
		try {
			alipay_response = (AlipayTradeRefundResponse) alipayClient.execute(alipay_request);
		} catch (AlipayApiException e) {
			log.error("发送支付宝系统失败,错误信息为[{}]"+ e);
			throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR);
		}
		
	    
		log.info("alipay_response"+alipay_response.getBody());
	    //调用支付宝接口失败
	    if(alipay_response.getCode()==null ||!alipay_response.getCode().equals("10000")) {
	    	 log.info("请求支付宝退款接口发生异常，平台流水["+order.getTransSeqNo()+"]，支付宝返回码为:["+alipay_response.getCode()+"],信息描述为["+
	    			 alipay_response.getSubMsg()+"]");
	    	 
	    	order.setReturnCode(alipay_response.getCode());	    	 
	    	order.setReturnMsg(alipay_response.getSubMsg());
	    	order.setStatus(Constants.TRANS_STATUS_PROCESSING);
	    	ctx.setData("ReturnCode", alipay_response.getCode());
	 		ctx.setData("ReturnMsg", alipay_response.getSubMsg());
	    	throw new PeException(alipay_response.getCode());
	    	 
	    }else{
	    	//退款成功
	    	order.setStatus(Constants.TRANS_STATUS_OK);
	    	ctx.setData("trade_no",alipay_response.getTradeNo());
	    	order.setReturnCode(Constants.AAAAAAA);		
	    	order.setReturnMsg(alipay_response.getSubMsg());
	 		ctx.setData("ReturnCode", Constants.AAAAAAA);
	 		ctx.setData("ReturnMsg", alipay_response.getSubMsg());
	    }
	    //支付宝退款时间
		Date  hostDate =alipay_response.getGmtRefundPay();
		
		if(hostDate !=null) {
			order.setHostDate(MiscUtil.calStringToDate(MiscUtil.getDateFormat(MiscUtil.DEFAULT_DATE_PATTERN).format(hostDate))); //核心日期
			order.setHostDateTime(MiscUtil.calStringToTimestamp(MiscUtil.getDateFormat(MiscUtil.DEFAULT_TIME_PATTERN).format(hostDate)) );//核心时间
		}
		
		order.setHostSeqNo(alipay_response.getTradeNo()); //支付宝流水
		
		order.setOpenid(alipay_response.getBuyerUserId());
		
		ctx.setVariable(order);
		
	}


	public AlipayClientManager getAlipayClientManager() {
		return alipayClientManager;
	}


	public void setAlipayClientManager(AlipayClientManager alipayClientManager) {
		this.alipayClientManager = alipayClientManager;
	}


	public OrderManager getOrderManager() {
		return orderManager;
	}


	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}


	public String getNotifyUrl() {
		return notifyUrl;
	}


	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	
}
