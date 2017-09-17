package com.csii.weixin.common.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;

public class PreQryAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(PreQryAction.class);
    private OrderManager orderManager;
	public void execute(Context context) throws PeException {
		String tradeNo = "";

		String transId = context.getTransactionId();
		if (transId.equals("TPCX")) {
			tradeNo = (String) context.getData("out_trade_no");
			logger.info("[{}]支付订单[{}]查询.", transId, tradeNo);
		} else if (transId.equals("TRCX")) {
			tradeNo = (String) context.getData("out_refund_no");
			logger.info("[{}]退款订单[{}]查询.", transId, tradeNo);
		} else if (transId.equals("APQO")) {
			tradeNo = (String) context.getData("out_trade_no");
			logger.info("[{}]支付宝直连订单[{}]查询.", transId, tradeNo);
		} else {
			throw new PeException("E00011");
		}

		Map map = new HashMap();
		map.put("TransSeqNo", tradeNo);

		Order order = this.orderManager.getOrder(tradeNo);

		if (order == null) {
			throw new PeException("E00012", new Object[] { map.get("TransSeqNo") });
		} else {
			context.setVariable(order);
		}
		Map para = new HashMap();
		para.put("bankId", order.getBankId());
		para.put("departmentId", order.getDepartmentId());
		Map MerBankParameterMap = (Map) this.getSqlMap().queryForObject("pp.core.queryMerBankParameter", para);
		context.setData("MerBankParameterMap", MerBankParameterMap);
	}
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
	
}
