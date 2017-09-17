package com.csii.weixin.mgmt.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.model.User;
import com.csii.pp.util.BCrypt;
import com.csii.pp.util.MiscUtil;

public class ModifyUserPasswordAction extends AbstractAction {

	private static Logger logger = LoggerFactory.getLogger(ModifyUserPasswordAction.class);

	@Override
	public void execute(Context ctx) throws PeException {
		String userId = (String) ctx.getData("userId");
		String userCode = (String) ctx.getData("userCode");
		String password = (String) ctx.getData("password");
		String merId = ctx.getString("merId");
		String orgPassword = ctx.getString("orgPassword");
		String confirmPassword = ctx.getString("confirmPassword");

		Map map = new HashMap();
		map.put("userId", userId);
		map.put("merId", merId);
		User userDB = (User) this.getSqlMap().queryForObject("pp.core.qryUserInfo", map);
		if (userDB == null) {
			logger.error("用户[USER_ID:{}]不存在", userId);
			throw new PeException("E00014");
		}
		
		if(!MiscUtil.trimAndEquals(password, confirmPassword)){
			throw new PeException("E00024");
		}
		
		if (!BCrypt.checkpw(orgPassword, userDB.getPassword())) {
			logger.error("用户[USER_ID:{}]修改密码，原密码验证失败", userId);
			throw new PeException("E00032");
		}
		
		User user = new User();
		user.setUserId(userId);
		user.setUserCode(userCode);
		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		user.setMerId(merId);
		this.getSqlMap().update("pp.core.updateUserInfo", user);

		Map jsonMap = new HashMap();
		jsonMap.put("ReturnCode", "000000");
		jsonMap.put("ReturnMsg", "交易成功");
		ctx.setData("json", jsonMap);
	}
}
