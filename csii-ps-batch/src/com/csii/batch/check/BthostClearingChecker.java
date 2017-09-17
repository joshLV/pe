package com.csii.batch.check;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;

public class BthostClearingChecker extends AbstractChecker {
	
	private OrderManager orderManager;

/*
 * (non-Javadoc)
 * @see com.csii.pp.check.Checker#check(com.csii.pp.order.Order)
 * 对账的处理逻辑
 * 以bt_fee_sett表的未对账订单跟核心表bt_host_trans进行对账
 * 如果核心表bt_host_trans 不存在相应的记录
 *     无论bt_mer_sett表的订单的状态是成功、失败、未知 都记录差错表，并且对于bt_mer_sett表的订单状态
 *     是成功的情况，设置交易步骤为对账成功，目的是下面的笔数检查抛错，需人工干预
 * 如果核心表bt_host_trans存在相应的记录
 *   匹配：
 *       对于订单的状态是成功的话，设置交易步骤为对账成功
 *       对于订单的状态是失败、未知的话，设置交易步骤为对账成功，插入差错表，参与清分，对于如果是退货的话
 *       还需修改原交易的一退货金额和未退货金额
 *   不匹配：
 *      无论成功、失败都将插入到差错表，订单步骤为对账失败
 */
	public void check(Order order) throws PeException {
	}

	public OrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public boolean check(Map data) throws PeException {
		String hostSeqNo = MiscUtil.toStringAndTrim(data.get(Constants.HOSTSEQNO));
		String transSeqNo = MiscUtil.toStringAndTrim(data.get(Dict.PPTRANSSEQNO));
		Date preClearDate = MiscUtil.rolDate((Date)data.get("HostDate"), 1);
		String type = MiscUtil.toStringAndTrim(data.get("TransCode"));
		Order order = null;
		if("02".equals(type)){
			order = orderManager.getHistoryOrderByOrgTransSeqNo(transSeqNo,preClearDate);
		}else{
			order = orderManager.getHistoryOrder(transSeqNo,preClearDate);
		}
//		Order order = orderManager.getHistoryOrder(transSeqNo,preClearDate);
		if(order == null){
			//不存在对应交易
			data.put(Dict.PPCHECKSTATUS, Constants.TRANS_CHECK_STATUS_PPHOSTEXITSERROR);
			insertCheckError(data);
		}else{
			BigDecimal transAmt = (BigDecimal) data.get(Dict.PPTRANSAMOUNT);
			order.setClearDate((Date)data.get("HostDate"));
			order.setHostDate((Date)data.get("HostDate"));
			if (order.getAmount3().compareTo(transAmt) == 0 ) { //匹配的话
				data.put(Dict.PPCHECKSTATUS, Constants.TRANS_CHECK_STATUS_PPHOSTSUCCSERROR);
//				if(!Constants.AAAAAAA.equals(order.getRespCode())){ //因为成功的，已经清分了
					order.setReturnCode(Constants.AAAAAAA);
//					order.setCheckStatus(Constants.TRANS_CHECK_STATUS_OK);
					order.setHostSeqNo(hostSeqNo);
					order.setStep(Constants.TRANS_STEP_CHECK_OK);
					//插入bt_clear_trans表中
					super.sqlMap.insert("pp.core.insertBtClearTransOrder",order);
//				}
				//插入对帐差错明细表
//				insertCheckError(data);
			} else { //交易不完全匹配，正常情况下不应该发生这类问题

				log.error("清算交易明细表中存在不完全匹配的交易记录，请检查交易金额和帐号");
				log.error("核心交易：" + data); 
				log.error("订单信息：\n" + order);
				data.put(Dict.PPCHECKSTATUS, Constants.TRANS_CHECK_STATUS_PPHOSTERROR);
				order.setStatus(Constants.TRANS_STATUS_ERROR);
				order.setStep(Constants.TRANS_CHECK_STATUS_PPHOSTERROR);
				data.put("BankId", order.getBankId());
//				order.setCheckStatus(Constants.TRANS_CHECK_STATUS_PPHOSTERROR);
				//插入对帐差错明细表
				insertCheckError(data);
			}
			
			//更新订单状态
			//orderManager.updateClearOrder(order);
			orderManager.updateHistoryOrder(order);
			
			//更新后台交易明细状态
			updateHostHistory(data);
		}
		return false;
	}
	
}
