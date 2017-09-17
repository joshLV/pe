package com.csii.weixin.mgmt.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.util.MiscUtil;

public class ANDRIODAction extends AbstractAction{

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		Map dataMap = new HashMap();
		dataMap.put("appVersion", MiscUtil.toStringAndTrim(ctx.getString("appVersion")));
		Map appMap = (Map) this.getSqlMap().queryForObject("pp.core.qryAppInfo", dataMap);
		String filePath = (String) appMap.get("filePath");
		String fileName = (String) appMap.get("fileName");
		String versionId  = (String) appMap.get("versionId");
		InputStream instm = null;
		File file = null;
		byte[] data = null;
		try {
			file =new File(filePath+versionId+"/"+fileName);
			
			if(!file.exists()){
				throw new PeException("E00022");
			}
			instm = new FileInputStream(file) ;
			data  =  IOUtils.toByteArray(instm);
		} catch (Exception ex) {
			log.error("读取文件异常", ex);
    		throw new PeException("E00022");
		} finally {
			if (instm != null) {
				try {
					instm.close();
				} catch (IOException ex) {
					log.error("关闭文件流异常", ex);
				}
			}
		}
		
		ctx.setData("DownloadFileContent", data);// format后字节数组
		ctx.setData("DownloadFileName", fileName);// 文件名不加路径
	}

}
