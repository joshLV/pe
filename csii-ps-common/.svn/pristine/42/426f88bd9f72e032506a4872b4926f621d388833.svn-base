package com.csii.pe.extend;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.validation.convert.AbstractDateConverter;

/**
 * 
 * @auther  XL
 * @date    2015-10-27 上午11:35:04
 */
public class SqlDatetimestampConverterV1 extends AbstractDateConverter{

	private static Log log = LogFactory.getLog(SqlDatetimestampConverterV1.class);
	
	protected Object internalConvert(Object obj) throws Exception{
		if(format != null && format.length() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setLenient(false);
	        String s = obj.toString().trim();
	        try{
	        	obj =  sdf.parse(s);
	        }catch(Exception e){
	        	log.error("SimpleDateFormat parse error",e);
	        	throw e;
	        }
	    }
		if(obj instanceof Date)
	          return new Timestamp(((Date)obj).getTime());
	    else
	        return Timestamp.valueOf(obj.toString());
	}
	
	private String format;

	public void setFormat(String format) {
		this.format = format;
	}
}
