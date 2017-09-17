package com.csii.pp.sftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.csii.pe.core.PeException;
import com.csii.pp.sftp.FileTransfer;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

  
public class SFTPFileTransfer implements FileTransfer{  
      
    private String host;  
    private String username;  
    private String password;  
    private int port;  
    private ChannelSftp sftp = null;  
    private String localPath;  
    
    private String remotePath = "/";  
    private String fileListPath = "";  
    private final String seperator = "";  
    private Logger logger =  LoggerFactory.getLogger(SFTPFileTransfer.class);  
    /**
	 * 服务器路径
	 */
	private String serverPath;
    /** 
     * connect server via sftp 
     * @throws PeException 
     * @throws Exception 
     */  
    public void connect() throws PeException {  
        try {  
            if(sftp != null){  
                System.out.println("sftp is not null");  
            }  
            JSch jsch = new JSch();  
            jsch.getSession(username, host, port);
            Session sshSession = jsch.getSession(username, host, port);  
            System.out.println("Session created.");  
            sshSession.setPassword(password);  
            Properties sshConfig = new Properties();  
            sshConfig.put("StrictHostKeyChecking", "no");  
            sshSession.setConfig(sshConfig);  
            sshSession.connect();  
            System.out.println("Session connected.");  
            System.out.println("Opening Channel.");  
            Channel channel = sshSession.openChannel("sftp");  
            channel.connect();  
            sftp = (ChannelSftp) channel;  
            System.out.println("Connected to " + host + ".");  
        } catch (Exception e) {
        	e.printStackTrace();  
        	logger.info("登陆异常："+e);
        	throw new PeException();
           
        }  
    }  
    /** 
     * Disconnect with server 
     */  
    public void disconnect() {  
        if(this.sftp != null){  
            if(this.sftp.isConnected()){  
                this.sftp.disconnect(); 
                try {
					if (sftp != null && (sftp.getSession() != null)) {
						sftp.getSession().disconnect();
					}
				} catch (JSchException e) {
					logger.error("session close exception", e);
				}
            }else if(this.sftp.isClosed()){  
                System.out.println("sftp is closed already");  
            }  
        }  
  
    }  
  
    public void download() {  
        // TODO Auto-generated method stub  
          
  
    }  
      
    /** 
    private void download(String directory, String downloadFile,String saveFile, ChannelSftp sftp) {  
        try {  
            sftp.cd(directory);  
            File file = new File(saveFile);  
            sftp.get(downloadFile, new FileOutputStream(file));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
      */
    /** 
     * upload all the files to the server 
     */  
    public void upload() {  
        List<String> fileList = this.getFileEntryList(fileListPath);  
        try {  
            if(fileList != null){  
                for (String filepath : fileList) {  
                    String localFile = this.localPath + this.getSeperator()+ filepath;  
                    File file = new File(localFile);  
                      
                    if(file.isFile()){  
                        System.out.println("localFile : " + file.getAbsolutePath());  
                        String remoteFile = this.remotePath + this.getSeperator() + filepath;  
                        System.out.println("remotePath:" + remoteFile);  
                        File rfile = new File(remoteFile);  
                        String rpath = rfile.getParent();  
                        try {  
                            createDir(rpath, sftp);  
                        } catch (Exception e) {  
                            System.out.println("*******create path failed" + rpath);  
                        }  
  
                        this.sftp.put(new FileInputStream(file), file.getName());  
                        System.out.println("=========upload down for " + localFile);  
                    }  
                }  
            }  
        } catch (FileNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (SftpException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
          
    }  
    /** 
     * create Directory 
     * @param filepath 
     * @param sftp 
     */  
    private void createDir(String filepath, ChannelSftp sftp){  
        boolean bcreated = false;  
        boolean bparent = false;  
        File file = new File(filepath);  
        String ppath = file.getParent();  
        try {  
            this.sftp.cd(ppath);  
            bparent = true;  
        } catch (SftpException e1) {  
            bparent = false;  
        }  
        try {  
            if(bparent){  
                try {  
                    this.sftp.cd(filepath);  
                    bcreated = true;  
                } catch (Exception e) {  
                    bcreated = false;  
                }  
                if(!bcreated){  
                    this.sftp.mkdir(filepath);  
                    bcreated = true;  
                }  
                return;  
            }else{  
                createDir(ppath,sftp);  
                this.sftp.cd(ppath);  
                this.sftp.mkdir(filepath);  
            }  
        } catch (SftpException e) {  
            System.out.println("mkdir failed :" + filepath);  
            e.printStackTrace();  
        }  
          
        try {  
            this.sftp.cd(filepath);  
        } catch (SftpException e) {  
            e.printStackTrace();  
            System.out.println("can not cd into :" + filepath);  
        }  
          
    }  
    /** 
     * get all the files need to be upload or download 
     * @param file 
     * @return 
     */  
    private List<String> getFileEntryList(String file){  
        ArrayList<String> fileList = new ArrayList<String>();  
        InputStream in = null;  
        try {  
              
            in = new FileInputStream(file);  
            InputStreamReader inreader = new InputStreamReader(in);  
              
            LineNumberReader linreader = new LineNumberReader(inreader);  
            String filepath = linreader.readLine();  
            while(filepath != null){  
                fileList.add(filepath);  
                filepath = linreader.readLine();  
            }  
            in.close();  
        } catch (FileNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }finally{  
            if(in != null){  
                in = null;  
            }  
        }  
  
        return fileList;  
    }  
  
    
  
    public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	/** 
     * @return the sftp 
     */  
    public ChannelSftp getSftp() {  
        return sftp;  
    }  
  
    /** 
     * @param sftp the sftp to set 
     */  
    public void setSftp(ChannelSftp sftp) {  
        this.sftp = sftp;  
    }  
  
//    /** 
//     * @return the localPath 
//     */  
//    public String getLocalPath() {  
//        return localPath;  
//    }  
//  
//    /** 
//     * @param localPath the localPath to set 
//     */  
//    public void setLocalPath(String localPath) {  
//        this.localPath = localPath;  
//    }  
  
    /** 
     * @return the remotePath 
     */  
    public String getRemotePath() {  
        return remotePath;  
    }  
  
    /** 
     * @param remotePath the remotePath to set 
     */  
    public void setRemotePath(String remotePath) {  
        this.remotePath = remotePath;  
    }  
  
    /** 
     * @return the fileListPath 
     */  
    public String getFileListPath() {  
        return fileListPath;  
    }  
  
    /** 
     * @param fileListPath the fileListPath to set 
     */  
    public void setFileListPath(String fileListPath) {  
        this.fileListPath = fileListPath;  
    }  
      
    public static void main(String[] args) throws PeException {  
        // TODO Auto-generated method stub  
        SFTPFileTransfer ftp= new SFTPFileTransfer();  
        ftp.connect();  
        ftp.upload("D:/app/", "600090_20150605_FI0006.xml", "/home/epay/file/hexun/20150605/"); 
        ftp.disconnect();  
        System.exit(0);  
    }
	/* (non-Javadoc)
	 * @see com.csii.wuhan.sftp.FileTransfer#upload(java.lang.String, java.lang.String)
	 */
	public void upload(String localPath,String sourceFileName) {
		 FileInputStream fis=null;
        try {  
                    String localFile = localPath + this.getSeperator()+ sourceFileName;  
                    System.out.println("localFile : " + localFile); 
                    File file = new File(localFile);  
                    fis=new FileInputStream(file);
                    System.out.println("file:"+file);
                    System.out.println("file1:"+fis);
                    if(file.isFile()){  
                        System.out.println("file.absolutePath : " + file.getAbsolutePath()); 
                       // String remoteFile = remotePath + this.getSeperator() + targetFileName;  
                      ///  System.out.println("remotePath:" + remoteFile);  
                       // File rfile = new File(remoteFile);  
                        //String rpath = rfile.getParent();  
                       // try {  
                       //     createDir(rpath, sftp);  
                       // } catch (Exception e) {  
                        //    System.out.println("*******create path failed" + rpath);  
                       // }  
  
                        this.sftp.put(fis, localFile);  
                        System.out.println("=========upload down for " + localFile);  
                    }
        } catch (FileNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (SftpException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } finally{
        	if(fis!=null){
        		try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
          
    
		
	}
	
	
	/*localPath  本地路径
	 * sourceFileName 文件名
	 * uploadpath 上传路径
	 */
	public void upload(String localPath,String sourceFileName,String uploadpath,String targetFileName) {
		 logger.info("upload  execute");
		 FileInputStream fis=null;
        try {  
                    String localFile = localPath + sourceFileName;  
                   
                    File file = new File(localFile);  
                    
                    String uploadFile = uploadpath +targetFileName;
                    logger.info("uploadFile path+fileName: " + uploadFile); 
                    if(file.isFile()){  
                    	logger.info("file.absolutePath : " + file.getAbsolutePath()); 
                    	fis=new FileInputStream(file);
                        this.sftp.put(fis, uploadFile);  
                        logger.info("=========upload  " + localFile+"    success");  
                    }  
        } catch (FileNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (SftpException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } finally{
        	if(fis!=null){
        		try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
          
    
		
	}
	
	
	/*localPath  本地路径
	 * sourceFileName 文件名(本地和目标文件名)
	 * uploadpath 上传路径
	 */
	public void upload(String localPath,String sourceFileName,String uploadpath) throws PeException {
		logger.info("upload  execute");
		FileInputStream fis=null;
		try {  
			
			 String localFile = localPath +sourceFileName;
			 logger.info("localFile"+localFile);
			 logger.info("uploadpath"+uploadpath);
			 logger.info("sourceFileName"+sourceFileName);
             File file = new File(localFile);
             try{
            	 this.sftp.ls(uploadpath);
             }catch(Exception e){
            	 logger.info("上传异常"+e.getMessage());
            	 this.sftp.mkdir(uploadpath);
             }
             fis=new FileInputStream(file);
             this.sftp.put(fis, uploadpath+file.getName()); 
             logger.info("=========upload  " + localFile+"   success");   
                    
        } catch (Exception e) {
        	logger.error("文件名称【"+sourceFileName+"】上传失败"+e.getMessage());
        	throw new PeException("OF0005");
        }finally{
        	if(fis!=null){
        		try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
	}
	
	public void download(String localPath,String sourceFileName,String downloadpath) throws PeException{
		logger.info("download  execute");
		File file= null;
		FileOutputStream fos=null;
		try {  
		String downloadFile = downloadpath +sourceFileName;
		String localFile = localPath + sourceFileName;
		file = new File(localFile);
		if (file.getParentFile()!=null && !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
			fos=new FileOutputStream(file);
            this.sftp.get(downloadFile, fos); 
            logger.info("=========download  " + downloadFile+"   success");  
		} catch (Exception e) {  
			file.delete();
			logger.error("文件名称【"+sourceFileName+"】下载失败");
        	throw new PeException("OF0004");
        }finally{
        	if(fos!=null){
        		try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	}
        }
		
	}
	
	public boolean download(String sourceFileName, String targetFileName) {
		// TODO Auto-generated method stub
		ChannelSftp sftp=null;
		Session session=null;
		try {
			JSch jsch=new JSch();
			session=jsch.getSession(username, host, port);
			session.setPassword(password);
			Properties properties=new Properties();
			properties.put("StrictHostKeyChecking", "no");
			session.setConfig(properties);
			
//			//使用代理
//			if(userProxy){
//				//如果是代理服务器采用的HTTP方式代理，可以通过
//				ProxyHTTP proxyhttp = new  ProxyHTTP(proxyHost,proxyPort);
//                session.setProxy(proxyhttp);
//			}
			
			session.connect();
			Channel channel=session.openChannel("sftp");
			channel.connect();
			sftp=(ChannelSftp)channel;
			
			sftp.cd(serverPath);
			sftp.get(sourceFileName, targetFileName);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("sftp download file error",e);
			return false;
		}finally{
			if(sftp!=null){
				sftp.disconnect();
			}
			if(session!=null){
				session.disconnect();
			}
		}
	}
	
	/**
	 * @return the seperator
	 */
	public String getSeperator() {
		return seperator;
	}
	public String getServerPath() {
		return serverPath;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}
}  
