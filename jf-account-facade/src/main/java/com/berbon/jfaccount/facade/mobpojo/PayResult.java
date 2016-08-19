package com.berbon.jfaccount.facade.mobpojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/17.
 */
public class PayResult implements Serializable{

    public  Result result;

    public PayResult(Result result, String payOrderId, long payValue) {
        this.result = result;
        this.payOrderId = payOrderId;
        this.payValue = payValue;
    }

    public static enum Result {
        paying("支付中"),
        succ("支付成功"),
        fail("支付失败"),
        exception("支付异常"),
        unknow("未知");
        Result(String s){}
    }

    public String payOrderId;
    public long payValue;
}
