package com.csii.weixin.mgmt.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.model.User;

public class ModifyUserAction extends AbstractAction {

	private static Logger logger = LoggerFactory.getLogger(ModifyUserAction.class);

	@Override
	public void execute(Context ctx) throws PeException {
		String userId = (String) ctx.getData("userId");
		String userName = (String) ctx.getData("userName");
		String userLevel = (String) ctx.getData("userLevel");
		String merId = (String) ctx.getData("merId");
		String modifyUserId = (String) ctx.getData("modifyUserId");
		String usrStatus = (String) ctx.getData("userStatus");// 0 - 开启；1 - 关闭
		String refundAuth = (String) ctx.getData("refundAuthority");// 0 - 开启；1 - 关闭
		String seqAuth = (String) ctx.getData("seqAuthority");// 0 - 开启；1 - 关闭
		String statisticAuth = (String) ctx.getData("statisticsAuthority");// 0 - 开启；1 - 关闭
		String cashiermanage = (String) ctx.getData("cashierManage");// 0 - 开启；1 - 关闭

		Map map = new HashMap();
		map.put("userId", userId);
		map.put("merId", merId);
		User userFromDB = (User) this.getSqlMap().queryForObject("pp.core.qryUserInfo", map);
		if (userFromDB == null) {
			logger.error("用户[USER_ID:{}]不存在", userId);
			throw new PeException("E00014");
		}

		User user = new User();
		user.setUserId(userId);
		user.setUserName(userName);
		user.setUserLevel(userLevel);
		user.setMerId(merId);
		user.setModifyUserId(modifyUserId);
		user.setModifyDateTime(new Date());
		user.setUserStatus(usrStatus);
		user.setRefundAuth(refundAuth);
		user.setSeqAuth(seqAuth);
		user.setStatisticAuth(statisticAuth);
		user.setCashiermanage(cashiermanage);

		this.getSqlMap().update("pp.core.updateUserInfo", user);

		Map result = new HashMap();
		result.put("ReturnCode", "000000");
		result.put("ReturnMsg", "交易成功");

		ctx.setData("json", result);
	}
}
