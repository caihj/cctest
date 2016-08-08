package com.berbon.jfaccount.pojo;

import java.util.Date;

/**
 * Created by chj on 2016/8/8.
 */
public class CashRecord {
    private Date payDate;
    private String tradeOrderNo;
    private int tradeOrderType;
    private String otherAccount;
    private String type;
    //金额，小数类型，这里用string表示
    private String amount;
    private String balance;

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getTradeOrderNo() {
        return tradeOrderNo;
    }

    public void setTradeOrderNo(String tradeOrderNo) {
        this.tradeOrderNo = tradeOrderNo;
    }

    public int getTradeOrderType() {
        return tradeOrderType;
    }

    public void setTradeOrderType(int tradeOrderType) {
        this.tradeOrderType = tradeOrderType;
    }

    public String getOtherAccount() {
        return otherAccount;
    }

    public void setOtherAccount(String otherAccount) {
        this.otherAccount = otherAccount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
