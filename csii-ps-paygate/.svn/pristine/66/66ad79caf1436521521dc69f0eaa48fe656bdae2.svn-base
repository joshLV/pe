package com.csii.alipay.zhilian.trans.action;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
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
 * 支付宝交易查询接口action
 * */
public class APQOAction extends AbstractTwoPhaseAction {

	//支付宝通讯工具类
	private AlipayClientManager alipayClientManager;
	
	private OrderManager orderManager;
	//支付宝异步通知地址
	private String notifyUrl;

	
	public void execute(Context ctx) throws PeException {
		
		Order order = (Order) ctx.getVariable();
		

		String status = order.getStatus();
		
		if (Constants.TRANS_STATUS_PAYING.equals(status) || Constants.TRANS_STATUS_UNPAY.equals(status)) {
			
			ctx.setData("QryWxTransStatus", true);

			log.info("请求支付宝查询接口开始，平台流水["+order.getTransSeqNo()+"]");
			
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
			
				
			AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
			
			AlipayTradeQueryModel model=new AlipayTradeQueryModel();
			//平台流水号
			
		    model.setOutTradeNo(order.getTransSeqNo());
		   
		    alipay_request.setBizModel(model);
		    
		    //请求支付宝端
		    AlipayTradeQueryResponse alipay_response =null;
			try {
				alipay_response = (AlipayTradeQueryResponse) alipayClient.execute(alipay_request);
			} catch (AlipayApiException e) {
				log.error("发送支付宝系统失败,错误信息为[{}]"+ e);
				throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR);
			}
		
		    //调用支付宝接口失败
		    if(alipay_response.getCode()==null ||!alipay_response.getCode().equals("10000")) {
		    	 log.info("请求支付宝查询接口发生异常，平台流水["+order.getTransSeqNo()+"]，支付宝返回码为:["+alipay_response.getCode()+"],信息描述为["+
		    			 alipay_response.getSubMsg()+"]");
		    	
		    	 order.setReturnCode(alipay_response.getSubCode());	    	 
			   
		    	order.setReturnMsg(alipay_response.getSubMsg());
			
				ctx.setData("ReturnCode", alipay_response.getSubCode());
	
		    }else if("10000".equals(alipay_response.getCode())){
		    	if("TRADE_SUCCESS".equals(alipay_response.getTradeStatus())){
		    		
		    		order.setStatus(Constants.TRANS_STATUS_OK);
					
					order.setReturnCode(Constants.AAAAAAA);
					
					order.setReturnMsg("交易成功");
					
					order.setHostSeqNo(alipay_response.getTradeNo());//支付宝流水号
					
					order.setOpenid(alipay_response.getBuyerLogonId());		//买家账号
					
					order.setHostDate(MiscUtil.calStringToDate((MiscUtil.getDateFormat(MiscUtil.DEFAULT_DATE_PATTERN).format(alipay_response.getSendPayDate())))); //核心日期
					
					order.setHostDateTime(MiscUtil.calStringToTimestamp(MiscUtil.getDateFormat(MiscUtil.DEFAULT_TIME_PATTERN).format(alipay_response.getSendPayDate())));
	
			    	ctx.setData("result_code", Constants.TRANS_STATUS_OK);
					
			    	ctx.setData("ReturnCode", Constants.AAAAAAA);
			    	
		    	}else if("WAIT_BUYER_PAY".equals(alipay_response.getTradeStatus())){
		    		
		    		order.setStatus(Constants.TRANS_STATUS_UNPAY);
			    	
		    		ctx.setData("result_code", Constants.TRANS_STATUS_UNPAY);
					
		    		ctx.setData("ReturnCode", Constants.AAAAAAA);
		    	}
		    	
		    }
		    	ctx.setVariable(order);
		}else {
			ctx.setData("QryWxTransStatus", false);
			log.info("请求支付宝查询接口开始，平台流水["+order.getTransSeqNo()+"],状态明确，不需查询后台交易");
		}
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
