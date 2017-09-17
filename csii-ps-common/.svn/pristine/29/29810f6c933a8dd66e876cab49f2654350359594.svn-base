package com.csii.pe.extend;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.validation.convert.AbstractNumberConverter;

/**
 * 
 * @auther  XL
 * @date    2015-10-27 上午11:23:30
 */
public class BigDecimalConverterV1 extends AbstractNumberConverter{

	private static Log log = LogFactory.getLog(BigDecimalConverterV1.class);
	
	protected Object internalConvert(Object obj)throws Exception{
        if(format != null && format.length() > 0){
        	DecimalFormat df = new DecimalFormat(format);
        	try{
        		 obj = df.parse(obj.toString());
        	}catch(Exception e){
        		log.error("DecimalFormat parse error",e);
        	}
        }
        
        return new BigDecimal(obj.toString());
    }

	private String format;

	public void setFormat(String format) {
		this.format = format;
	}
}
