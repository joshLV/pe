package com.csii.weixin.mgmt.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.model.User;
import com.qcloud.sms.SmsSingleSender;
import com.qcloud.sms.SmsSingleSenderResult;

public class SmsSendAction extends AbstractAction{
	
	private int msgAppId;
	private String msgAppKey;

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		String userPhone = ctx.getString("userPhone");
		Map userMap = new HashMap();
		userMap.put("userId", userPhone);
		User user = (User) this.getSqlMap().queryForObject("pp.core.qryUserInfo", userMap);
		if(user == null){
			log.error("手机号["+"]非注册用户手机号");
			throw new PeException("E00026");
		}
		int ss =(int)((Math.random()*9+1)*100000);
		String yzm = String.valueOf(ss);
		log.info(ss);
		try
	    {

	      int tmplId = 10260;

	      SmsSingleSender singleSender = new SmsSingleSender(msgAppId, msgAppKey);

	      ArrayList params = new ArrayList();
	      params.add(yzm);
	      SmsSingleSenderResult singleSenderResult = singleSender.sendWithParam("86", user.getUserPhone(), tmplId, params, "科蓝软件", "", "");
	      System.out.println(singleSenderResult);

	    } catch (Exception e) {
	      log.error("手机短信验证码发送失败",e);
	      throw new PeException("E00027");
	    }
		Map map = new HashMap();
		map.put("userPhone", user.getUserPhone());
		map.put("verifyCode", yzm);
		map.put("createTime",new Timestamp(ctx.getTimestamp().getTime()));
		getSqlMap().delete("pp.core.deleteVerifyCode", map);
		getSqlMap().insert("pp.core.insertVerifyCode", map);
		Map json = new HashMap();
		json.put("ReturnCode", Constants.AAAAAAA);
		json.put("ReturnMsg", "交易成功");
		ctx.setData("json", json);
		
	}

	public void setMsgAppId(int msgAppId) {
		this.msgAppId = msgAppId;
	}

	public void setMsgAppKey(String msgAppKey) {
		this.msgAppKey = msgAppKey;
	}
	
}
