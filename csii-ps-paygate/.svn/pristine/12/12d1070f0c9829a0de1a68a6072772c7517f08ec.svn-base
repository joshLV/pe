package com.csii.mybank.trans.action;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;

/**
 * 撤销订单接口action
 * */
public class ALCXAction extends AbstractTwoPhaseAction {

	private String url;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(Context ctx) throws PeException {
		Order order = (Order) ctx.getVariable();
		String status = order.getOrgOrder().getStatus();
		if (Constants.TRANS_STATUS_OK.equals(status)) {

		Map<String,Object> reqMap = new HashMap<String,Object>();
		reqMap.put("Version", "1.0.0"); // 版本号
		reqMap.put("Function", "ant.mybank.bkmerchanttrade.payCancel"); // 接口代码
		reqMap.put("ReqTime",  MiscUtil.timeToString(order.getTransDateTime())); // 报文发起时间
		reqMap.put("ReqTimeZone", "UTC+8"); // 报文发起时区
		reqMap.put("ReqMsgId", MiscUtil.timeToString(order.getTransDateTime())); // 请求报文ID
		reqMap.put("InputCharset", "UTF-8"); // 报文字符编码
		reqMap.put("Reserve", ""); // 保留字段
		reqMap.put("SignType", "RSA"); // 签名方式
		
		reqMap.put("OutTradeNo",  order.getTransSeqNo()); // 原支付交易外部交易号
		reqMap.put("MerchantId", "2088611330087634"); // 商户号。网商为商户分配的商户号，通过商户入驻结果查询接口获取。
		reqMap.put("IsvOrgId", "202210000000000001055"); // 合作方机构号（网商银行分配）
		
		
		
		reqMap.put("_TransName", "ALCX"); // 创建订单终端的IP
		reqMap.put("submitURL", url); 

		
		Map paraMap = new HashMap();
		paraMap.put("bankId", order.getBankId());
		paraMap.put("departmentId", order.getDepartmentId());
		Map<String,Object> MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", paraMap);
		String alipayBankPriKey = (String) MerBankParameterMap.get("alipayBankPriKey");
		String alipayPubKey = (String) MerBankParameterMap.get("alipayPubKey");
		String alipayAppId = (String) MerBankParameterMap.get("alipayAppId");
			
		reqMap.put("alipayBankPriKey", alipayBankPriKey);
		reqMap.put("alipayPubKey", alipayPubKey);
		reqMap.put("Appid",alipayAppId); // 应用ID
		Map resMap = (Map) this.issueAlipayCoreHostTrs(ctx, reqMap);
		resMap.remove("Signature");


		ctx.setDataMap(resMap);

		// 判断交易状态码
		if (Constants.QRCODE_FAIL.equals(resMap.get("return_code"))) {
			order.setStatus(Constants.TRANS_STATUS_ERROR);
			log.info("撤销订单交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理失败 - 错误码[" + resMap.get("return_code") + "]错误信息[" + resMap.get("return_msg") + "]");
			throw new PeException((String) resMap.get("return_code"));
		}

		String returnCode = (String) resMap.get("ResultCode");
		String returnMsg = (String) resMap.get("ResultMsg");

		ctx.setData("returnCode", returnCode);
		ctx.setData("returnMsg", returnMsg);

			if (Constants.WS_ALIPAY_SUCCESS.equals(returnCode)) {
				String resultCode = (String) resMap.get("ResultStatus");


				if ("S".equals(resultCode)) {
					order.setReturnCode(Constants.QRCODE_SUCCESS);

					log.info("撤销订单交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理成功.");
				} else {
					String errCode = (String) resMap.get("ResultCode");
					String errCodeDes = (String) resMap.get("ResultMsg");

					order.setReturnCode(errCode);
					order.setReturnMsg(errCodeDes);

					ctx.setData("errCode", errCode);
					ctx.setData("errCodeDes", errCodeDes);

					log.info("撤销订单交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理失败 - 错误码[" + errCode + "]错误信息[" + errCodeDes + "]");
				}

				ctx.setDataMap(resMap);

				ctx.setVariable(order);
			} else {
				log.info("撤销订单交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 微信受理失败 - 错误码[" + returnCode + "]错误信息[" + returnMsg + "]");
				throw new PeException("000001");
			}
		}else {
			log.error("订单状态不成功，不能撤销");
			ctx.setData("returnCode", "9999");
			ctx.setData("returnMsg", "订单状态不成功，不能撤销");
			throw new PeException("9999");

		}
		

	}

	

	public void setUrl(String url) {
		this.url = url;
	}

}
