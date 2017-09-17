package com.csii.pp.clearing;

import com.csii.pe.core.PeException;

public interface SettlementBooker {

	public void book(Object data) throws PeException;

}