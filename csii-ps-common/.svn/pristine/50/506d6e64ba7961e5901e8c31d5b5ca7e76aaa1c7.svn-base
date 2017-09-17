package com.csii.pp.core;

import org.apache.commons.dbcp.BasicDataSource;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import com.csii.pp.util.DESHelper;

/**
 * SecretDataSource.java
 *
 * @author X
 * <p>
 *   Created on 2010-4-15
 *   Modification history
 * </p>
 * <p>
 *   IBS Product Expert Group, CSII
 *   Powered by CSII PowerEngine 6.0
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class SecretDataSource extends BasicDataSource {
	
//	private static Log log = LogFactory.getLog(SecretDataSource.class);

	public synchronized void setPassword(String password)
    {
		super.setPassword(password);
		String plainPwd = null;
		try{
			javax.crypto.SecretKey deskey = DESHelper.genDESKey("DESede");
			plainPwd = DESHelper.desDecrypt(deskey, password);
		}catch(Exception e){
			// e.printStackTrace();
		}
        this.password = plainPwd;
    }
}
