package com.csii.pp.fee.calc;

import java.math.BigDecimal;

import com.csii.pe.core.PeException;
import com.csii.pp.merchant.MerchantSett;
import com.csii.pp.util.MiscUtil;



/**
 * @description  按百分比的费用计算类
 */

public class PercentFeeCalc extends AbstractFeeCalc {

	public BigDecimal calculate(MerchantSett merchantSett, BigDecimal amount) throws PeException {
		return (amount.multiply(new BigDecimal(MiscUtil
				.toStringAndTrim(merchantSett.getFeeAmt())))).setScale(2,
				BigDecimal.ROUND_HALF_UP);
	}

}
