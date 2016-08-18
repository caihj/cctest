package com.berbon.jfaccount.facade.pojo;

/**
 * Created by chj on 2016/8/18.
 */
public class ValNotifyRsp {

    public static enum CODE{
        succ,
        fail,
        exception;
    }

    /**
     */
    private CODE code;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     *业务系统订单号
     */
    private String orderId;

    /**
     * 支付系统订单号
     */
    private String tradeOrderId;

    /**
     * 金额
     */
    private long  amount;


    public CODE getCode() {
        return code;
    }

    public void setCode(CODE code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTradeOrderId() {
        return tradeOrderId;
    }

    public void setTradeOrderId(String tradeOrderId) {
        this.tradeOrderId = tradeOrderId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
