package com.csii.pp.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;

import com.csii.pp.model.WxAccessTokenResult;
import com.csii.pp.model.WxErrorInfoResult;
import com.csii.pp.model.WxUserInfoResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WxUserInfoUtil {
	public static String authorizeUrl(String callbackUrl, String appId) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=").append(appId).append("&redirect_uri=").append(URLEncoder.encode(callbackUrl, "UTF-8")).append("&response_type=code")
				.append("&scope=snsapi_userinfo").append("&state=STATE").append("#wechat_redirect");

		return sb.toString();
	}

	/**
	 * 获取access_token
	 * 
	 * @param code
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static WxAccessTokenResult accessToken(String code, String appId, String appSecret) throws ClientProtocolException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").disableHtmlEscaping().create();
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.weixin.qq.com/sns/oauth2/access_token?appid=").append(appId).append("&secret=").append(appSecret).append("&code=").append(code).append("&grant_type=authorization_code");
		String result = WxHttpClientUtil.toString(WxHttpClientUtil.doHttpsGet(sb.toString(), ""));
		WxAccessTokenResult accessTokenResult = gson.fromJson(result, WxAccessTokenResult.class);
		return accessTokenResult;
	}

	/**
	 * refreshToken 重新获取 accessToken
	 * 
	 * @param refreshToken
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static WxAccessTokenResult refreshToken(String refreshToken, String appId) throws ClientProtocolException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").disableHtmlEscaping().create();
		WxAccessTokenResult accessTokenResult = null;
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=").append(appId).append("&refresh_token=").append(refreshToken).append("&grant_type=refresh_token");
		String result = WxHttpClientUtil.toString(WxHttpClientUtil.doHttpsGet(sb.toString(), ""));
		if (result != null) {
			accessTokenResult = gson.fromJson(result, WxAccessTokenResult.class);
		}
		return accessTokenResult;
	}

	/**
	 * 获取用户信息 需要snstype 为 userinfo 非 base
	 * 
	 * @param accessToken
	 * @param openId
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static WxUserInfoResult userInfo(String accessToken, String openId) throws ClientProtocolException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").disableHtmlEscaping().create();
		WxUserInfoResult userInfoResult = null;
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.weixin.qq.com/sns/userinfo?access_token=").append(accessToken).append("&openid=").append(openId).append("&lang=zh_CN");
		String result = WxHttpClientUtil.toString(WxHttpClientUtil.doHttpsGet(sb.toString(), ""));
		if (result != null) {
			userInfoResult = gson.fromJson(result, WxUserInfoResult.class);
		}
		return userInfoResult;
	}

	/**
	 * 判断accesstoken 是否有效
	 * 
	 * @param accessToken
	 * @param openId
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static boolean authAccessToken(String accessToken, String openId) throws ClientProtocolException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").disableHtmlEscaping().create();
		WxErrorInfoResult errorInfoResult = null;
		boolean isValid = false;
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.weixin.qq.com/sns/auth?access_token=").append(accessToken).append("&openid=").append(openId);
		String result = WxHttpClientUtil.toString(WxHttpClientUtil.doHttpsGet(sb.toString(), ""));
		if (result != null) {
			errorInfoResult = gson.fromJson(result, WxErrorInfoResult.class);
			if (0 == errorInfoResult.getErrcode()) {
				isValid = true;
			}
		}
		return isValid;
	}
}
