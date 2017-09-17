/*
 * @(#)SFTPServer.java	2013-6-30
 *
 * Copyright 2004-2012 Client Service International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pp.sftp;

import com.csii.pe.core.PeException;
import com.jcraft.jsch.ChannelSftp;

/**
 *
 * @author liuqingkuo
 * <p>
 *   Created on 2013-6-30
 *   Modification history
 * </p>
 * <p>
 *   EPP Product Expert Group, CSII
 *   Powered by CSII PowerEngine
 * </p>
 * @version 1.0
 * @since 1.0
 */
public interface FileTransfer {
	public void connect() throws PeException;
	public void disconnect();
	public void upload(String localPath,String sourceFileName);
	public void upload(String localPath,String sourceFileName,String uploadPath) throws PeException ;
	
	public void upload(String localPath,String sourceFileName,String uploadPath,String targetFileName);
	
}
