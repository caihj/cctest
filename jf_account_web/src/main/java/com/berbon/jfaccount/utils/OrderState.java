package com.berbon.jfaccount.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * 页面订单状态列举
 * @author baijt
 *
 * 2015年7月28日
 */
public class OrderState {
	public static  int CHARGE_SUCCESS=10;    //充值成功
	public static  int CHARGEING=11;        //充值中
	public static  int NOTPAY=20;           //未支付
	public static  int PAYING=24;           //支付中
	public static  int PAY_SUCCESS=21;       //支付成功
	public static  int UNKONE_PAY=22;         //支付状态未知
	public static  int PAY_FAILD=23;         //支付失败
	public static  int CHARGE_FAILD=12;     //充值失败
	public static  int UNKONE_CHARGEING=13;        //充值未知
	public static  int REFUNDING=30;         //退款中
	public static  int REFUND_SUCCESS=31;    //退款成功
	public static  int REFUND_FAILD=32;     //退款失败
	public static  int UNKONE_REFUND=33;     //退款未知
	public static  int CORRECTING=40;      //冲正中
	public static  int CORRECT_SUCCESS=41; //冲正成功
	public static  int CORRECT_FAILD=42;   //冲正失败
	public static  int CANEL=50;         //撤销

	
	public static void main(String[] args) {
		JSONObject jsonObject = new JSONObject();
		System.err.println(jsonObject.getString("ss"));
	}

}
