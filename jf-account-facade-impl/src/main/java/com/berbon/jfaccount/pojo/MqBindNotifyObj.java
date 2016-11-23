package com.berbon.jfaccount.pojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/11/22.
 */
public class MqBindNotifyObj implements Serializable{
    /**
     * 用户帐号
     */
    public String userId;
    /**
     * 绑卡号
     */
    public String bindCardNo;
    /**
     * 姓名
     */
    public String realName;
    /**
     * 银行ID
     */
    public String bankId;
    /**
     * 银行名称
     */
    public String bankName;
    /**
     * 证件类型：0、身份证(默认) 1、护照
     */
    public Integer identityType;
    /**
     * 身份证号码
     */
    public String identityNo;
    /**
     * 手机号
     */
    public String mobileNo;
    /**
     * 卡号
     */
    public String cardNo;
    /**
     * 银行卡对公还是对私：1-对私账户 2-对公账户
     */
    public Integer cardActType;
    /**
     * 银行卡类型：1借记卡；2信用卡
     */
    public Integer cardType;
    /**
     * 绑定类别（1快捷支付，2提现银行卡， 3提现支付宝)
     */
    public Integer bindType;

}
