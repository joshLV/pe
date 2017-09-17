package com.csii.weixin.mgmt.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.model.User;

public class UserInfoQryAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(UserInfoQryAction.class);

	@Override
	public void execute(Context ctx) throws PeException {
		String userId = (String) ctx.getData("userId");
		String merId = (String) ctx.getData("merId");

		Map map = new HashMap();
		map.put("userId", userId);
		map.put("merId", merId);
		User userDB = (User) this.getSqlMap().queryForObject("pp.core.qryUserInfo", map);
		if (userDB == null) {
			logger.error("用户[USER_ID:{}]不存在", userId);
			throw new PeException("E00014");
		}

		Map jsonMap = new HashMap();

		jsonMap.put("userId", userDB.getUserId());
		jsonMap.put("userCode", userDB.getUserCode());
		jsonMap.put("userName", userDB.getUserName());
		jsonMap.put("userPhone", userDB.getUserPhone());
		jsonMap.put("userStatus", userDB.getUserStatus());
		jsonMap.put("refundAuthority", userDB.getRefundAuth());
		jsonMap.put("seqAuthority", userDB.getSeqAuth());
		jsonMap.put("statisticsAuthority", userDB.getStatisticAuth());
		jsonMap.put("cashierManage", userDB.getCashiermanage()); 

		jsonMap.put("ReturnCode", "000000");
		jsonMap.put("ReturnMsg", "交易成功");

		ctx.setData("json", jsonMap);
	}
}
