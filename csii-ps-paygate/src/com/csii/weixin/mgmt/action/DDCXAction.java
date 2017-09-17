package com.csii.weixin.mgmt.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.model.User;
import com.csii.pp.util.MiscUtil;

public class DDCXAction extends AbstractAction {

	@Override
	public void execute(Context ctx) throws PeException {
		BigDecimal paperNo = new BigDecimal(ctx.getString("pageNo"));
		String cashierId = ctx.getString("cashierId");
		String sub_mch_id = ctx.getString("sub_mch_id");
		String beginDate = ctx.getString("beginDate");
		String endDate = ctx.getString("endDate");
		BigDecimal pageNum = new BigDecimal(ctx.getString("pageNum"));
		String transType = ctx.getString("transType");
		String payType = ctx.getString("payType");
		String departmentId = MiscUtil.toStringAndTrim(ctx.getData("departmentId"));
		String transStatus = ctx.getString("transStatus");
		String transSeqNo = ctx.getString("transSeqNo");
		String userId = ctx.getString("userId");

		Map userDataMap = new HashMap();
		userDataMap.put("userId", userId);
		User user = (User) this.getSqlMap().queryForObject("pp.core.qryUserInfo", userDataMap);
		Map map = new HashMap();
		map.put("SubMerId", sub_mch_id);
		map.put("BeginDate", beginDate);
		map.put("EndDate", endDate);
		map.put("TransType", transType);
//		if("1".equals(user.getSeqAuth())){
			map.put("CashierId", cashierId);
//		}
		map.put("payType", payType);
		map.put("transStatus", transStatus);
		map.put("transSeqNo", transSeqNo);
		map.put("departmentId", departmentId);
		map.put("bankId", MiscUtil.toStringAndTrim(ctx.getData("bankId")));

		BigDecimal recordIndex = pageNum.multiply(paperNo.subtract(new BigDecimal(1)));
		map.put("recordIndex", recordIndex.toString());
		map.put("pageSize", pageNum.toString());

		List orderList = this.getSqlMap().queryForList("pp.core.queryOrderListDetail", map);

		int count = orderList.size();

		Map totalInfo = (Map) this.getSqlMap().queryForObject("pp.core.queryOrderTotalInfo", map);

		Map json = new HashMap();
		json.put("ReturnCode", Constants.AAAAAAA);
		json.put("ReturnMsg", "交易成功");

		json.put("TotalCount", totalInfo.get("totalNum"));
		if(MiscUtil.isNullOrEmpty(totalInfo.get("totalAmt"))){
			json.put("TotalAmt", "0.00");
		}else{
			json.put("TotalAmt", ((BigDecimal)totalInfo.get("totalAmt")).toString());
		}
		

		json.put("QueryCount", count);
		json.put("SendList", orderList);

		ctx.setData("json", json);
	}
}
