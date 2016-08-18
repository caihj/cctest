package com.berbon.jfaccount.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chj on 2016/8/5.
 */
public class UtilTool {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
    private static int randomNum = 1;//生成订单号的随机数

    public static String ChargeOrderPrefix = "CZ";
    public static String transferOrderPrefix = "ZZ";
    public static String withdrawOrderPrefix = "TX";


    private static  String getRandom(int randomNum, int needNum) {
        String result = randomNum + "";
        int size = needNum - (result).length();
        for (int i = 0; i < size; i++) {
            result = "0" + result;
        }
        return result;
    }

    public static  String createOrderId(String prefix) {
        synchronized (UtilTool.class) {// 加锁避免出现相同的orderId
            String orderId = prefix + sdf.format(new Date()) + getRandom(randomNum++, 2);
            if(randomNum == 100){
                randomNum = 1;
            }
            return orderId;
        }
    }

    public static String generateChargeOrderId(){
        return createOrderId(ChargeOrderPrefix);
    }

    public static String generateTransferOrderId(){
        return createOrderId(transferOrderPrefix);
    }

    public static String generateWithdrawOrderId(){
        return createOrderId(withdrawOrderPrefix);
    }

}
