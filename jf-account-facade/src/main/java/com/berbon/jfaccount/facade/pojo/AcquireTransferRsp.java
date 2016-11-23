package com.berbon.jfaccount.facade.pojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/11/22.
 */
public class AcquireTransferRsp implements Serializable {

    /**
     * 返回码
     */
    public ACK ack;

    /**
     * 返回消息
     */
    public String msg;

    /**
     * 转账订单号
     */
    public String transferOrderId;

    /**
     * 转账金额
     */
    public Long amount;



    public static enum ACK implements Serializable{
      SUCCESS,    //转账成功
      NOT_CHILD_ACCOUNT,        //不是子账号
      FAIL;     //其他错误
    }
}
