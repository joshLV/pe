package com.csii.weixin.trans.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.core.AlipayClientManager;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.model.WxAccessTokenResult;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUserInfoUtil;

/**
 * 商户基本信息查询及OPENID获取接口
 */
public class PTQMAction extends AbstractAction {

	private static Logger logger = LoggerFactory.getLogger(PTQMAction.class);
	private AlipayClientManager alipayClientManager;


	@Override
	public void execute(Context context) throws PeException {
		String mchToken = (String) context.getData("mch_token");
		String reqAppId = (String) context.getData("appid");
		String reqAlipayAppId = (String) context.getData("alipay_appid");
		String authChannel = (String) context.getData("auth_channel");// 用户支付渠道（微信、支付宝等）目前只支持微信
		String authCode = (String) context.getData("auth_code");
		String departmentId = (String) context.getData("departmentId");

		Map m = new HashMap();
		m.put("mchToken", mchToken);
		Merchant merchant = (Merchant) this.getSqlMap().queryForObject("pp.core.qryMerInfoByToken", m);
		if (merchant == null) {
			logger.error("根据商户[TOKEN:{}]无法查找到商户", mchToken);
			throw new PeException("E00003");
		}
		Map para = new HashMap();
		para.put("bankId", merchant.getMerBankId());
		if("tenpay".equals(authChannel)){
			para.put("departmentId", "weixin");
		}else if("alipay".equals(authChannel)){
			para.put("departmentId", departmentId);
		}
		
		String openId = "";
		String accessToken = "";
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", para);
        if("tenpay".equals(authChannel)){
        	
        	 String appId = (String) MerBankParameterMap.get("appId");
             String appSecret = (String) MerBankParameterMap.get("appSecret");
     		if (!appId.equals(reqAppId)) {
     			logger.error("上送的商户[appId:{}]与配置的[appId:{}]不匹配", reqAppId, appId);
     			throw new PeException("E00017");
     		}
        	openId = authTenpay(authCode, appId, appSecret);
        	
        }else if("alipay".equals(authChannel)){
        	
        	String alipayAppId = (String) MerBankParameterMap.get("alipayAppId");
        	if (!alipayAppId.equals(reqAlipayAppId)) {
     			logger.error("上送的商户[appId:{}]与配置的[appId:{}]不匹配", reqAlipayAppId, alipayAppId);
     			throw new PeException("E00017");
     		}
        	AlipayClient alipayClient = alipayClientManager.getAlipayClient(alipayAppId);
        	AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        	request.setCode(authCode);
        	request.setGrantType("authorization_code");
        	try {
        	    AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
        	    if(oauthTokenResponse.isSuccess()){
					log.info("支付宝授权接口调用成功");
					openId = oauthTokenResponse.getUserId();
					accessToken = oauthTokenResponse.getAccessToken();
				}else{
					log.info("支付宝授权接口"+ ",支付宝响应码[" + oauthTokenResponse.getSubCode() +  "],支付宝错误信息["
							+ oauthTokenResponse.getSubMsg() + "]");
					throw new PeException(oauthTokenResponse.getSubCode());
				}
        	    System.out.println(oauthTokenResponse.getAccessToken());
        	} catch (Exception e) {
        	    //处理异常
        	    log.error("获取支付宝用户信息异常", e);
        	    throw new PeException("E00031");
        	}
        }

		Map dataMap = new HashMap();
		dataMap.put("ReturnCode", "000000");
		dataMap.put("ReturnMsg", "交易成功");

		dataMap.put("openid", openId);
		dataMap.put("accessToken", accessToken);
		dataMap.put("mch_id", merchant.getId());
		dataMap.put("mch_name", merchant.getMerShortName());
		dataMap.put("mch_add", merchant.getMerAddr());
		dataMap.put("mch_phone", merchant.getMerPhoneNo());
		dataMap.put("nonce_str", MiscUtil.getRandom());
		dataMap.put("mch_agentId", merchant.getAgentId());

		context.setData("json", dataMap);
	}

	/*
	 * 微信授权
	 */
	private String authTenpay(String authCode, String authAppId, String authAppSecret) throws PeException {

		String openId = "";
		try {
			WxAccessTokenResult accessTokenResult = WxUserInfoUtil.accessToken(authCode, authAppId, authAppSecret);

			String errorCode = accessTokenResult.getErrcode();

			if ((errorCode != null) && !errorCode.equals("")) {
				logger.error("从微信获取用户的OpenId错误 - [ErrorCode:{} - ErrorMsg:{}]", errorCode, accessTokenResult.getErrmsg());
				throw new PeException("E00031");
			} else {
				openId = accessTokenResult.getOpenid();
			}

		} catch (Exception e) {
			logger.error("从微信获取用户的OpenId错误 - " + e.getMessage(), e);
			throw new PeException("E00031");
		}

		return openId;
	}

	public void setAlipayClientManager(AlipayClientManager alipayClientManager) {
		this.alipayClientManager = alipayClientManager;
	}
	
	

}
