package com.berbon.jfaccount.facade.pojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/10.
 */
public class TransferOrderCrtRsp implements Serializable {


    private String msg;

    private TransferOrderInfo orderInfo;

    public TransferOrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(TransferOrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
