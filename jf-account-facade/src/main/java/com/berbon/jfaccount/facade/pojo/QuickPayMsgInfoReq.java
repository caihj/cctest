package com.berbon.jfaccount.facade.pojo;

/**
 * Created by chj on 2016/8/9.
 */
public class QuickPayMsgInfoReq {
    private String verifyCode;
    private String tradeOrderId;
    private String attach;

    //来源渠道 1网站，2手机，3微信,4内部
    private String srcChannel;

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
}
