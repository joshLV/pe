package com.csii.pp.command;

import java.util.Map;

import com.csii.pe.chain.Command;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.signature.MerchantXmlSignature;
import com.csii.pp.util.MiscUtil;

public class AlipayNotifyVerifySignCommand  implements Command{
	
	private String key;

	private MerchantXmlSignature merchantXmlSignature;
	@Override
	public boolean execute(Context paramContext, Map paramMap) throws PeException {
		
		String orgSign = paramContext.getString("sign");
		System.out.println("sign string org is =========="+orgSign);
		System.out.println("key is ========"+key);
		String keyid = (String)paramContext.getString("key_id");
		String signStr ="";
		if(MiscUtil.isNullOrEmpty(keyid)){
			//app
			signStr = MiscUtil.sign(paramContext.getDataMap(), key);
		}else{
			//其他
			Map keyMap = merchantXmlSignature.getKeyId(keyid);
			if (keyMap.isEmpty()){
				//无此keyid信息
				throw new PeException("000002");
			}
			String keyContent = (String)keyMap.get("keyContent");
			signStr = MiscUtil.sign(paramContext.getDataMap(), keyContent);
		}
		System.out.println("signStr is ======="+signStr);
		boolean flag = true;
		if(MiscUtil.trimAndEquals(orgSign, signStr)){
			flag =false;
		}else{
			throw new PeException("000002");
		}
		return flag;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setMerchantXmlSignature(MerchantXmlSignature merchantXmlSignature) {
		this.merchantXmlSignature = merchantXmlSignature;
	}
}
