package com.csii.weixin.common.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.config.support.SqlMapAware;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.merchant.MerchantSett;
import com.csii.pp.model.User;
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;

public class PrePayCheckOrderAction extends AbstractAction implements SqlMapAware {

	private static Logger logger = LoggerFactory.getLogger(PrePayCheckOrderAction.class);

	private SqlMapClientOperations sqlMap;

	@Override
	public void execute(Context paramContext) throws PeException {

		Order order = (Order) paramContext.getVariable();

		if (order.getAmount().compareTo(Constants.ZERO) <= 0) {
			throw new PeException("E00004");
		}

		Map merMap = new HashMap();
		merMap.put("mer_id", order.getSubMerchantId());
		Merchant merchant = (Merchant) sqlMap.queryForObject("pp.core.qryMerId", merMap);
		if (!MiscUtil.trimAndEquals(merchant.getMerStatus(), "0")) {
			logger.error("商户户状态[{}]非正常，无法交易", merchant.getMerStatus());
			throw new PeException("E00013");
		}
		
		Map para = new HashMap();
		para.put("merId", order.getSubMerchantId());
		para.put("payType", order.getPayType());
		para.put("departmentId", order.getDepartmentId());
		MerchantSett merchantSett =(MerchantSett) this.sqlMap.queryForObject("pp.core.queryMerchantSett", para);
		
		if(merchantSett == null){
			logger.error("商户支付类型[{}]未开通，无法交易", order.getPayType());
			throw new PeException("E00034");
		}

	}

	@Override
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}

}
