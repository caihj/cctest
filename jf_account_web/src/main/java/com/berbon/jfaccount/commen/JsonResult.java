package com.berbon.jfaccount.commen;

/**
 * Created by chj on 2016/8/3.
 */
public class JsonResult {
    private String retinfo;
    private String result;
    private Object data;

    public String getRetinfo() {
        return retinfo;
    }

    public void setRetinfo(String retinfo) {
        this.retinfo = retinfo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setResult(ResultAck result) {
        this.result = result.getCode();
        this.retinfo = result.getDesc();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
