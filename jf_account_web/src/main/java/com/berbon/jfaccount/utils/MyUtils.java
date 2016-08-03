package com.berbon.jfaccount.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;




import com.berbon.ordersystem.pojo.BerbonOrder;
import com.berbon.ordersystem.pojo.CommonConstant;
import com.berbon.ordersystem.pojo.OrderStateMapping;
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
	
	/**
	 * 处理订单列表查询返回状态
	 * @param berbonOrder
	 * @return
	 */
	
	public static Integer handleFuelState(BerbonOrder berbonOrder){
		if (berbonOrder.getRefundState().equals(CommonConstant.refund_success)) {
			return OrderState.REFUND_SUCCESS;
		}
		if (berbonOrder.getRefundState().equals(CommonConstant.refund_handling)) {
			return OrderState.REFUNDING;
		}
		if (berbonOrder.getRefundState().equals(CommonConstant.refund_fail)) {
			return OrderState.REFUND_FAILD;
		}
		if (berbonOrder.getRefundState().equals(CommonConstant.refund_unknown)) {
			return OrderState.UNKONE_REFUND;
		}
		if (berbonOrder.getPayState().equals(CommonConstant.unpay)) {
			return OrderState.NOTPAY;
		}
		if (berbonOrder.getPayState().equals(CommonConstant.failPay)) {
			return OrderState.PAY_FAILD;
		}
		if (berbonOrder.getPayState().equals(CommonConstant.paying)) {
			return OrderState.PAYING;
		}
		if (berbonOrder.getPayState().equals(CommonConstant.unknownPay)) {
			return OrderState.UNKONE_PAY;
		}
		if (berbonOrder.getOrderState().equals(OrderStateMapping.order_success.getOrderState())) {
			return OrderState.CHARGE_SUCCESS;
		}
		if (berbonOrder.getOrderState().equals(OrderStateMapping.order_handling.getOrderState())) {
			return OrderState.CHARGEING;
		}
		if (berbonOrder.getOrderState().equals(OrderStateMapping.order_fail.getOrderState())) {
			return OrderState.CHARGE_FAILD;
		}
		if (berbonOrder.getOrderState().equals(OrderStateMapping.order_start.getOrderState())) {
			return OrderState.NOTPAY;
		}
		return OrderState.UNKONE_CHARGEING;
	}
	
	
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
	 

}
