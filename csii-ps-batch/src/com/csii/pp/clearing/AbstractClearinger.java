package com.csii.pp.clearing;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;
import com.csii.pp.clearing.fee.FeeReceiver;
import com.csii.pp.fee.calc.FeeCalcFactory;
import com.csii.pp.order.OrderManager;

public abstract class AbstractClearinger implements Clearinger, MessageSourceAware,SqlMapAware {
	
	protected MessageSource messageSource;
	
	protected OrderManager orderManager;
	
	protected FeeCalcFactory feeCalcFactory;
	
	protected SqlMapClientOperations sqlMap;

	protected ProfitAssigner profitAssigner;

	protected FeeReceiver feeReceiver;

	protected SettlementBooker settlementBooker;
	
	protected static Log log = LogFactory.getLog(AbstractClearinger.class);

	/**
	 * @param orderManager the orderManager to set
	 */
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
	/* (non-Javadoc)
	 * @see com.csii.pp.action.AbstractAction#setMessageSource(org.springframework.context.MessageSource)
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	public void setFeeCalcFactory(FeeCalcFactory feeCalcFactory) {
		this.feeCalcFactory = feeCalcFactory;
	}
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}
	public static Log getLog() {
		return log;
	}
	public static void setLog(Log log) {
		AbstractClearinger.log = log;
	}
	public void setProfitAssigner(ProfitAssigner profitAssigner) {
		this.profitAssigner = profitAssigner;
	}
	public void setFeeReceiver(FeeReceiver feeReceiver) {
		this.feeReceiver = feeReceiver;
	}
	public void setSettlementBooker(SettlementBooker settlementBooker) {
		this.settlementBooker = settlementBooker;
	}

}
