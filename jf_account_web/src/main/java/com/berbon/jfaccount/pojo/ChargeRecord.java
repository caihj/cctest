package com.berbon.jfaccount.pojo;

import java.util.Date;

/**
 * Created by chj on 2016/8/8.
 */
public class ChargeRecord {
    private Date addTime;
    private String rechargeOrderId;
    private String rechargeChannelStr;
    private String rechargeAmount;
    private String rechargeStatusStr;

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getRechargeOrderId() {
        return rechargeOrderId;
    }

    public void setRechargeOrderId(String rechargeOrderId) {
        this.rechargeOrderId = rechargeOrderId;
    }

    public String getRechargeChannelStr() {
        return rechargeChannelStr;
    }

    public void setRechargeChannelStr(String rechargeChannelStr) {
        this.rechargeChannelStr = rechargeChannelStr;
    }

    public String getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(String rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public String getRechargeStatusStr() {
        return rechargeStatusStr;
    }

    public void setRechargeStatusStr(String rechargeStatusStr) {
        this.rechargeStatusStr = rechargeStatusStr;
    }
}
