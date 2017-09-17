package com.csii.mybank.trans.action;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;

/**
 * 支付宝申请退款接口action
 * */
public class ALTKAction extends AbstractTwoPhaseAction {

	private static Logger logger = LoggerFactory.getLogger(ALTKAction.class);
	
	private String url;

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Context ctx) throws PeException {
		Order order = (Order) ctx.getVariable();
		Map para = new HashMap();
		para.put("departmentId", order.getDepartmentId());
		para.put("bankId", order.getBankId());
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", para);
		Map<String,Object> reqMap = new HashMap<String,Object> ();

		reqMap.put("Version", "1.0.0"); // 版本号
		reqMap.put("Appid",order.getAppid()); 
		reqMap.put("Function", "ant.mybank.bkmerchanttrade.refund"); // 接口代码
		reqMap.put("ReqTime",  MiscUtil.timeToString(order.getTransDateTime())); // 报文发起时间
		reqMap.put("ReqTimeZone", "UTC+8"); // 报文发起时区
		reqMap.put("ReqMsgId", MiscUtil.timeToString(order.getTransDateTime())); // 请求报文ID
		reqMap.put("InputCharset", "UTF-8"); // 报文字符编码
		reqMap.put("Reserve", ""); // 保留字段
		reqMap.put("SignType", "RSA"); // 签名方式
		
		reqMap.put("OutTradeNo", order.getOrgTransSeqNo()); // 原支付交易外部交易号
		reqMap.put("MerchantId", order.getMerWxId()); // 商户号。网商为商户分配的商户号，通过商户入驻结果查询接口获取。
		reqMap.put("IsvOrgId", MerBankParameterMap.get("mybankIsvOrgId")); // 合作方机构号（网商银行分配）
		reqMap.put("OutRefundNo", order.getTransSeqNo()); // 退款外部交易号。由合作方生成，同笔退款交易，交易状态未明需要重试时，使用同一个交易号。
		reqMap.put("RefundAmount", order.getAmount().multiply(new BigDecimal(100)).setScale(0)); // 退款金额。币种同原交易
		reqMap.put("RefundReason", "退款"); // 退款原因。支付宝交易须填写。
//		reqMap.put("OperatorId", ""); // 操作员ID
//		reqMap.put("DeviceId", "123qwe"); // 终端设备号(门店号或收银设备ID)。
		reqMap.put("DeviceCreateIp", ctx.getData("TerIp")); // 创建订单终端的IP
		
		
		reqMap.put("_TransName", "ALTK"); // 创建订单终端的IP
		reqMap.put("submitURL", url); 
		reqMap.put("mybankJfPriKey", MerBankParameterMap.get("mybankJfPriKey"));
		reqMap.put("mybankPubKey", MerBankParameterMap.get("mybankPubKey"));
		
		Map resMap = (Map) this.issueAlipayCoreHostTrs(ctx, reqMap);
		resMap.remove("Signature");
		
			String returnCode = (String) resMap.get("ResultCode");
			String returnMsg = (String) resMap.get("ResultMsg");

			
				ctx.setData("refundId", (String) resMap.get("RefundOrderNo"));
				String ResultStatus = (String) resMap.get("ResultStatus");
				
				if ("S".equals(ResultStatus)) {
					//成功状态，后服务中也是按处理中记录
					order.setReturnCode(Constants.QRCODE_SUCCESS);
					log.info("申请退款交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 网商-支付宝受理成功.");
				}else if("U".equals(ResultStatus)) {
					order.setReturnCode(Constants.QRCODE_SUCCESS);
					//成功状态，后服务中也是按处理中记录
					log.info("申请退款交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 网商-支付宝受理结果未知.");
				} else {
					String errCode = (String) resMap.get("ResultCode");
					String errCodeDes = (String) resMap.get("ResultMsg");

					log.info("申请退款交易 - 支付平台流水号[" + order.getTransSeqNo() + "] - 网商-支付宝受理失败 - 错误码[" + errCode + "]错误信息[" + errCodeDes + "]");
					throw new PeException((String) resMap.get("ResultCode"));
				}

				ctx.setDataMap(resMap);
				ctx.setVariable(order);
			
		
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
