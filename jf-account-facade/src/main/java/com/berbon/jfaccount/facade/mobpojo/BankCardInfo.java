package com.berbon.jfaccount.facade.mobpojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/14.
 */
public class BankCardInfo implements Serializable {
    private String bindNo;
    private String cardNo;
    private String bankName;
    private String swiftCode;

    public String getBindNo() {
        return bindNo;
    }

    public void setBindNo(String bindNo) {
        this.bindNo = bindNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }
}
