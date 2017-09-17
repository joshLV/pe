package com.csii.weixin.trans.action;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeCancelModel;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.csii.ibs.action.AbstractAction;
import com.csii.pe.common.util.CsiiUtils;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.CommunicationException;
import com.csii.pe.service.comm.Transport;
import com.csii.pp.core.AlipayClientManager;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;
import com.csii.pp.dict.ErrorConstants;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;
import com.csii.pp.util.XmlUtil;
import com.csii.weixin.notify.action.MerchantSendAction;

public class QueryPayOrderAction extends AbstractAction {

	private WxUtil wxUtil;

	private String url;

	private String refundurl;

	private String times;

	private OrderManager orderManager;
	
	private MerchantSendAction merchantSendAction;
	
	private Transport mybankTransport;
	
	private String mybankurl;
	
	
	int alipayQueryTime= 5;
	
	//支付宝通讯工具类
	private AlipayClientManager alipayClientManager;
	

	@Override
	public void execute(Context ctx) throws PeException {
		log.info("定时轮询 - 查询待支付订单 - 开始");
		List unpayList = getSqlMap().queryForList("pp.core.queryUnpayOrder");

		if (unpayList != null) {
			for (Iterator it = unpayList.iterator(); it.hasNext();) {
				Map para = (Map) it.next();
				
				String transSeqNo = (String) para.get("TransSeqNo");
				Order order = (Order) this.getSqlMap().queryForObject("pp.core.queryOtByTransSeqNo", para);
				if (order == null) {
					log.error(new StringBuffer("网关流水[").append(transSeqNo).append("]，查证处理结束，原交易已经不存在").toString());
					this.getSqlMap().delete("pp.core.deleteUnpayOrderByTransSeqNo", para);
					continue;
				}
				int sendTimes = (Integer) para.get(Dict.PPSENDTIMES) + 1;
				if (sendTimes >= Integer.parseInt(times)) {
					if("mybank".equals(order.getDepartmentId())){
						log.info(new StringBuffer("网关流水[").append(transSeqNo).append("]，查证处理结束，已查证").append(times).append("次").toString());
						try {
							CloseOrderForMybank(order);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							log.error("网商支付交易关闭失败：",e);
						}
						this.getSqlMap().delete("pp.core.deleteUnpayOrderByTransSeqNo", para);
					}
				}
				
				String status = order.getStatus();

				//支付宝直连关闭订单（订单查询超过5次就关闭订单）
				if (sendTimes >= alipayQueryTime) {
					if(Constants.DEPARTMENTID_ALIPAY.equals(order.getDepartmentId()) && order.getTransType().equals("00")){
						log.info(new StringBuffer("网关流水[").append(transSeqNo).append("]，查证处理结束，已查证").append(sendTimes).append("次").toString());
						try {
							//未支付的情况,关闭订单
							if(Constants.TRANS_STATUS_UNPAY.equals(status)) {
								CloseOrderForAlipay(ctx,order,para,sendTimes);
							}else{
								CancelOrderForAlipay(ctx,order,para,sendTimes);
							}
						} catch (Exception e) {
							log.error("支付宝直连交易关闭失败：",e);
						}
						
					}
					this.getSqlMap().delete("pp.core.deleteUnpayOrderByTransSeqNo", para);
				}
				
				if(Constants.DEPARTMENTID_ALIPAY.equals(order.getDepartmentId()) && order.getTransType().equals("00") && sendTimes>=3){
					log.info(new StringBuffer("网关流水[").append(transSeqNo).append("]，发起支付宝撤销交易").append(sendTimes).append("次").toString());
					//状态未名的话，发起撤销交易 	
					if(!Constants.TRANS_STATUS_UNPAY.equals(status)) {
						try{
							CancelOrderForAlipay(ctx,order,para,sendTimes);
						}catch (Exception e) {
							log.error("支付宝直连交易关闭失败：",e);
						}
					}
					para.put(Dict.PPSENDTIMES,sendTimes );
					this.getSqlMap().update("pp.core.updateQueryTimes", para);
				}
				
				
				log.debug("********第" + sendTimes + "次处理未知查询交易********");
				Map paraMap = new HashMap();
				paraMap.put("bankId", order.getBankId());
				paraMap.put("departmentId", order.getDepartmentId());
				Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", paraMap);
				String paysignkey = (String) MerBankParameterMap.get("paysignkey");
				if (Constants.TRANS_STATUS_PAYING.equals(status) || Constants.TRANS_STATUS_UNPAY.equals(status)||Constants.TRANS_STATUS_PROCESSING.equals(status)||Constants.TRANS_STATUS_UNKNOW.equals(status)) {
					if ("00".equals(order.getTransType())) {
						if("weixin".equals(order.getDepartmentId())){
							Map sendMap = new HashMap();
							sendMap.put("appid", order.getAppid());
							sendMap.put("mch_id", MerBankParameterMap.get("merId"));
							sendMap.put("sub_mch_id", order.getMerWxId());
							sendMap.put("out_trade_no", order.getTransSeqNo());// 商户订单号
							sendMap.put("nonce_str", MiscUtil.getRandom());
							String str1 = MiscUtil.sign(sendMap,paysignkey);
							sendMap.put("sign", str1);
							String xmlData = wxUtil.parseXML(sendMap);
							byte[] bytes = wxUtil.httpSend(xmlData.getBytes(), url);
							String str = MiscUtil.getStrMsg(bytes, "UTF-8");
							Map wxResMap = XmlUtil.toMap(str, "xml");
//							ctx.setDataMap(wxResMap);
							log.info("定时轮询支付订单 - 支付平台流水号[" + order.getTransSeqNo() + "],微信返回信息如下:" + wxResMap);
							String returnCode = (String) wxResMap.get("return_code");
							String returnMsg = (String) wxResMap.get("return_msg");
							if (Constants.QRCODE_SUCCESS.equals(returnCode)) {
								String sign = (String) wxResMap.get("sign");
								String resultCode = (String) wxResMap.get("result_code");
								String errorCode = (String) wxResMap.get("err_code");
								String errorMsg = (String) wxResMap.get("err_code_des");
								wxResMap.remove("sign");
								String tSign = MiscUtil.sign(wxResMap,paysignkey);
								boolean isnotify = false;
								if (sign.equals(tSign)) {
									if (Constants.QRCODE_SUCCESS.equals(resultCode)) {
										order.setStatus(wxUtil.getTransStatus(MiscUtil.toStringAndTrim(wxResMap.get("trade_state"))));
										if (Constants.QRCODE_SUCCESS.equals(wxResMap.get("trade_state"))) {
											order.setReturnCode(Constants.AAAAAAA);
											order.setReturnMsg("交易成功");
											String amountStr = MiscUtil.DivideHundred(ctx.getData("cash_fee"));
											BigDecimal amount = new BigDecimal(amountStr).setScale(2, BigDecimal.ROUND_HALF_UP);
											order.setAmount(amount);
											order.setOpenid(MiscUtil.toStringAndTrim(wxResMap.get("openid")));
											order.setBankType(MiscUtil.toStringAndTrim(wxResMap.get("bank_type")));
											order.setHostSeqNo(MiscUtil.toStringAndTrim(wxResMap.get("transaction_id")));
											String hostDateTime = MiscUtil.toStringAndTrim(wxResMap.get("time_end"));
											order.setHostDateTime(new Timestamp(CsiiUtils.bocmDatetimeToCal(hostDateTime).getTimeInMillis()));

											String hostDate = MiscUtil.toStringAndTrim(wxResMap.get("time_end")).substring(0, 8);
											order.setHostDate(MiscUtil.calStringToDate(hostDate));
											this.getSqlMap().delete("pp.core.deleteUnpayOrderByTransSeqNo", para);
											
											//trade_state为success交易成功，扣减限额
											Map para1 = new HashMap();
											para1.put("MerchantId", order.getSubMerchantId());
											para1.put("Status", "0");
											//商户交易限额控制
											Map ctrl = (Map) this.getSqlMap().queryForObject("pp.core.queryMerchantTransCtrlDynamic",para1);
											log.info("order======"+order.getAmount3());
											if (ctrl != null) {
												log.debug("aaaaa"+order.getAmount3());
												log.info("kkkkk"+order.getAmount3());
												log.debug("bbbbb"+ctrl.get("PerDayAmt"));
												log.debug("ccccc"+((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
												ctrl.put("PerDayAmt",((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
												this.getSqlMap().update("pp.core.updateMerLimitCtrl",ctrl);
											}
											isnotify = true;
										} else {
											para.put(Dict.PPSENDTIMES, sendTimes);
											this.getSqlMap().update("pp.core.updateQueryTimes", para);
										}

										orderManager.updateOrder(order);
										if(isnotify){
											//发送商户通知
											ctx.setVariable(order);
											merchantSendAction.execute(ctx);
										}
									} else {
										para.put(Dict.PPSENDTIMES, sendTimes);
										this.getSqlMap().update("pp.core.updateQueryTimes", para);
										log.error("定时轮询支付订单 - 支付平台流水号[" + order.getTransSeqNo() + "]交易失败，错误码[" + errorCode + "]，错误信息[" + errorMsg + "]");
									}
								} else {
									log.error("定时轮询支付订单 - 支付平台流水号[" + order.getTransSeqNo() + "]验签失败.");
								}
							}
						}else if("mybank".equals(order.getDepartmentId())){
							Map sendMap = new HashMap();
							sendMap.put("submitURL", mybankurl);
							sendMap.put("Appid", order.getAppid());
							sendMap.put("Function", "ant.mybank.bkmerchanttrade.payQuery");
							sendMap.put("ReqTime", MiscUtil.getCurrentTimeString());
							sendMap.put("ReqMsgId", MiscUtil.getCurrentTimeString());
							sendMap.put("InputCharset", "UTF-8");
							sendMap.put("Reserve", ""); // 保留字段
							sendMap.put("SignType", "RSA"); // 签名方式
							sendMap.put("ReqTimeZone", "UTC+8"); // 报文发起时区
							sendMap.put("IsvOrgId", MerBankParameterMap.get("mybankIsvOrgId"));
							sendMap.put("MerchantId", order.getMerWxId());
							sendMap.put("OutTradeNo", order.getTransSeqNo());
							sendMap.put("_TransName", "APCX");
							sendMap.put("mybankJfPriKey", MerBankParameterMap.get("mybankJfPriKey"));
							sendMap.put("mybankPubKey", MerBankParameterMap.get("mybankPubKey"));
							Map result =(Map) this.mybankTransport.submit(sendMap);
							
							String returnCode = (String) result.get("ResultCode");

							String ResultStatus = (String) result.get("ResultStatus");
								
									if ("S".equals(ResultStatus)) {
										String TradeStatus  = (String) result.get("TradeStatus");
										order.setStatus(WxUtil.getTransStatus(TradeStatus));
										if ("succ".equals(TradeStatus)) {
											order.setReturnCode(Constants.AAAAAAA);
											order.setReturnMsg("交易成功");
											order.setHostSeqNo(MiscUtil.toStringAndTrim(ctx.getData("OrderNo")));
											order.setRemark1(MiscUtil.toStringAndTrim(ctx.getData("PayChannelOrderNo")));
											order.setOpenid(MiscUtil.toStringAndTrim(ctx.getData("OpenId")));
											order.setBankType(MiscUtil.toStringAndTrim(ctx.getData("BankType")));
											String hostDateTime = MiscUtil.toStringAndTrim(ctx.getData("GmtPayment"));
											order.setHostDateTime(Timestamp.valueOf(hostDateTime));
				                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
											order.setHostDate(MiscUtil.calStringToDate(hostDateTime.substring(0,10)));
											
                                             this.getSqlMap().delete("pp.core.deleteUnpayOrderByTransSeqNo", para);
											
											//交易成功，扣减限额
											Map para1 = new HashMap();
											para1.put("MerchantId", order.getSubMerchantId());
											para1.put("Status", "0");
											//商户交易限额控制
											Map ctrl = (Map) this.getSqlMap().queryForObject("pp.core.queryMerchantTransCtrlDynamic",para1);
											log.info("order======"+order.getAmount3());
											if (ctrl != null) {
												log.debug("aaaaa"+order.getAmount3());
												log.info("kkkkk"+order.getAmount3());
												log.debug("bbbbb"+ctrl.get("PerDayAmt"));
												log.debug("ccccc"+((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
												ctrl.put("PerDayAmt",((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
												this.getSqlMap().update("pp.core.updateMerLimitCtrl",ctrl);
											}
										}else{
											para.put(Dict.PPSENDTIMES, sendTimes);
											this.getSqlMap().update("pp.core.updateQueryTimes", para);
										}
										orderManager.updateOrder(order);

									} else {
										para.put(Dict.PPSENDTIMES, sendTimes);
										this.getSqlMap().update("pp.core.updateQueryTimes", para);
										String errorCode = (String) result.get("ResultCode");
										String errorMsg = (String) result.get("ResultMsg");
										log.error("定时轮询支付订单网商-支付宝 - 支付平台流水号[" + order.getTransSeqNo() + "]交易失败，错误码[" + errorCode + "]，错误信息[" + errorMsg + "]");
									}
							
							//支付宝直连交易查询开始	wzj	
							}else if(Constants.DEPARTMENTID_ALIPAY.equals(order.getDepartmentId())) {
							
								try {
										queryAlipayOrder(ctx,order,para,sendTimes);
									
								}catch (Exception e) {
										log.error("定时轮询支付订单-支付宝 - 支付平台流水号[" + order.getTransSeqNo() + "]交易失败，错误信息[" + e + "]");
										para.put(Dict.PPSENDTIMES, sendTimes);
										this.getSqlMap().update("pp.core.updateQueryTimes", para);
										continue;
									}
								
							}
						
					} else {
						if("weixin".equals(order.getDepartmentId())) {
						
							Map sendMap = new HashMap();
							sendMap.put("appid", order.getAppid()); // 公众帐号ID
							sendMap.put("mch_id", MerBankParameterMap.get("merId")); // 商户号
							sendMap.put("sub_mch_id", order.getMerWxId()); // 子商户号
							sendMap.put("nonce_str", MiscUtil.getRandom()); // 随机字符串
							sendMap.put("out_refund_no", order.getTransSeqNo()); // 商户退款单号
							// 获取签名字符串
							String signStr = MiscUtil.sign(sendMap,paysignkey);
							log.info("微信支付定时查询退款接口支付平台流水号[" + order.getTransSeqNo() + "]签名数据为：" + signStr);
							sendMap.put("sign", signStr);
							String xmlData = wxUtil.parseXML(sendMap);
							byte[] bytea = wxUtil.httpSend(xmlData.getBytes(), refundurl);
							String str = MiscUtil.getStrMsg(bytea, "UTF-8");
							Map wxResMap = XmlUtil.toMap(str, "xml");
	//						ctx.setDataMap(wxResMap);
							log.info("微信支付定时查询退款接口支付平台流水号[" + order.getTransSeqNo() + "]微信返回信息如下:" + wxResMap);
							String returnCode = (String) wxResMap.get("return_code");
							String returnMsg = (String) wxResMap.get("return_msg");
	
							if (Constants.QRCODE_SUCCESS.equals(returnCode)) {
								String sign = (String) wxResMap.get("sign");
								String resultCode = (String) wxResMap.get("result_code");
								String errorCode = (String) wxResMap.get("err_code");
								String errorMsg = (String) wxResMap.get("err_code_des");
								wxResMap.remove("sign");
	
								String tSign = MiscUtil.sign(wxResMap,paysignkey);
	
								if (sign.equals(tSign)) {
									if (Constants.QRCODE_SUCCESS.equals(resultCode)) {
	
										String refund_count = (String) wxResMap.get("refund_count");
										if (!MiscUtil.isNullOrEmpty(refund_count)) {
											for (int i = 0; i <= Integer.valueOf(refund_count)-1; i++) {
												if (order.getTransSeqNo().equals(wxResMap.get("out_refund_no_" + i))) {
													ctx.setData("refund_id", wxResMap.get("refund_id" + i));
													ctx.setData("refund_status", wxResMap.get("refund_status_" + i));
												}
											}
										}
	
										if (Constants.QRCODE_SUCCESS.equals(wxResMap.get("refund_status"))) {
											order.setReturnCode(Constants.AAAAAAA);
											order.setReturnMsg("交易成功");
											order.setHostSeqNo(MiscUtil.toStringAndTrim(wxResMap.get("refund_id")));
											this.getSqlMap().delete("pp.core.deleteUnpayOrderByTransSeqNo", para);
										} else {
											para.put(Dict.PPSENDTIMES, sendTimes);
											this.getSqlMap().update("pp.core.updateQueryTimes", para);
										}
	
										order.setStatus(wxUtil.getTransStatus(MiscUtil.toStringAndTrim(wxResMap.get("refund_status"))));
										orderManager.updateOrder(order);
									} else {
										log.error("微信订单查询 - 支付平台流水号[" + order.getTransSeqNo() + "]交易失败，错误码[" + errorCode + "]，错误信息[" + errorMsg + "]");
										para.put(Dict.PPSENDTIMES, sendTimes);
										this.getSqlMap().update("pp.core.updateQueryTimes", para);
									}
								} else {
									log.error("微信订单查询 - 支付平台流水号[" + order.getTransSeqNo() + "]验签失败.");
								}
							 }
							//支付宝退款交易查询
							}else if(Constants.DEPARTMENTID_ALIPAY.equals(order.getDepartmentId())) {
								
								try {
									queryAlipayRFOrder(ctx,order,para,sendTimes);
								
							}catch (Exception e) {
									log.error("定时轮询支付订单-支付宝 - 支付平台流水号[" + order.getTransSeqNo() + "]交易失败，错误信息[" + e + "]");
									para.put(Dict.PPSENDTIMES, sendTimes);
									this.getSqlMap().update("pp.core.updateQueryTimes", para);
									continue;
								}
							
						}
								
					
					}
				}else{
					log.error(new StringBuffer("网关流水[").append(transSeqNo).append("]，查证处理结束，原交易状态明确，无须查证").toString());
					this.getSqlMap().delete("pp.core.deleteUnpayOrderByTransSeqNo", para);
					continue;
				}
				
			}
		}
		log.info("定时轮询 - 查询待支付订单 - 结束");
	}
	
	
	private void CloseOrderForAlipay(Context ctx, Order order, Map para, int sendTimes) throws PeException {

		
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
		

		AlipayTradeCloseRequest alipay_request = new AlipayTradeCloseRequest();
		
		AlipayTradeCloseModel model=new AlipayTradeCloseModel();
		//平台流水号
	    model.setOutTradeNo(MiscUtil.toStringAndTrim(order.getTransSeqNo()));
	    
	    alipay_request.setBizModel(model);
	    
	    AlipayTradeCloseResponse alipay_response =null;
		try {
			alipay_response = (AlipayTradeCloseResponse) alipayClient.execute(alipay_request);
		} catch (AlipayApiException e) {
			log.error("请求支付宝关闭接口异常,[{}]"+e);
			throw new PeException("999999"); //系统异常
		}

		log.info("支付宝交易,订单["+order.getTransSeqNo()+"]，处理成功，返回结果码为["+alipay_response.getCode()+"],结果信息为:["+ alipay_response.getMsg()+"]");
	    
		
		if(alipay_response.getSubCode().equals("ACQ.TRADE_HAS_SUCCESS") && 
				(!order.getStatus().equals(Constants.TRANS_STATUS_OK  ) && !order.getStatus().equals(Constants.TRANS_STATUS_SUB_WITHDRAW  ) 
						&& !order.getStatus().equals(Constants.TRANS_STATUS_ALL_WITHDRAW  ) )) {
	    	
			order.setStatus(Constants.TRANS_STATUS_OK);
			
			order.setReturnCode(Constants.AAAAAAA);
			
			order.setReturnMsg("交易成功");
			
			order.setHostSeqNo(alipay_response.getTradeNo());//支付宝流水号
			
			
			//更新订单状态
			orderManager.updateOrder(order);
			
			//交易成功，扣减限额
			Map para1 = new HashMap();
			para1.put("MerchantId", order.getSubMerchantId());
			para1.put("Status", "0");
			//商户交易限额控制
			Map ctrl = (Map) this.getSqlMap().queryForObject("pp.core.queryMerchantTransCtrlDynamic",para1);
			log.info("order======"+order.getAmount3());
			if (ctrl != null) {
				log.debug("日限额更新为[{}]"+((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
				ctrl.put("PerDayAmt",((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
				this.getSqlMap().update("pp.core.updateMerLimitCtrl",ctrl);
			}
			
		//订单关闭的话，按照交易失败处理
	    }else if("10000".equals(alipay_response.getCode()) || "ACQ.TRADE_STATUS_ERROR".equals(alipay_response.getSubCode())
	    		||"ACQ.TRADE_HAS_CLOSE".equals(alipay_response.getSubCode()) ||"ACQ.TRADE_HAS_CLOSE".equals(alipay_response.getSubCode())
	    		){
	    	order.setStatus(Constants.TRANS_STATUS_ERROR);
	    	order.setReturnCode("SYSTEMERROR");
	    	order.setReturnMsg("交易已关闭");
			//删除待查询记录
			//更新订单状态
			orderManager.updateOrder(order);
	    	
	    }else {
	    	
	    	order.setStatus(Constants.TRANS_STATUS_ERROR);
	    	order.setReturnCode("SYSTEMERROR");
	    	order.setReturnMsg("交易已关闭");
			//删除待查询记录
			//更新订单状态
			orderManager.updateOrder(order);
	    	
	    }

		
	}
	private void CancelOrderForAlipay(Context ctx, Order order, Map para, int sendTimes) throws PeException {
		
		
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
		
		AlipayTradeCancelRequest alipay_request = new AlipayTradeCancelRequest();
		
		AlipayTradeCancelModel model=new AlipayTradeCancelModel();
		//平台流水号
		model.setOutTradeNo(MiscUtil.toStringAndTrim(order.getTransSeqNo()));
		
		alipay_request.setBizModel(model);
		
		AlipayTradeCancelResponse alipay_response =null;
		try {
			alipay_response = (AlipayTradeCancelResponse) alipayClient.execute(alipay_request);
		} catch (AlipayApiException e) {
			log.error("请求支付宝关闭接口异常,[{}]"+e);
			throw new PeException("999999"); //系统异常
		}
		
		log.info("支付宝交易,订单["+order.getTransSeqNo()+"]，处理成功，返回结果码为["+alipay_response.getCode()+"],结果信息为:["+ alipay_response.getMsg()+"]");
		
		
		if(alipay_response.getSubCode().equals("ACQ.TRADE_HAS_SUCCESS") && 
				(!order.getStatus().equals(Constants.TRANS_STATUS_OK  ) && !order.getStatus().equals(Constants.TRANS_STATUS_SUB_WITHDRAW  ) 
						&& !order.getStatus().equals(Constants.TRANS_STATUS_ALL_WITHDRAW  ) )) {
			
			order.setStatus(Constants.TRANS_STATUS_OK);
			
			order.setReturnCode(Constants.AAAAAAA);
			
			order.setReturnMsg("交易成功");
			
			order.setHostSeqNo(alipay_response.getTradeNo());//支付宝流水号
			
			
			//更新订单状态
			orderManager.updateOrder(order);
			
			//交易成功，扣减限额
			Map para1 = new HashMap();
			para1.put("MerchantId", order.getSubMerchantId());
			para1.put("Status", "0");
			//商户交易限额控制
			Map ctrl = (Map) this.getSqlMap().queryForObject("pp.core.queryMerchantTransCtrlDynamic",para1);
			log.info("order======"+order.getAmount3());
			if (ctrl != null) {
				log.debug("日限额更新为[{}]"+((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
				ctrl.put("PerDayAmt",((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
				this.getSqlMap().update("pp.core.updateMerLimitCtrl",ctrl);
			}
			
			//订单关闭的话，按照交易失败处理
		}else if("10000".equals(alipay_response.getCode()) || "ACQ.TRADE_STATUS_ERROR".equals(alipay_response.getSubCode())
				||"ACQ.TRADE_HAS_CLOSE".equals(alipay_response.getSubCode()) ||"ACQ.TRADE_HAS_CLOSE".equals(alipay_response.getSubCode())
				){
			order.setStatus(Constants.TRANS_STATUS_ERROR);
			order.setReturnCode("SYSTEMERROR");
			order.setReturnMsg("交易已关闭");
			//删除待查询记录
			//更新订单状态
			orderManager.updateOrder(order);
			
		}else {
			
			order.setStatus(Constants.TRANS_STATUS_ERROR);
			order.setReturnCode("SYSTEMERROR");
			order.setReturnMsg("交易已关闭");
			//删除待查询记录
			//更新订单状态
			orderManager.updateOrder(order);
			
		}
		
		
	}


	/**
	 * 支付宝交易查询
	 * @param ctx
	 * @param alipayClient
	 * @param order
	 * @throws PeException
	 */
	
	
	private Order queryAlipayOrder(Context ctx,Order order ,Map para,int sendTimes) throws PeException {
		
		//是否异步通知
		boolean isnotify =false;
		
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
	    	
			order.setStatus(Constants.TRANS_STATUS_OK);
			
			order.setReturnCode(Constants.AAAAAAA);
			
			order.setReturnMsg("交易成功");
			
			order.setHostSeqNo(alipay_response.getTradeNo());//支付宝流水号
			
			order.setOpenid(alipay_response.getBuyerLogonId());		//买家账号
			
			order.setHostDate(MiscUtil.calStringToDate((MiscUtil.getDateFormat(MiscUtil.DEFAULT_DATE_PATTERN).format(alipay_response.getSendPayDate())))); //核心日期
			
			order.setHostDateTime(MiscUtil.calStringToTimestamp(MiscUtil.getDateFormat(MiscUtil.DEFAULT_TIME_PATTERN).format(alipay_response.getSendPayDate())));
			//删除待查询记录
			this.getSqlMap().delete("pp.core.deleteUnpayOrderByTransSeqNo", para);
			//更新订单状态
			orderManager.updateOrder(order);
			
			isnotify  = true;
			
			
			//交易成功，扣减限额
			Map para1 = new HashMap();
			para1.put("MerchantId", order.getSubMerchantId());
			para1.put("Status", "0");
			//商户交易限额控制
			Map ctrl = (Map) this.getSqlMap().queryForObject("pp.core.queryMerchantTransCtrlDynamic",para1);
			log.info("order======"+order.getAmount3());
			if (ctrl != null) {
				log.debug("日限额更新为[{}]"+((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
				ctrl.put("PerDayAmt",((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
				this.getSqlMap().update("pp.core.updateMerLimitCtrl",ctrl);
			}
			
		//订单关闭的话，按照交易失败处理
	    }else if("TRADE_CLOSED".equals(alipay_response.getTradeStatus())){
	    	order.setStatus(Constants.TRANS_STATUS_ERROR);
	    	order.setReturnCode("SYSTEMERROR");
	    	order.setReturnMsg("交易已关闭");
			//删除待查询记录
			this.getSqlMap().delete("pp.core.deleteUnpayOrderByTransSeqNo", para);
			//更新订单状态
			orderManager.updateOrder(order);
	    	
	    }else {
	    	
			para.put(Dict.PPSENDTIMES, sendTimes);
			this.getSqlMap().update("pp.core.updateQueryTimes", para);
	    	
	    }

		
		//发送商户通知
		if(isnotify){
			ctx.setVariable(order);
			merchantSendAction.execute(ctx);
		}
		return order;
		
	}
	
	/**
	 * 支付宝退款交易查询
	 * @param ctx
	 * @param alipayClient
	 * @param order
	 * @throws PeException
	 */
	
	
	private Order queryAlipayRFOrder(Context ctx,Order order ,Map para,int sendTimes) throws PeException {
		
		
		String status = order.getStatus();
		
		if (MiscUtil.isNullOrEmpty(status) || Constants.TRANS_STATUS_PROCESSING.equals(status)) {
			

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
	
		    }else if("10000".equals(alipay_response.getCode()) && !MiscUtil.isNullOrEmpty(alipay_response.getTradeNo())){
		    		
		    		order.setStatus(Constants.TRANS_STATUS_OK);
					
					order.setReturnCode(Constants.AAAAAAA);
					
					order.setReturnMsg("交易成功");
					
					order.setHostSeqNo(alipay_response.getTradeNo());//支付宝流水号
					
		    	}else{
		    		
		    		order.setStatus(Constants.TRANS_STATUS_ERROR);
		    		
		    	}
		    
			//更新订单状态
			orderManager.updateOrder(order);
			//删除待查询记录
			this.getSqlMap().delete("pp.core.deleteUnpayOrderByTransSeqNo", para);
	
		    }else {
				log.info("请求支付宝退款查询接口开始，平台流水["+order.getTransSeqNo()+"],状态为["+order.getStatus()+"]明确，不需查询后台交易");
			}
		
		return order;
		
	}
	
	

	public void setWxUtil(WxUtil wxUtil) {
		this.wxUtil = wxUtil;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public void setRefundurl(String refundurl) {
		this.refundurl = refundurl;
	}

	public void setTimes(String times) {
		this.times = times;
	}
	public void setMerchantSendAction(MerchantSendAction action) {
		merchantSendAction = action;
	}

	public void setMybankTransport(Transport mybankTransport) {
		this.mybankTransport = mybankTransport;
	}

	public void setMybankurl(String mybankurl) {
		this.mybankurl = mybankurl;
	}


	public void setAlipayClientManager(AlipayClientManager alipayClientManager) {
		this.alipayClientManager = alipayClientManager;
	}
	
	private void CloseOrderForMybank(Order order) throws Exception{
		Map paraMap = new HashMap();
		paraMap.put("bankId", order.getBankId());
		paraMap.put("departmentId", order.getDepartmentId());
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", paraMap);
		Map sendMap = new HashMap();
//		报文头
		sendMap.put("_TransName", "ALGB");
		sendMap.put("submitURL", mybankurl);
		sendMap.put("Appid", order.getAppid());
		sendMap.put("Function", "ant.mybank.bkmerchanttrade.payClose");
		sendMap.put("ReqTime", MiscUtil.getCurrentTimeString());
		sendMap.put("ReqMsgId", MiscUtil.getCurrentTimeString());
		sendMap.put("InputCharset", "UTF-8");
		sendMap.put("Reserve", ""); // 保留字段
		sendMap.put("SignType", "RSA"); // 签名方式
		sendMap.put("ReqTimeZone", "UTC+8"); // 报文发起时区
		sendMap.put("IsvOrgId", MerBankParameterMap.get("mybankIsvOrgId"));
		sendMap.put("MerchantId", order.getMerWxId());
		sendMap.put("OutTradeNo", order.getTransSeqNo());
		sendMap.put("mybankJfPriKey", MerBankParameterMap.get("mybankJfPriKey"));
		sendMap.put("mybankPubKey", MerBankParameterMap.get("mybankPubKey"));
		Map result =(Map) this.mybankTransport.submit(sendMap);
		String ResultStatus = (String) result.get("ResultStatus");
		if ("S".equals(ResultStatus)) {
			order.setStatus(Constants.TRANS_STATUS_ORDER_CLOSE);
			orderManager.updateOrder(order);
		}
	}
	
	
}
