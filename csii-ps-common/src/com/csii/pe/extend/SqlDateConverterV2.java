package com.csii.pe.extend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.csii.pe.core.PeException;
import com.csii.pe.validation.convert.AbstractDateConverter;

/**
 * 
 */
public final class SqlDateConverterV2 extends AbstractDateConverter{

	protected Object internalConvert(Object obj) throws Exception{
		if(format != null){
	        String s = obj.toString().trim();
	        obj =  format.parse(s);
	        if(isErrorLastChar(s)){
	        	throw new PeException("最后一位格式不正确");
	        }
	    }else{
	    }
		
		
		
	    if(obj instanceof Date)
            return new java.sql.Date(((Date)obj).getTime());
        else
            return java.sql.Date.valueOf(obj.toString());
	}
	
	private DateFormat format;
	
	public void setFormat(String str){
		format = new SimpleDateFormat(str);
		format.setLenient(false);
	}
	
	/**
	 * 验证日期字符串，错误返回true
	 * @param dateStr
	 * @return
	 */
	private static boolean isErrorLastChar(String dateStr) {
		String lastStr = dateStr.substring(dateStr.length()-1, dateStr.length());
		try {
			int lastInt = Integer.parseInt(lastStr);
			if(lastInt < 0 || lastInt > 9){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return true;
		}
	}
}
