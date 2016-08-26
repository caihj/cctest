package com.berbon.jfaccount.facade.pojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/22.
 */
public class ReSendChargeValMsgRsp implements Serializable {

    private boolean ok;
    private String msg;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
