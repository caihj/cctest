package com.berbon.jfaccount.facade.mobpojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/14.
 */
public class CreateChargeReq implements Serializable{
    private String userCode;
    private Long payAmount;
    private String fromMob;
    private String fromIp;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Long getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Long payAmount) {
        this.payAmount = payAmount;
    }

    public String getFromMob() {
        return fromMob;
    }

    public void setFromMob(String fromMob) {
        this.fromMob = fromMob;
    }

    public String getFromIp() {
        return fromIp;
    }

    public void setFromIp(String fromIp) {
        this.fromIp = fromIp;
    }
}
