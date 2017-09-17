package com.csii.pe.extend;

import java.sql.Date;
import java.sql.Timestamp;

import com.csii.pe.transform.stream.xml.ElementResolver;
import com.csii.pe.util.config.ContentAware;

/**
 * 
 * @auther  XL
 * @date    2015-7-4 下午4:28:30
 */
public class DateElementResolver implements ElementResolver, ContentAware{

	public DateElementResolver(){
		
	}

	@Override
	public Object resolve() {
		// TODO Auto-generated method stub
		if(content != null)
        {
            int i = content.trim().length();
            if(i == 8 || i == 10)
                return Date.valueOf(content);
            if(i == 14 || i >= 19)
                return Timestamp.valueOf(content);
            else
                return content.trim();
        } else
        {
            return "";
        }
	}

	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return content;
	}

	@Override
	public void setContent(String content) {
		// TODO Auto-generated method stub
		this.content = content;
	}
	
	private String content;
}
