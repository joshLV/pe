package com.csii.alipay.zhilian.trans.action;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
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
public class APRFQueryAction extends AbstractTwoPhaseAction {

	//支付宝通讯工具类
	private AlipayClientManager alipayClientManager;
	
	//支付宝异步通知地址
	private String notifyUrl;

	
	public void execute(Context ctx) throws PeException {
		
		Order order = (Order) ctx.getVariable();
		

		String status = order.getStatus();
		
		if (MiscUtil.isNullOrEmpty(status) || Constants.TRANS_STATUS_PROCESSING.equals(status)) {
			
			ctx.setData("QryWxTransStatus", true);

			log.info("请求支付宝退款查询接口开始，平台流水["+order.getTransSeqNo()+"]");
			
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
			
				
			AlipayTradeFastpayRefundQueryRequest alipay_request = new AlipayTradeFastpayRefundQueryRequest();
			
			AlipayTradeFastpayRefundQueryModel model=new AlipayTradeFastpayRefundQueryModel();
			
			//平台 原支付流水号
		    model.setOutTradeNo(order.getOrgTransSeqNo());
		   
		    model.setOutRequestNo(order.getTransSeqNo());
		   
		    alipay_request.setBizModel(model);
		    
		    //请求支付宝端
		    AlipayTradeFastpayRefundQueryResponse alipay_response =null;
			try {
				alipay_response = (AlipayTradeFastpayRefundQueryResponse) alipayClient.execute(alipay_request);
			} catch (AlipayApiException e) {
				log.error("发送支付宝系统失败,错误信息为[{}]"+ e);
				throw new CommunicationException(ErrorConstants.PP_COMMUNICATION_TIMEOUT_ERROR);
			}
		
		    //调用支付宝接口失败
		    if(alipay_response.getCode()==null ||!alipay_response.getCode().equals("10000")) {
		    	 log.info("请求支付宝查询接口发生异常，平台流水["+order.getTransSeqNo()+"]，支付宝返回码为:["+alipay_response.getCode()+"],信息描述为["+
		    			 alipay_response.getSubMsg()+"]");
		    	 
		    	order.setStatus(Constants.TRANS_STATUS_ERROR);
		    	
				order.setReturnCode(alipay_response.getCode());
				
				order.setReturnMsg(alipay_response.getSubMsg());
		    	
		    	ctx.setData("result_code", Constants.TRANS_STATUS_ERROR);

				ctx.setData("ReturnCode", Constants.AAAAAAA);
			
				ctx.setData("ReturnMsg", "交易成功");
	
		    }else if("10000".equals(alipay_response.getCode()) && !MiscUtil.isNullOrEmpty(alipay_response.getTradeNo())){
		    		
		    		order.setStatus(Constants.TRANS_STATUS_OK);
					
					order.setReturnCode(Constants.AAAAAAA);
					
					order.setReturnMsg("交易成功");
					
					order.setHostSeqNo(alipay_response.getTradeNo());//支付宝流水号
					
			    	ctx.setData("result_code", Constants.TRANS_STATUS_OK);
					
			    	ctx.setData("ReturnCode", Constants.AAAAAAA);
			    	
			    	ctx.setData("ReturnMsg", alipay_response.getMsg());
		    	}else{
		    		
		    		order.setStatus(Constants.TRANS_STATUS_ERROR);
		    		
		    		ctx.setData("result_code", Constants.TRANS_STATUS_ERROR);
					
		    		ctx.setData("ReturnCode", Constants.AAAAAAA);

		    		ctx.setData("ReturnMsg", "交易成功");
		    	}
		    	
		    }else {
				ctx.setData("QryWxTransStatus", false);
				log.info("请求支付宝退款查询接口开始，平台流水["+order.getTransSeqNo()+"],状态为["+order.getStatus()+"]明确，不需查询后台交易");
			}
		    ctx.setVariable(order);
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

	
}
