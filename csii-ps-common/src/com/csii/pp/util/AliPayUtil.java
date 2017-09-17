package com.csii.pp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;

public class AliPayUtil {
	public static boolean signCheck(Map paraMap,String sign,String publicKey) throws AlipayApiException, IOException {
		String signContent = AlipaySignature.getSignContent(paraMap);
		boolean rsaCheckContent = AlipaySignature.rsaCheck(signContent, sign,publicKey, "UTF-8", "RSA2");
		return rsaCheckContent;
	}
	
	public static Map firstLetterToUpper(Map map){
		Iterator entries = map.entrySet().iterator();  
		Map newMap = new HashMap();
		while (entries.hasNext()) {  
		    Map.Entry entry = (Map.Entry) entries.next();  
		    String key = entry.getKey().toString();  
		    key = String.valueOf(key.charAt(0)).toLowerCase().concat(key.substring(1));
		    newMap.put(key, entry.getValue());
		}  
		return newMap;
	}
	
	
	public static void main(String[] args) {/*
//		String para="body=大乐透2.1&buyer_id=2088102116773037&charset=utf-8&gmt_close=2016-07-19 14:10:46&gmt_payment=2016-07-19 14:10:47&notify_time=2016-07-19 14:10:49&notify_type=trade_status_sync&out_trade_no=0719141034-6418&refund_fee=0.00&subject=大乐透2.1&total_amount=2.00&trade_no=2016071921001003030200089909&trade_status=TRADE_SUCCESS&version=1.0";
//		String para="Body=大乐透2.1&Buyer_id=2088102116773037&Charset=utf-8&Gmt_close=2016-07-19 14:10:46&Gmt_payment=2016-07-19 14:10:47&Notify_time=2016-07-19 14:10:49&Notify_type=trade_status_sync&Out_trade_no=TYZF01652203&Refund_fee=0.00&Subject=大乐透2.1&Total_amount=2.00&Trade_no=2016071921001003030200089909&Trade_status=TRADE_SUCCESS&Version=1.0";
		String para="App_id=2016080600180885&Buyer_id=2088102116773037&Gmt_close=2016-07-19 14:10:46&Out_trade_no=TYZF01652203&Total_amount=2.00&Trade_no=2016071921001003030200089909&Trade_status=TRADE_SUCCESS";
//		             App_id=2016080600180885&Buyer_id=2088102116773037&Out_trade_no=TYZF01652203&Total_amount=2.00&Trade_no=2016071921001003030200089909&Trade_status=TRADE_SUCCESS
		
//		Map map = new HashMap();
		
//		Map firstLetterToUpper = firstLetterToUpper(map);
		String signContent = AlipaySignature.getSignContent(firstLetterToUpper);
		System.out.println(signContent);
		String resSign = "ly0Yydro45HLcAD8vCSjr49NYcBsDqyQyrEzO0yh7xuXGmCIuRIoHJhONpnNX+eVpeLdrHpxWWytsmCgeiYr9uhe6LLDXBXwnpimpGQpfMlHN5pLLp1/9CGtkZEQddQCaMCk7UUXlP5CztMEUNhB+W3opyKzKUwKsfNzDAX1ZCUIk5FKKNN0EQI1PnzECrKp4tIjLqM3Vypmcja0H5IEb8EYJ3A+tsi2Kc4EL+sRXsbt5e5YNukTU+/MCIerfsOjk0kgtbUYa+FzKYrOCTpwHCOFm6KB/v36gH5lItJ7FXKeihVZUB9AsseXPquxLMMSYQt5p6jcagWIAwju/xZCyg==";
		try {
//			resSign = AlipaySignature.rsaSign(para, AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.CHARSET,AlipayConfig.SIGNTYPE);
//			System.out.println(resSign);
//			resSign = AlipaySignature.rsaSign(map, AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.CHARSET);
//			System.out.println(resSign);
			boolean rsaCheck = AlipaySignature.rsaCheck(signContent, resSign,AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGNTYPE);
			
//			AlipaySignature.rsa
			
			System.out.println(rsaCheck);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	*/}
	
	

}
