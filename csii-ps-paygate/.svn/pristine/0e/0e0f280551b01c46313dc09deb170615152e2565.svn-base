package com.csii.weixin.mgmt.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;

public class APPDownLoadAction extends AbstractAction{
	
    private String url;
	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		String appVersion = ctx.getString("appVersion");
		String versionId  = ctx.getString("versionId");
		Map dataMap = new HashMap();
		dataMap.put("appVersion", appVersion);
		Map appMap = (Map) this.getSqlMap().queryForObject("pp.core.qryAppInfo", dataMap);
		Map json = new HashMap();
		if(appMap != null){
			String flag = "";// 0:无需更新;1:可以更新;2:必须更新;
			if(!versionId.equals(appMap.get("versionId"))){
				flag ="1";
				if("2".equals(appMap.get("enforceUpdate"))){
					flag="2";
				}
				String downUrl = url +"?txnType=andriod"+"&appVersion="+appVersion;
				if("IOS".equals(appVersion)){
					downUrl="itms-apps://itunes.apple.com/cn/app/miaoshoubao/id1212695316?mt=8";
				}else if("IOS-bh".equals(appVersion)){
					downUrl="https://itunes.apple.com/cn/app/%E5%A5%BDe%E9%80%9A/id1250358614?mt=8";
				}
				
				json.put("downUrl", downUrl);
			}else{
				flag="0";
			}
		json.put("flag", flag);
		json.put("ReturnCode", Constants.AAAAAAA);
		json.put("ReturnMsg", "交易成功");
		ctx.setData("json", json);
		}else{
			throw new PeException("E00023");
		}
		
	}
	public void setUrl(String url) {
		this.url = url;
	}

	
}
