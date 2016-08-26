package com.berbon.jfaccount.facade.pojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/18.
 */
public enum NotifyOrderType implements Serializable {
    charge_notify,
    transfer_notify,
    withdraw_notify;
}
