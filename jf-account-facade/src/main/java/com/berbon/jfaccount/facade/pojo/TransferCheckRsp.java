package com.berbon.jfaccount.facade.pojo;

/**
 * Created by chj on 2016/8/10.
 */
public class TransferCheckRsp {
    protected boolean canTransfer;
    protected String msg;

    public boolean isCanTransfer() {
        return canTransfer;
    }

    public void setCanTransfer(boolean canTransfer) {
        this.canTransfer = canTransfer;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
