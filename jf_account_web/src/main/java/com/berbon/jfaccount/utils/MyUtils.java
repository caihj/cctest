package com.berbon.jfaccount.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;




import com.berbon.util.MD5.MD5Util;

public class MyUtils {
	
	/**
	 * 下单 计算sign
	 * @param data
	 * @param key
	 * @return
	 */
	public  static String getSign(String data ,String key){
		String strInput ="data="+data+"&"+"key="+key;
		return MD5Util.encrypt(strInput, "UTF-8").toUpperCase();
		
	}

	public static BigDecimal fen2yuan(long money) {
		BigDecimal bd = new BigDecimal(money+"");
		BigDecimal bd100 = new BigDecimal("100");
		return bd.divide(bd100, 2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 处理订单列表查询返回状态
	 * @param berbonOrder
	 * @return
	 */
	

	
	
	/**
	 * 组织返回json格式
	 * @param httpServletRequest
	 * @param data
	 * @return
	 */
	public static String  getJsonP(HttpServletRequest request,String data){
		String  callback = request.getParameter("callback");
		 return callback + "({'result':"+data+"})";  
	}
	
	
	   public static String getPostfix(String path) {
		           if (path == null || "".equals(path.trim())) {
		               return "";
		           }
		           if (path.contains(".")) {
		                return path.substring(path.lastIndexOf(".") + 1, path.length());
		           }
		            return "";
		       }
	   
	   
	   /**
	    * 验证手机号码
	    * @param mobiles
	    * @return
	    */
	 public static boolean isMobileNO(String mobiles){  
		 if (mobiles.length()==11&&mobiles.startsWith("1")) {
			 Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9])|17[0-9])\\d{8}$");
			 Matcher m = p.matcher(mobiles);  
			return m.matches();  
		}else if (mobiles.length()<13&&mobiles.startsWith("0")) {
			return isNumber(mobiles);
		}else {
			return false;
		}
		
       }
	 
	 /**
	    * 判断是否是数据子
	    * @param mobiles
	    * @return
	    */
	 public static boolean isNumber(String str){
		 Pattern pattern = Pattern.compile("[0-9]*");
		  return pattern.matcher(str).matches();
		 
	 }
	 /**
	  * 验证是否整除
	  * @param dividend
	  * @param divisor
	  * @return
	  */
	 
	 public static boolean isRightNum(int dividend,int divisor){
		 return dividend%divisor==0 ?true :false;
		 
	 }
	 
	  public static void CopySingleFile(String oldPathFile, String newPathFile,String newDir) {  
	        try {  
	            int bytesum = 0;  
	            int byteread = 0;  
	            File oldfile = new File(oldPathFile);  
	            
	            if (oldfile.exists()) { //文件存在时  
	            	 File newfile = new File(newDir); 
	            	 if (!newfile.exists()) {
	            		 newfile.mkdirs();
					}
	                InputStream inStream = new FileInputStream(oldPathFile); //读入原文件  
	                FileOutputStream fs = new FileOutputStream(newPathFile);  
	                byte[] buffer = new byte[1444];  
	                while ((byteread = inStream.read(buffer)) != -1) {  
	                    bytesum += byteread; //字节数 文件大小  
	                    //System.out.println(bytesum);  
	                    fs.write(buffer, 0, byteread);  
	                } 
//	                fs.flush();
	                inStream.close();  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  

	public static  String markName(String realName){

		if(realName!=null && realName.length()>0){
			if(realName.length()==2){
				realName = "*"+realName.substring(1,2);
			}else  if(realName.length()==3){
				realName = "*"+realName.substring(1,3);
			}else if(realName.length()==4){
				realName = "**"+realName.substring(2,4);
			}else{
				realName = "**"+realName.substring(2,realName.length());
			}
		}
		return realName;
	}

}
