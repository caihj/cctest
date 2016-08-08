package com.berbon.jfaccount.pojo;

import java.util.Date;

/**
 * Created by chj on 2016/8/8.
 */
public class TransferData {

    private Date addTime;
    private String transferOrderId;
    private String transferTypeStr;

    //交易对方
    private String  otherAccount;

    //金额
    private String transferAmount;
    //状态
    private String transferStatusStr;
    //转入/转出 (in/out)
    private String type;
    //备注
    private String remark;


    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getTransferOrderId() {
        return transferOrderId;
    }

    public void setTransferOrderId(String transferOrderId) {
        this.transferOrderId = transferOrderId;
    }

    public String getTransferTypeStr() {
        return transferTypeStr;
    }

    public void setTransferTypeStr(String transferTypeStr) {
        this.transferTypeStr = transferTypeStr;
    }

    public String getOtherAccount() {
        return otherAccount;
    }

    public void setOtherAccount(String otherAccount) {
        this.otherAccount = otherAccount;
    }

    public String getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getTransferStatusStr() {
        return transferStatusStr;
    }

    public void setTransferStatusStr(String transferStatusStr) {
        this.transferStatusStr = transferStatusStr;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

