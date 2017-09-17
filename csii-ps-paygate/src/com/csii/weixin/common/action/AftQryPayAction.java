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
import com.csii.weixin.notify.action.MerchantSendAction;

public class AftQryPayAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(AftQryPayAction.class);

	private OrderManager orderManager;
	private MerchantSendAction merchantSendAction;
	@Override
	public void execute(Context context) throws PeException {

		Order order = (Order) context.getVariable();

		Map jsonMap = new HashMap();

		Boolean flag = (Boolean) context.getData("QryWxTransStatus");// 是否通过微信查询订单状态
		if (flag) {
			// 更新订单状态
			orderManager.updateOrderInOtAndHt(order);
            if("weixin".equals(order.getDepartmentId())){
            	if ("SUCCESS".equals(context.getData("return_code"))) {
    				if ("SUCCESS".equals(context.getData("result_code"))) {
    					jsonMap.put("ReturnCode", Constants.AAAAAAA);
    					jsonMap.put("ReturnMsg", "交易成功");
    				} else {
    					jsonMap.put("ReturnCode", context.getData("err_code"));
    					jsonMap.put("ReturnMsg", context.getData("err_code_des"));
    				}
    			} else {
    				jsonMap.put("ReturnCode", context.getData("return_code"));
    				jsonMap.put("ReturnMsg", context.getData("return_msg"));
    			}
            }else{
            	jsonMap.put("ReturnCode", context.getData("ReturnCode"));
				jsonMap.put("ReturnMsg", context.getData("ReturnCode"));
            }
			
		} else {
			jsonMap.put("ReturnCode", Constants.AAAAAAA);
			jsonMap.put("ReturnMsg", "交易成功");
		}

		if (context.getTransactionId().equals("TPCX")) {
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
		
		//如果是支付成功则发送后台通知。因为会频繁查询此交易，所以不在此通知。
//		if ("00".equals(order.getTransType())) {
//			String status = (String)order.getStatus();
//			if("00".equals(status)||"03".equals(status)||"04".equals(status)||"05".equals(status)){
//				//发送商户通知
//				context.setVariable(order);
//				merchantSendAction.execute(context);
//			}
//		}
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public void setMerchantSendAction(MerchantSendAction action) {
		merchantSendAction = action;
	}
}
