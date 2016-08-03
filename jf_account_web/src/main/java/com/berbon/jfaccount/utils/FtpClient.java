package com.berbon.jfaccount.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;

/**
 * @author 
 * @since 2015/7/9
 */
public class FtpClient {

	private static Logger logger = LoggerFactory.getLogger(FTPClient.class);

	private String host;
	private int port;
	private String username;
	private String password;
	private FTPClient ftp = new FTPClient();

	/**
	 * @param host
	 *            ip地址
	 * @param port
	 *            端口号
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 */
	public FtpClient(String host, int port, String username, String password) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	private boolean connectFtp() {
		boolean flag = false;
		int reply;
		try {
			long startLogin = System.currentTimeMillis();
			ftp.connect(host, port);// 连接ftp服务器
			logger.info("连接：" + host + ":" + port);
			ftp.login(username, password);// 登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				logger.info("登陆失败");
				return false;
			}
			flag = true;
			long endLogin = System.currentTimeMillis();
			logger.info("登录成功，登录耗时:"+(endLogin-startLogin)+"ms");
			ftp.enterLocalPassiveMode();
			logger.info("开启被动模式");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("连接超时，请检查地址和端口");
		}
		return flag;
	}

	private boolean logOut() {
		long logOutTime = System.currentTimeMillis();
		boolean flag = false;
		try {
			if (ftp != null)
				ftp.logout();
				ftp.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endLogOutTime = System.currentTimeMillis();
		logger.info("登出耗时:"+(endLogOutTime-logOutTime)+"ms");
		return flag;
	}

	/**
	 * @param path
	 *            ftp服务器的文件路径
	 * @param filename
	 *            保存文件名
	 * @param inputStream
	 *            文件流
	 * @return
	 */
	public boolean uploadFile(String path, String filename,
			InputStream inputStream) {
		boolean flag = false;
		if (connectFtp()) {
			try {
				logger.info("Remote system is " + ftp.getSystemType());
				if (!ftp.changeWorkingDirectory(path)) {
					if (!ftp.makeDirectory(path)) {
						logger.info("创建目录【" + path + "】失败");
						logger.info("上传失败");
						return false;
					} else {
						ftp.changeWorkingDirectory(path);
						logger.info("当前目录是 " + ftp.printWorkingDirectory());
					}
				}
				ftp.enterLocalPassiveMode();
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				flag = ftp.storeFile(filename, inputStream);
				if (flag == true) {
					logger.info("上传成功");
				} else {
					logger.info("上传失败");
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logOut();
			}
		}
		return flag;
	}

	/**
	 * @param path
	 *            ftp服务器的文件路径
	 * @param filename
	 *            文件名
	 * @param sourcePath
	 *            要上传的文件路径
	 * @return
	 */
	public boolean uploadFile(String path, String filename, String sourcePath) {
		boolean flag = false;
		File file = new File(sourcePath);
		if (file.exists() && file.isFile()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				flag = uploadFile(path, filename, fis);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			logger.error("文件不存在或者该文件是文件夹");
		}
		return flag;
	}

	public boolean downloadFile(String downloadPath,
			String remotePath, String[] remoteFilenames) {
		// TODO Auto-generated method stub
		boolean downloadFlag = false;
		if (connectFtp()) {
			long startDownloadTime = System.currentTimeMillis();
			try {
				if (ftp.changeWorkingDirectory(remotePath)) {
					logger.info("当前目录:"+ftp.printWorkingDirectory());
					/*long startListFiles = System.currentTimeMillis();
					FTPFile[] ftpFiles = ftp.listFiles();
					long endListFiles = System.currentTimeMillis();
					logger.info("列出ftp服务器文件耗时:"+(endListFiles-startListFiles)+"ms");*/
					long singleDownTime = 0;
					long writeFileTime = 0;
					long startMatchTime = System.currentTimeMillis();
					//for (FTPFile ff : ftpFiles) {
						for(String remoteFilename:remoteFilenames){
						//	if (ff.getName().equals(remoteFilename)) {
								long startDownloadFile = System.currentTimeMillis();
								InputStream inputStream = ftp
										.retrieveFileStream(remoteFilename);
								long endDownloadFile = System.currentTimeMillis();
								logger.info("下载文件"+remoteFilename+"耗时:"+(endDownloadFile-startDownloadFile)+"ms");
								singleDownTime = endDownloadFile-startDownloadFile;
								if (inputStream == null) {
									logger.info("无法下载文件:" + remoteFilename);
									continue;
								} else {
									logger.info("下载的文件是:" + remoteFilename);
								}
								long startWriteFileTolocal = System.currentTimeMillis();
								OutputStream output = new FileOutputStream(
										new File(downloadPath + remoteFilename));
								byte[] bytes = new byte[1024];
								int i = -1;
								try {
									while ((i = inputStream.read(bytes)) != -1) {
										output.write(bytes, 0, i);;
									}
									output.flush();
									downloadFlag = true;
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} finally {
									inputStream.close();
									ftp.completePendingCommand();
									output.close();
								}
								long endWriteFileTolocal = System.currentTimeMillis();
								writeFileTime+=(endWriteFileTolocal-startWriteFileTolocal);
							}
					//	}
				//	}
					long endMatchTime=System.currentTimeMillis();
					logger.info("文件匹配耗时:"+(endMatchTime-startMatchTime-singleDownTime-writeFileTime)+"ms");
					logger.info("写入文件耗时:"+writeFileTime+"ms");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("切换工作目录报错!");
			}
			long endDownloadTime = System.currentTimeMillis();
			logger.info("下载所有文件加匹配耗时:"+(endDownloadTime-startDownloadTime)+"ms");
			logOut();
		}
		return downloadFlag;
	}
}
