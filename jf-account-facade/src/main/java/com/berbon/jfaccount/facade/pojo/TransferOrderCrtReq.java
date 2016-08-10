package com.berbon.jfaccount.facade.pojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/10.
 */
public class TransferOrderCrtReq implements Serializable {

    /**用户倍棒号
     *
     */
    private String userCode;

    /**
     * 金额，单位分
     */
    private long amount;
    /**
     * 备注
     */
    private String note;

    /**
     * 接收转账成功通知的手机号码
     */
    private String phone;
    /**
     * 1使用余额支付.2.使用绑定的银行卡支付
     */
    private int type;
    /**
     * 已绑定的银行卡绑定编号
     */
    private String bindNo;
    /**
     * 接收方真实姓名
     */
    private String realName;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBindNo() {
        return bindNo;
    }

    public void setBindNo(String bindNo) {
        this.bindNo = bindNo;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
