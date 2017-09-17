package com.csii.alipay.zhilian.trans.action;

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

public class AftAlipayQryPayAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(AftAlipayQryPayAction.class);

	private OrderManager orderManager;
	@Override
	public void execute(Context context) throws PeException {

		Order order = (Order) context.getVariable();

		Map jsonMap = new HashMap();

			orderManager.updateOrderInOtAndHt(order);
            if("alipay".equals(order.getDepartmentId())){
            	if ("10000".equals(order.getReturnCode())) {
    				if (Constants.TRANS_STATUS_OK.equals(context.getData("result_code"))) {
    					jsonMap.put("ReturnCode", Constants.AAAAAAA);
    					jsonMap.put("ReturnMsg", "交易成功");
    				} else {
    					jsonMap.put("ReturnCode", order.getReturnCode());
    					jsonMap.put("ReturnMsg", order.getReturnMsg());
    				}
    			} else {
    				jsonMap.put("ReturnCode", order.getReturnCode());
					jsonMap.put("ReturnMsg", order.getReturnMsg());
    			}
            }

		if (context.getTransactionId().equals("APQO")) {
			jsonMap.put("trade_state", WxUtil.getTransStatus(order.getStatus()));
			jsonMap.put("bank_type", order.getBankType());
			jsonMap.put("total_fee", MiscUtil.MultiplyHundred(order.getAmount3()));
			jsonMap.put("out_trade_no", order.getTransSeqNo());
			jsonMap.put("cash_fee", MiscUtil.MultiplyHundred(order.getAmount()));
			jsonMap.put("already_refund_amt", MiscUtil.MultiplyHundred(order.getAmount1()));
			jsonMap.put("DepartmentId", order.getDepartmentId());
			jsonMap.put("trade_type", order.getPayType());
			jsonMap.put("transaction_id", order.getHostSeqNo());// 第三方单号
			jsonMap.put("trans_datetime", MiscUtil.timeToString1(order.getTransDateTime()));// 交易时间

			String merId = order.getSubMerchantId();
			jsonMap.put("mer_id", merId);// 商户号
			Map map = new HashMap();
			map.put("mer_id", merId);
			Merchant merchant = (Merchant) this.getSqlMap().queryForObject("pp.core.qryMerId", map);
			jsonMap.put("mer_name", merchant.getMerShortName());// 商户名

			jsonMap.put("cashier_name", order.getCashierName());// 收银员
			if(!MiscUtil.isNullOrEmpty(context.getData("userId"))){
				Map userMap1 = new HashMap();
				userMap1.put("userId", context.getData("userId"));
				User user1 = (User) this.getSqlMap().queryForObject("pp.core.qryUserInfo", userMap1);
				if(user1 !=null){
					jsonMap.put("refundAuthority", user1.getRefundAuth());
				}
			}
			
		}

		context.setData("json", jsonMap);
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
}
