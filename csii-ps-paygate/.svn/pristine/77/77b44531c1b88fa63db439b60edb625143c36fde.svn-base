package com.csii.weixin.mgmt.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.merchant.Merchant;

public class MerInfoQryAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(MerInfoQryAction.class);

	@Override
	public void execute(Context ctx) throws PeException {
		String merId = (String) ctx.getData("merId");

		Map dataMap = new HashMap();
		dataMap.put("mer_id", merId);
		Merchant merchant = (Merchant) this.getSqlMap().queryForObject("pp.core.qryMerId", dataMap);

		if (merchant == null) {
			logger.error("编号为[{}]的商户不存在.", merId);
			throw new PeException("E00003");
		}

		Map resultMap = new HashMap();
		resultMap.put("id", merchant.getId());
		resultMap.put("merShortName", merchant.getMerShortName());
		resultMap.put("manageType", merchant.getManageType());// 经营类型

		resultMap.put("merAddr", merchant.getMerAddr());// 详细地址

		resultMap.put("merSettAcctBankName", merchant.getMerSettAcctBankName());// 开户行
		resultMap.put("merSettAcctOpenName", merchant.getMerSettName());// 开户名
		resultMap.put("merSettAcctNo", merchant.getMerSettAcctNo());// 卡号

		resultMap.put("ReturnCode", "000000");
		resultMap.put("ReturnMsg", "交易成功");

		ctx.setData("json", resultMap);
	}
}
