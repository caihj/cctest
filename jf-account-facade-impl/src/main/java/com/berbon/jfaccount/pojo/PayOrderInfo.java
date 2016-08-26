package com.berbon.jfaccount.pojo;

import java.util.Date;

/**
 * Created by chj on 2016/8/25.
 */
public class PayOrderInfo {

    public static enum OrderState {
        init(0,"初始态"),
        paying(1,"支付中"),
        success(2,"成功"),
        fail(3,"失败"),
        exceptioned(4,"异常");

         OrderState(int state,String desc){
            this.state = state;
            this.desc = desc;
        }

        public int state;
        public String desc;
    }

    private long id;
    private int orderType;
    private String busOrderId;
    private String tradeOrderId;
    private Date createtime;
    private int orderState;
    private String orderStateDesc;
    private Date lastUpdateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getBusOrderId() {
        return busOrderId;
    }

    public void setBusOrderId(String busOrderId) {
        this.busOrderId = busOrderId;
    }

    public String getTradeOrderId() {
        return tradeOrderId;
    }

    public void setTradeOrderId(String tradeOrderId) {
        this.tradeOrderId = tradeOrderId;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public String getOrderStateDesc() {
        return orderStateDesc;
    }

    public void setOrderStateDesc(String orderStateDesc) {
        this.orderStateDesc = orderStateDesc;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
