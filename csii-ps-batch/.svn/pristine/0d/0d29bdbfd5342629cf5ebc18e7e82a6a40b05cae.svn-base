package com.csii.pp.fee.calc;

import java.util.Map;

import com.csii.pe.core.PeException;
import com.csii.pp.merchant.MerchantSett;

public class DefaultFeeCalcFactory implements FeeCalcFactory {

	private Map feeFalcs;

	/* (non-Javadoc)
	 * @see com.csii.pp.fee.calc.FeeCalcFactory#getFeeCalc(com.csii.pp.order.Order)
	 */
	public FeeCalc getFeeCalc(MerchantSett merchantSett) throws PeException {
		FeeCalc ret = (FeeCalc) feeFalcs.get(merchantSett.getFeeType());
		if (ret == null) {
			throw new PeException("fee_mode_not_support");
		}
		return ret;
	}

	/**
	 * @param feeFalcs the feeFalcs to set
	 */
	public void setFeeFalcs(Map feeFalcs) {
		this.feeFalcs = feeFalcs;
	}
}
