package com.csii.weixin.mgmt.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.model.User;

public class UserListInfoQryAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(UserListInfoQryAction.class);

	@Override
	public void execute(Context ctx) throws PeException {
		String merId = (String) ctx.getData("merId");

		Map dataMap = new HashMap();
		dataMap.put("merId", merId);
		List<User> userList = this.getSqlMap().queryForList("pp.core.qryUserInfo", dataMap);

		Map resultMap = new HashMap();

		User[] users = new User[userList.size()];
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);

			User u = new User();
			u.setUserId(user.getUserId());
			u.setUserName(user.getUserName());
			u.setUserLevel(user.getUserLevel());
			u.setMerId(user.getMerId());
			u.setCreateUserId(user.getCreateUserId());
			u.setCreateDateTime(user.getCreateDateTime());
			u.setModifyUserId(user.getModifyUserId());
			u.setModifyDateTime(user.getModifyDateTime());
			u.setUserPhone(user.getUserPhone());
			u.setUserCode(user.getUserCode());
			u.setUserStatus(user.getUserStatus());
			users[i] = u;
		}

		resultMap.put("userInfo", users);

		resultMap.put("ReturnCode", "000000");
		resultMap.put("ReturnMsg", "交易成功");

		ctx.setData("json", resultMap);
	}
}
