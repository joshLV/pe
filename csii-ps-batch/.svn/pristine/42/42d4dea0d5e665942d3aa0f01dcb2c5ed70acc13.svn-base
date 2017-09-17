package com.csii.pp.clearing;

import java.util.Map;

import com.csii.pp.merchant.Merchant;
import com.csii.pp.order.Order;

public class DefaultClearingerFactory implements ClearingerFactory {

	private Map clearingers;


	/* (non-Javadoc)
	 * @see com.csii.pp.clearing.ClearingerFactory#getClearinger(java.lang.Object)
	 */
	public Clearinger getClearinger(Object data) {
		
		return (Clearinger) clearingers.get(((Order) data).getTransType());
		
	}
	
	/**
	 * @param clearingers the clearingers to set
	 */
	public void setClearingers(Map clearingers) {
		this.clearingers = clearingers;
	}

}
