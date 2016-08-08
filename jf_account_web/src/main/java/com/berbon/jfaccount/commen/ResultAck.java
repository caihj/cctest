package com.berbon.jfaccount.commen;

/**
 * Created by chj on 2016/8/3.
 */
public  enum ResultAck {

    succ("0","成功"),
    fail("-1","失败"),
    para_error("-2","参数错误");


    ResultAck(String code,String desc){
        this.code=code;
        this.desc=desc;
    }

    private String code;
    private String desc;

    public String getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }

}
