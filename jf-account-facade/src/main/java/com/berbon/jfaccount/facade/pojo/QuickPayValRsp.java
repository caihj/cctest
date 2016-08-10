package com.berbon.jfaccount.facade.pojo;

import java.util.Map;

/**
 * Created by chj on 2016/8/9.
 */
public class QuickPayValRsp {
    //结果代码 (1, "待支付"), (2,"支付中"),(3, "成功"),(4,"失败"),(5,"异常");
    protected String resultCode;
    protected String resultMsg;
    protected String tradeOrderId;

    protected String payUrl;
    protected Map<String, String> payParams;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getTradeOrderId() {
        return tradeOrderId;
    }

    public void setTradeOrderId(String tradeOrderId) {
        this.tradeOrderId = tradeOrderId;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public Map<String, String> getPayParams() {
        return payParams;
    }

    public void setPayParams(Map<String, String> payParams) {
        this.payParams = payParams;
    }
}
