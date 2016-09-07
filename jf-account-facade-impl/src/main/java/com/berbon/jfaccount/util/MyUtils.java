package com.berbon.jfaccount.util;

import com.berbon.util.MD5.MD5Util;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
