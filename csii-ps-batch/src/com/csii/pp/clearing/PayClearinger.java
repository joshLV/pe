package com.csii.pp.clearing;


import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;

/**
 * 
 * 支付交易清分
 * 
 */
public class PayClearinger extends AbstractClearinger {

	/* (non-Javadoc)
	 * @see com.csii.pp.clearing.Clearinger#clear(java.lang.Object)
	 */
	public void clear(Object data) throws PeException {
		
		Order order = (Order) data;
		
//		收取商户手续费
		feeReceiver.receive(order);

		
//		商户支付结算
		settlementBooker.book(order);

//		交易手续费分润
		profitAssigner.assign(order);
		
//		如果订单状态没有被设置为已结算,则置为清分成功
		if(!MiscUtil.trimAndEquals(Constants.TRANS_STEP_SETT_OK,order.getStep())){
			order.setStep(Constants.TRANS_STEP_CLEAR_OK);
		}

//		更新清算明细表的处理阶段
//		orderManager.updateDayClearOrder(order);
		orderManager.updateClearOrder(order);

//		更新历史交易明细表的处理阶段
		orderManager.updateHistoryOrder(order);

	}

}
