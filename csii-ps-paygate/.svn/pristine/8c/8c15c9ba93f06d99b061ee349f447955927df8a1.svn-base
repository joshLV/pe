package com.csii.weixin.mgmt.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.AbstractAction;
import com.csii.ibs.workflow.xml.ThrowException;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.id.IdFactory;
import com.csii.pp.model.User;
import com.csii.pp.util.BCrypt;
import com.qcloud.sms.SmsSingleSender;
import com.qcloud.sms.SmsSingleSenderResult;

public class CreateUserAction extends AbstractAction {

	private static Logger logger = LoggerFactory.getLogger(CreateUserAction.class);
	
	private IdFactory seqNoGen;
	
	private int msgAppId;
	private String msgAppKey;


	@Override
	public void execute(Context ctx) throws PeException {
		String userId = (String) ctx.getData("userId");
//		String password = (String) ctx.getData("password");
		int ss =(int)((Math.random()*9+1)*10000000);
		String yzm = String.valueOf(ss);
		String password = String.valueOf(ss);
		String userName = (String) ctx.getData("userName");
		String userLevel = (String) ctx.getData("userLevel");
		String merId = (String) ctx.getData("merId");
		String createUserId = (String) ctx.getData("createUserId");
		String userPhone = ctx.getString("userPhone");
		String refundAuth = ctx.getString("refundAuthority");
		String seqAuth = ctx.getString("seqAuthority");
		String statisticAuth = ctx.getString("statisticsAuthority");
		String cashiermanage = ctx.getString("cashierManage");

		Map map = new HashMap();
		map.put("userId", userId);
//		map.put("merId", merId);
		User userFromDB = (User) this.getSqlMap().queryForObject("pp.core.qryUserInfo", map);
		if (userFromDB != null) {
			logger.error("用户[USER_ID:{}]已经存在", userId);
			throw new PeException("E00016");
		}

		String userCode = (String)seqNoGen.generate();
		User user = new User();
		user.setUserCode(userCode);
		user.setUserId(userId);
		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		user.setUserName(userName);
		user.setUserLevel(userLevel);
		user.setMerId(merId);
		user.setCreateUserId(createUserId);
		user.setCreateDateTime(new Date());
		user.setUserStatus("0");
		user.setUserPhone(userPhone);
		user.setRefundAuth(refundAuth);
		user.setSeqAuth(seqAuth);
		user.setStatisticAuth(statisticAuth);
		user.setCashiermanage(cashiermanage);
		
		this.getSqlMap().insert("pp.core.insertUserInfo", user);

		Map result = new HashMap();
		result.put("ReturnCode", "000000");
		result.put("ReturnMsg", "交易成功");
		result.put("password", password);
		ctx.setData("json", result);
		
		try
	    {
		
          //根据商户号查询短信模板id
	      Map param = new HashMap();
	      param.put("merId", merId);
	      Map smsMap = (Map) this.getSqlMap().queryForObject("pp.core.querySmsTpl", param);
	      int tmplId = 0;
	      String sign ="";
	      if(smsMap!=null && !smsMap.isEmpty()){
	    	 tmplId =  Integer.parseInt((String) smsMap.get("smsTplid"));
	    	 sign = (String) smsMap.get("smsSign");
	      }else{
	    	  logger.error("商户[MER_ID:{}]未配置短信模板", merId);
	    	  throw new PeException("E00043");
	      }
	      SmsSingleSender singleSender = new SmsSingleSender(msgAppId, msgAppKey);

	      ArrayList params = new ArrayList();
	      params.add(userId);
	      params.add(password);
	      SmsSingleSenderResult singleSenderResult = singleSender.sendWithParam("86", userPhone, tmplId, params, sign, "", "");
	      System.out.println(singleSenderResult);

	    } catch (Exception e) {
	      log.error("手机短信验证码发送失败",e);
	    }
	}

	public void setSeqNoGen(IdFactory seqNoGen) {
		this.seqNoGen = seqNoGen;
	}

	public void setMsgAppId(int msgAppId) {
		this.msgAppId = msgAppId;
	}

	public void setMsgAppKey(String msgAppKey) {
		this.msgAppKey = msgAppKey;
	}
	
	
	
}
