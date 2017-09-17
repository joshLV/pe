package com.csii.weixin.mgmt.action;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.model.User;
import com.csii.pp.util.BCrypt;
import com.csii.pp.util.MiscUtil;

public class ForgetPasswdAction extends AbstractAction{

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		String userPhone = ctx.getString("userPhone");
//		String merId = ctx.getString("merId");
		String inputMsg = ctx.getString("inputMsg");
		String SetPasswd = ctx.getString("SetPasswd");
		String ConfirmPasswd = ctx.getString("ConfirmPasswd");
		Map userMap = new HashMap();
		userMap.put("userId", userPhone);
		User user = (User) this.getSqlMap().queryForObject("pp.core.qryUserInfo", userMap);
		if(user == null){
			log.error("收银员不存在");
			throw new PeException("E00018");
		}
		Map phoneMap = new HashMap();
		phoneMap.put("userPhone", user.getUserPhone());
		Map verifyCodeMap = (Map) this.getSqlMap().queryForObject("pp.core.qryVerifyCode", phoneMap);
		if(verifyCodeMap !=null){
			String verifyCode = (String) verifyCodeMap.get("verifyCode");
			if(!inputMsg.equals(verifyCode)){
				throw new PeException("E00029");
			}
			long constant  = 300000;
			Date date1 = (Date)verifyCodeMap.get("createTime");
			Date date2 = MiscUtil.getCurrentDate();
			log.info(date1.getTime());
			log.info(date2.getTime());
			long cha =Math.abs(date1.getTime()-date2.getTime());
			log.info(cha);
			if(cha>constant){
				throw new PeException("E00030");
			}
		}else{
			throw new PeException("E00028");
		}
		
		user.setPassword(BCrypt.hashpw(SetPasswd, BCrypt.gensalt()));
		getSqlMap().delete("pp.core.deleteVerifyCode", userMap);
		this.getSqlMap().update("pp.core.updateUserInfo", user);

		Map jsonMap = new HashMap();
		jsonMap.put("ReturnCode", "000000");
		jsonMap.put("ReturnMsg", "交易成功");
		ctx.setData("json", jsonMap);
		
	}
	
	public static void main(String args[]){
		System.out.print(BCrypt.hashpw("12345678", BCrypt.gensalt()));
	}

}
