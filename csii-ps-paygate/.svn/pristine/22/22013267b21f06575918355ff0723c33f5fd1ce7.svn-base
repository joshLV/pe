package com.csii.weixin.mgmt.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.model.User;
import com.csii.pp.util.BCrypt;

public class UserLoginAction extends AbstractAction {
	private static Logger logger = LoggerFactory.getLogger(UserLoginAction.class);
	
	private String domain;

	@Override
	public void execute(Context ctx) throws PeException {

		String loginUserId = (String) ctx.getData("userId");
		String loginPassword = (String) ctx.getData("password");

		Map userDataMap = new HashMap();
		userDataMap.put("userId", loginUserId);
		User user = (User) this.getSqlMap().queryForObject("pp.core.qryUserInfo", userDataMap);

		if (user == null) {
			logger.error("用户名[{}]不存在.", loginUserId);
			throw new PeException("E00014");
		}
		
		if("1".equals(user.getUserStatus())){
			throw new PeException("E00021");
		}

		Map result = new HashMap();
		if (BCrypt.checkpw(loginPassword, user.getPassword())) {
			Map merDataMap = new HashMap();
			merDataMap.put("mer_id", user.getMerId());
			Merchant merchant = (Merchant) this.getSqlMap().queryForObject("pp.core.qryMerId", merDataMap);

			if (merchant == null) {
				logger.error("编号为[{}]的商户不存在.", user.getMerId());
				throw new PeException("E00003");
			}
			String merQrUrl = domain+merchant.getMerToken();
			result.put("userId", user.getUserId());
			result.put("userName", user.getUserName());
			result.put("merId", user.getMerId());
			result.put("merName", merchant.getMerShortName());
			result.put("merQrUrl", merQrUrl);
			result.put("refundAuthority", user.getRefundAuth());
			result.put("seqAuthority", user.getSeqAuth());
			result.put("statisticsAuthority", user.getStatisticAuth());
			result.put("cashierManage", user.getCashiermanage());
			result.put("ReturnCode", "000000");
			result.put("ReturnMsg", "交易成功");

			ctx.setData("json", result);
		} else {
			logger.error("用户[USER_ID:{}]登录验证失败", loginUserId);
			throw new PeException("E00015");
		}
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
