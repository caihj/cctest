package com.berbon.jfaccount.facade.pojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/11/22.
 */
public class AcquireTransferReq implements Serializable {

    /**
     * 转入的子账户
     */
    public String userCode;

    /**
     * 正在发生的交易的支付金额
     */
    public Long amount;

    /**
     * 正在发生的交易的业务订单号
     */
    public String bussOrderId;

    /**
     * 发生交易的ip地址
     */
    public String ip;

}
