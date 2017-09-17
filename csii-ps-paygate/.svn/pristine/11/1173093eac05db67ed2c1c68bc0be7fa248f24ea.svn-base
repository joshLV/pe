package com.csii.weixin.common.action;

import java.math.BigDecimal;
import java.util.Date;
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
import com.csii.pp.order.Order;
import com.csii.pp.util.MiscUtil;

public class MerchantTransCtrlChecker extends AbstractAction implements SqlMapAware {

	private static Logger logger = LoggerFactory.getLogger(MerchantTransCtrlChecker.class);

	private SqlMapClientOperations sqlMap;

	@Override
	public void execute(Context paramContext) throws PeException {

		Order order = (Order) paramContext.getVariable();
		
		Map merMap = new HashMap();
		merMap.put("mer_id", order.getSubMerchantId());
		Merchant merchant = (Merchant) sqlMap.queryForObject("pp.core.qryMerId", merMap);
		//检查商户是否存在
		if (merchant == null) {
			logger.error("无此商户[{}]", order.getSubMerchantId());
			throw new PeException("E00013");
		}

		Map para = new HashMap();
		para.put("MerchantId", order.getSubMerchantId());
		para.put("Status", "0");
		//商户交易限额控制
		Map ctrl = (Map) this.sqlMap.queryForObject("pp.core.queryMerchantTransCtrlDynamic",para);
		if (ctrl != null) {
			//更新客户单日限额累计值
			Date transDate = (Date) order.getTransDate();
			if (ctrl.get("PerDayAmtDate") != null && transDate.getDate() == ((Date) ctrl.get("PerDayAmtDate")).getDate()) {
				//此处不直接扣减限额，由成功状态判断是否扣减限额。
				//ctrl.put("PerDayAmt",((BigDecimal) ctrl.get("PerDayAmt")).add(order.getAmount3()));
			} else {
				ctrl.put("PerDayAmt",Constants.ZERO);
				ctrl.put("PerDayAmtDate",transDate);
			}
			this.sqlMap.update("pp.core.updateMerLimitCtrl",ctrl);
			BigDecimal tranAmount = (BigDecimal) order.getAmount3();//订单金额
			if (tranAmount.compareTo((BigDecimal) ctrl.get("PerTransLimit")) >= 0) { //单笔限额校验
				logger.error("商户单笔限额超限");
				throw new PeException("E00042");
			}
			BigDecimal dayAddAmount = (BigDecimal) ctrl.get("PerDayAmt");
			if (dayAddAmount.compareTo((BigDecimal) ctrl.get("PerDayLimit")) >= 0) {//单日累计交易额校验
				logger.error("商户单日累计限额超限");
				throw new PeException("E00037");
			}
		}
		
	}

	@Override
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}

}
