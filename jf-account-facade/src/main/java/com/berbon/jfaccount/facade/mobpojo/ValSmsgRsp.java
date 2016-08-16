package com.berbon.jfaccount.facade.mobpojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/14.
 */
public class ValSmsgRsp implements Serializable {
    /**
     * 结果代码 (1, "待支付"), (2,"支付中"),(3, "成功"),(4,"失败"),(5,"异常");
     */
    private String resultCode;
    /**
     * 结果描述
     */
    private String resultMsg;


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
}
