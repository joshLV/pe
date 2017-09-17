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

public class DeleteUserAction extends AbstractAction {
	
	private static Logger logger = LoggerFactory.getLogger(DeleteUserAction.class);

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		
		//如果是管理员，userid为商户号。管理员不能删除
		String userId = ctx.getString("userId");
		String merId = ctx.getString("merId");
		Map merPara = new HashMap();
		merPara.put("mer_id",userId);
		Merchant merchant = (Merchant)this.getSqlMap().queryForObject("pp.core.qryMerId", merPara);
		if(null!=merchant){
			logger.error("用户[merId:{}]为管理员", merId);
			throw new PeException("E00047");
		}
		Map map = new HashMap();
		map.put("userId", userId);
		map.put("merId", merId);
		
		User userFromDB = (User) this.getSqlMap().queryForObject("pp.core.qryUserInfo", map);
		if (userFromDB == null) {
			logger.error("用户[USER_ID:{}]不存在", userId);
			throw new PeException("E00014");
		}
		
		int flag = this.getSqlMap().delete("pp.core.deleteUserInfo", map);
		
		Map result = new HashMap();
		result.put("ReturnCode", "000000");
		result.put("ReturnMsg", "交易成功");

		ctx.setData("json", result);
		
	}

}
