package com.csii.pp.clearing.fee;

import com.csii.pe.core.PeException;

public interface FeeReceiver {

	public void receive(Object data) throws PeException;

}
