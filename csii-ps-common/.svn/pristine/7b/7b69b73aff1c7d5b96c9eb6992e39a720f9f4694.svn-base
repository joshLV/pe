package com.csii.pp.util;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.core.PeException;
import com.csii.pe.core.PeRuntimeException;

/**
 * FileHelper.java
 * <p>
 * Created on 2008-4-7 
 * Modification history	
 * <p>
 * @author	lihui, Electronic Payment System Group, CSII
 * @version	1.0
 * @since	1.0
 */
public class FileHelper {
	
	private static Log log = LogFactory.getLog(FileHelper.class);

	/**
	 * 
	 */
	public FileHelper() {
		super();
	}
	
	
	public void createFile(String filePath, String fileName, byte content[]) throws PeException {
		FileOutputStream out = null;
		File file = null;
		try {
			file = new File(filePath + fileName);
			if (!file.exists()) {
				File dir = file.getParentFile();
				if (!dir.exists()) {
					dir.mkdirs();
				}
				file.createNewFile();
			}
			out = new FileOutputStream(file);
			out.write(content);
			out.flush();
		} catch (Exception ex) {
			log.error("Making file [" + file.getName() + "] Error.", ex);
			throw new PeRuntimeException("E00033", ex);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception ex) {
					log.fatal("Close FileOutputStream [" + file.getName() + "] Error.", ex);
				}
			}
		}
	}
	
}

