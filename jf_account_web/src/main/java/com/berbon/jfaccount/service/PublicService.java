package com.berbon.jfaccount.service;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.berbon.jfaccount.utils.Constant;
import com.berbon.util.date.DateUtil;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;

@Service
public class PublicService {

	Logger logger = LoggerFactory.getLogger(PublicService.class);
	@Autowired
	Constant constant;
//	MultipartResolver

	/**
	 * 上传文件
	 * 
	 * @param request
	 * @return
	 */
	public String uploadFile(HttpServletRequest request, String useCode,String filePath) {
		String path = null;
		try {

//			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			// 检查form是否有enctype="multipart/form-data"
//			if (multipartResolver.isMultipart(request)) {

//				MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
//
//				MultipartHttpServletRequest multiRequest = resolver.resolveMultipart(request);
			
	        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;  
				Iterator<String> iter = multiRequest.getFileNames();

				while (iter.hasNext()) {
					// 由CommonsMultipartFile继承而来,拥有上面的方法.
					MultipartFile file = multiRequest.getFile(iter.next());
					// 如果文件不为空，则进行处理
					if (!file.isEmpty()) {
						// 对图片文件名进行处理，取得最后的6个字符，然后以"."为分隔符取得文件后缀
						String originalFileName = file.getOriginalFilename();

						// 取得后缀
						String suffixString = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
						
						String dateTime = DateUtil.getDateStr(new Date(), "yyyyMMddHHmmss");
						// 设定文件名称
						String fileName = useCode + "_" + dateTime + "." + suffixString;

						path = filePath + fileName;

						File localFile = new File(path);
						if (!localFile.exists()) {
							localFile.mkdirs();
						}

						file.transferTo(localFile);
					}

				}

//			}

		} catch (Exception e) {

			logger.info("上传文件出错", e);

			return null;
		}
		return path;

	}
	
	
	/**
	 * 上传文件
	 * 
	 * @param request
	 * @return
	 */
	public String uploadFileByname(HttpServletRequest request, String fileName,String filePath) {
		String path = null;
		try {

//			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			// 检查form是否有enctype="multipart/form-data"
//			if (multipartResolver.isMultipart(request)) {

//				MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
//
//				MultipartHttpServletRequest multiRequest = resolver.resolveMultipart(request);
			
	        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;  
				Iterator<String> iter = multiRequest.getFileNames();

				while (iter.hasNext()) {
					// 由CommonsMultipartFile继承而来,拥有上面的方法.
					MultipartFile file = multiRequest.getFile(iter.next());
					// 如果文件不为空，则进行处理
					if (!file.isEmpty()) {
						// 对图片文件名进行处理，取得最后的6个字符，然后以"."为分隔符取得文件后缀
						String originalFileName = file.getOriginalFilename();

						// 取得后缀
						String suffixString = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
						
//						String dateTime = DateUtil.getDateStr(new Date(), "yyyyMMddHHmmss");
						// 设定文件名称
//						String fileName = useCode + "_" + dateTime + "." + suffixString;

						path = filePath + fileName+"."+suffixString;

						File localFile = new File(path);
						if (!localFile.exists()) {
							localFile.mkdirs();
						}

						file.transferTo(localFile);
					}

				}

//			}

		} catch (Exception e) {

			logger.info("上传文件出错", e);

			return null;
		}
		return path;

	}
}
