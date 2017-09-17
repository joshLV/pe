package com.csii.batch.check;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
//import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;

public class ClearingChecker extends AbstractChecker {

	private OrderManager orderManager;
    
	private List   withDrawList;
	
	private ClearingEntity clearingEntity;
	/* 
	 * data 核心流水
	 * order 支付流水
	 */
	public boolean check(Map data) throws PeException {
		
		String hostSeqNo = MiscUtil.toStringAndTrim(data.get(Constants.HOSTSEQNO));
		String transSeqNo = MiscUtil.toStringAndTrim(data.get(Dict.PPTRANSSEQNO));
		
		String type = MiscUtil.toStringAndTrim(data.get("TransCode"));
		Order order = null;
		if("02".equals(type)){
		    order = orderManager.getBtClearTransByOrgTransSeqNo(transSeqNo);
		}else{
			order = orderManager.getBtClearTransByTransSeqNo(transSeqNo);
		}
		
//		Order order = orderManager.getBtClearTransByTransSeqNo(transSeqNo);
		
		
		//存在对应交易
		if (order != null) {
			BigDecimal transAmt = (BigDecimal) data.get(Dict.PPTRANSAMOUNT);
			
			order.setReturnCode(Constants.AAAAAAA);

			//交易完全匹配
			//超时交易未查证可能存在核心流水号为空情况
			if ((MiscUtil.isNullOrEmpty(order.getHostSeqNo()) || hostSeqNo.equals(order.getHostSeqNo()))
				&& order.getAmount3().compareTo(transAmt) == 0 ) {

				if (Constants.TRANS_STATUS_OK.equals(order.getStatus())
					|| Constants.TRANS_STATUS_CANCELED.equals(order.getStatus())
					|| Constants.TRANS_STATUS_SUB_WITHDRAW.equals(order.getStatus())
					|| Constants.TRANS_STATUS_ALL_WITHDRAW.equals(order.getStatus())||Constants.TRANS_STATUS_REFUND_OK.equals(order.getStatus())) {

					//对帐成功
					data.put(Dict.PPCHECKSTATUS, Constants.TRANS_CHECK_STATUS_OK);
					order.setStep(Constants.TRANS_STEP_CHECK_OK);

				} else {

					//对帐时如果发现核心成功，网关超时或者失败的交易将网关交易设置为成功
					if (Constants.TRANS_STATUS_ERROR.equals(order.getStatus())
						|| Constants.TRANS_STATUS_TIMEOUT.equals(order.getStatus())  
						|| MiscUtil.trimAndEquals(order.getStatus(), null)
						|| MiscUtil.trimAndEquals(order.getStatus(), "")
					) {
						order.setHostSeqNo(hostSeqNo);
						order.setStatus(Constants.TRANS_STATUS_OK);

						//退货交易对账成功，需修改原交易
						if("01".equals((order.getTransType()))){
							updateOriginal(order);
						}
						
						//撤销交易对账成功，需修改原交易
						if("02".equals((order.getTransType()))){
							updateOriginalForRevoke(order);
						}

					}
					order.setStatus(Constants.TRANS_STATUS_OK);
					order.setReturnCode(Constants.RESPONSE_CODE_SUCCESS);
					order.setStep(Constants.TRANS_STEP_CHECK_OK);

					//对帐核心成功，支付失败
					data.put(Dict.PPCHECKSTATUS, Constants.TRANS_CHECK_STATUS_PPERROR);
					data.put("BankId", order.getBankId());

					//插入对帐差错明细表
//					insertCheckError(data);

				}

			} else { //交易不完全匹配，正常情况下不应该发生这类问题

				log.error("清算交易明细表中存在不完全匹配的交易记录，请检查交易金额和帐号");
				log.error("核心交易：" + data); 
				log.error("订单信息：\n" + order);
				
				//如果清算明细表中响应吗和交易状态正确,将核心对账明细表的对账标志改为支付平台成功，核心失败
				if(Constants.RESPONSE_CODE_SUCCESS.equals(order.getReturnCode()) &&
				   (Constants.TRANS_STATUS_OK.equals(order.getStatus()) || Constants.TRANS_STATUS_CANCELED.equals(order.getStatus())||
				    Constants.TRANS_STATUS_SUB_WITHDRAW.equals(order.getStatus())||
					Constants.TRANS_STATUS_ALL_WITHDRAW.equals(order.getStatus())||Constants.TRANS_STATUS_REFUND_OK.equals(order.getStatus()))){
					order.setStep(Constants.TRANS_STEP_CHECK_PPERROR);
					data.put(Dict.PPCHECKSTATUS, Constants.TRANS_CHECK_STATUS_HOSTERROR);		
					insertCheckError(data);
				
				//将核心对账明细表的对账标志改为支付平台失败，核心成功
				}else{
					order.setStep(Constants.TRANS_STEP_CHECK_PPERROR);
					data.put(Dict.PPCHECKSTATUS, Constants.TRANS_CHECK_STATUS_PPERROR);
					data.put("BankId", order.getBankId());
				}
				//插入对帐差错明细表
//				insertCheckError(data);
			}
			
			//更新订单状态
			orderManager.updateClearOrder(order);
			orderManager.updateHistoryOrder(order);
			
			//更新后台交易明细状态
			updateHostHistory(data);

			return true;
		} else {
			//不存在对应交易
			return false;
		}

	}

	public void updateOriginal(Order order) {

		//modify at 2011-09-19, 与orgOrderReader保持一致, 发生退货交易的前提就是能够找到原订单, 更新订单与customerWithdrawAction保持一致	
		//订单实际支付金额：amount，订单金额amount3
//		Order originalOrder = orderManager.getHisOriginalOrder(order);
		Order originalOrder = orderManager.getOriginalOrder(order);
		
		//已退货金额
		BigDecimal alreadyWithdrawAmount = (BigDecimal) originalOrder.getAmount1();

		//退货交易成功
		//order表中的退货金额等于原退货金额加上本次退货金额
		alreadyWithdrawAmount = alreadyWithdrawAmount.add(order.getAmount3());
		originalOrder.setAmount1(alreadyWithdrawAmount);

		//原交易尚未退货金额
		originalOrder.setAmount2(originalOrder.getAmount3().subtract(alreadyWithdrawAmount));

		//设置原交易状态为已经退货,原交易日志修改时间为当前日期时间
		if (alreadyWithdrawAmount.compareTo(originalOrder.getAmount3()) == 0) {
			originalOrder.setStatus(Constants.TRANS_STATUS_ALL_WITHDRAW);

		} else {
			originalOrder.setStatus(Constants.TRANS_STATUS_SUB_WITHDRAW);
		}

		//更新历史交易明细表原交易
		orderManager.updateHistoryOrder(originalOrder);
	}
	
	public void updateOriginalForRevoke(Order order) {

		Order originalOrder = orderManager.getOriginalOrder(order);
		//已退货金额
		originalOrder.setAmount1(order.getAmount3());
		//原交易尚未退货金额
		originalOrder.setAmount2(originalOrder.getAmount3().subtract(order.getAmount3()));
		originalOrder.setStatus(Constants.TRANS_STATUS_CANCELED);
		//更新历史交易明细表原交易
		orderManager.updateHistoryOrder(originalOrder);
	}

	/**
	 * @param manager
	 */
	public void setOrderManager(OrderManager manager) {
		orderManager = manager;
	}

	public void setWithDrawList(List withDrawList) {
		this.withDrawList = withDrawList;
	}
	
}
