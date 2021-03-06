package com.berbon.jfaccount.facade.pojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/9.
 */
public class QuickPayMsgInfoReq implements Serializable {
    private String verifyCode;
    private String tradeOrderId;
    private String attach;

    //来源渠道 1网站，2手机，3微信,4内部
    private String srcChannel;

    private String ip;

    private BusOrderType type;

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getTradeOrderId() {
        return tradeOrderId;
    }

    public void setTradeOrderId(String tradeOrderId) {
        this.tradeOrderId = tradeOrderId;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getSrcChannel() {
        return srcChannel;
    }

    public void setSrcChannel(String srcChannel) {
        this.srcChannel = srcChannel;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public BusOrderType getType() {
        return type;
    }

    public void setType(BusOrderType type) {
        this.type = type;
    }
}
