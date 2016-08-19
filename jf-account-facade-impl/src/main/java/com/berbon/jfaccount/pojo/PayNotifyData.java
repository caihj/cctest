package com.berbon.jfaccount.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by chj on 2016/8/18.
 */
public class PayNotifyData implements Serializable{
    public String orderId;
    public String payOrderId;
    public long payAmount;
    public Date payTime;
}
