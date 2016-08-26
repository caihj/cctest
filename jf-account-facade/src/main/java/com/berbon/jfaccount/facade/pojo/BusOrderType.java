package com.berbon.jfaccount.facade.pojo;

/**
 * Created by chj on 2016/8/22.
 */
public enum BusOrderType {

    charge_order("充值订单"),
    transfer_order("转账订单");

    BusOrderType(String name){this.name = name;}
    public String name;
}
