package com.berbon.jfaccount.comm;

/**
 * Created by chj on 2016/8/12.
 */
public enum  ErrorCode {


    paypwd_error("101","支付密码错误"),
    sys_error("102","系统异常"),
    para_error("103","参数错误");
    ErrorCode(String code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public String code;
    public String desc;
}
