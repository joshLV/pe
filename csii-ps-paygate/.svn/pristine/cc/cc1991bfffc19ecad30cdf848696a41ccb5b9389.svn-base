package com.csii.weixin.common.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.model.User;
import com.csii.pp.order.Order;
import com.csii.pp.order.OrderManager;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.WxUtil;

public class AftQryRefundAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(AftQryRefundAction.class);

	private OrderManager orderManager;

	public void execute(Context context) throws PeException {

		Order order = (Order) context.getVariable();

		orderManager.updateOrderInOtAndHt(order);

		Map jsonMap = new HashMap();
		Boolean flag = (Boolean) context.getData("QryWxTransStatus");// 是否通过微信查询订单状态
		if (flag) {
			 if("weixin".equals(order.getDepartmentId())){
				 String returnCode = (String) context.getData("return_code");
					String returnMsg = (String) context.getData("return_msg");
					String resultCode = (String) context.getData("result_code");
					String errorCode = (String) context.getData("err_code");
					String errorMsg = (String) context.getData("err_code_des");

					if ("SUCCESS".equals(returnCode)) {
						if ("SUCCESS".equals(resultCode)) {
							jsonMap.put("ReturnCode", Constants.AAAAAAA);
							jsonMap.put("ReturnMsg", "");
						} else {
							jsonMap.put("ReturnCode", errorCode);
							jsonMap.put("ReturnMsg", errorMsg);
						}
					} else {
						jsonMap.put("ReturnCode", returnCode);
						jsonMap.put("ReturnMsg", returnMsg);
					}
			 }else{
				 jsonMap.put("ReturnCode", context.getData("ReturnCode"));
				 jsonMap.put("ReturnMsg", context.getData("ReturnMsg"));
			 }
			
		}else{
			jsonMap.put("ReturnCode", Constants.AAAAAAA);
			jsonMap.put("ReturnMsg", "交易成功");
		}

		

		String transId = context.getTransactionId();
		Order orgOrder = orderManager.getOriginalOrder(order);
		if (transId.equals("TRCX") || order.getTransId().equals("APRF")) {
			jsonMap.put("out_trade_no", order.getOrgTransSeqNo());
			jsonMap.put("total_fee", MiscUtil.MultiplyHundred(orgOrder.getAmount3()));
			jsonMap.put("out_refund_no", order.getTransSeqNo());
			jsonMap.put("refund_fee", MiscUtil.MultiplyHundred(order.getAmount()));
			jsonMap.put("refund_status", WxUtil.getTransStatus(order.getStatus()));
			String merId = order.getSubMerchantId();
			jsonMap.put("mer_id", merId);// 商户号
			Map map = new HashMap();
			map.put("mer_id", merId);
			Merchant merchant = (Merchant) this.getSqlMap().queryForObject("pp.core.qryMerId", map);
			jsonMap.put("mer_name", merchant.getMerShortName());// 商户名
			jsonMap.put("cashier_name", order.getCashierName());// 收银员
			jsonMap.put("transaction_id", order.getOrgHostSeqNo());
			jsonMap.put("DepartmentId", order.getDepartmentId());
			jsonMap.put("trade_type", order.getPayType());
			jsonMap.put("refund_datetime", MiscUtil.timeToString1(order.getTransDateTime()));
			jsonMap.put("trans_datetime", MiscUtil.timeToString1(order.getOrgTransDateTime()));
			
		}

		context.setData("json", jsonMap);
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
}
