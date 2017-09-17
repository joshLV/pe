package com.csii.mybank.trans.action;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;

public class ALZSAction extends AbstractTwoPhaseAction{

	private String url;
	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Order order = (Order) ctx.getVariable();
		Map MerBankParameterMap = (Map) ctx.getData("MerBankParameterMap");
		Merchant merchant = order.getMerchant();

		Map sendMap = new HashMap();
		sendMap.put("submitURL", url);
		sendMap.put("Appid", order.getAppid());
		sendMap.put("Function", "ant.mybank.bkmerchanttrade.prePay");
		sendMap.put("ReqTime", MiscUtil.timeToString(order.getTransDateTime()));
		sendMap.put("ReqMsgId", MiscUtil.timeToString(order.getTransDateTime()));
		sendMap.put("InputCharset", "UTF-8");
		sendMap.put("Reserve", ""); // 保留字段
		sendMap.put("SignType", "RSA"); // 签名方式
		sendMap.put("ReqTimeZone", "UTC+8"); // 报文发起时区
		sendMap.put("OutTradeNo", order.getTransSeqNo());
		sendMap.put("Body", merchant.getMerShortName() + "-" + merchant.getManageDesc());
		sendMap.put("TotalAmount", ctx.getData("total_fee"));
		sendMap.put("Currency", order.getCurrency());
		sendMap.put("MerchantId", order.getMerWxId());
		sendMap.put("IsvOrgId", MerBankParameterMap.get("mybankIsvOrgId"));
		sendMap.put("ChannelType", ctx.getData("ChannelType"));
		sendMap.put("OpenId", order.getOpenid());
		sendMap.put("DeviceCreateIp", ctx.getData("spbill_create_ip"));
		sendMap.put("SettleType", "T1");
		sendMap.put("mybankJfPriKey", MerBankParameterMap.get("mybankJfPriKey"));
		sendMap.put("mybankPubKey", MerBankParameterMap.get("mybankPubKey"));
		sendMap.put("_TransName", "ALZS");
		Map result = (Map) this.issueAlipayCoreHostTrs(ctx, sendMap);
		
		log.info("支付平台流水号[" + order.getTransSeqNo() + "]网商银行返回信息如下:" + result);
		ctx.setDataMap(result);
		if("S".equals(result.get("ResultStatus"))){
			log.info("网商银行H5支付 - 支付平台流水号[" + order.getTransSeqNo() + "]交易成功");
		}else{
			log.info("网商银行H5支付 - 支付平台流水号[" + order.getTransSeqNo() + "],响应码[" + result.get("ResultCode") + "],响应信息[" + result.get("ResultMsg")+"]");
			throw new PeException((String) result.get("ResultCode"));
		}
		
		
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
