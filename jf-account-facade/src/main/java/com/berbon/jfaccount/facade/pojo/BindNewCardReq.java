package com.berbon.jfaccount.facade.pojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/15.
 */
public class BindNewCardReq implements Serializable {

    private String userCode;
    private int bindType;
    private int cardType;
    private String cardNo;
    private String cvv;
    private String expireDate;
    private String mobileNo;

    /**
     * 此操作是否是在进行实名认证
     */
    private boolean doVerify;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public int getBindType() {
        return bindType;
    }

    public void setBindType(int bindType) {
        this.bindType = bindType;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public boolean isDoVerify() {
        return doVerify;
    }

    public void setDoVerify(boolean doVerify) {
        this.doVerify = doVerify;
    }
}
