package com.berbon.jfaccount.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by chj on 2016/8/17.
 */
public class MobieChargeOrderInfo implements Serializable {

    private long id;
    private String order_sn;
    private String order_id007;
    private long ub_id;
    private long charge_price_id;
    private String mob;
    private long amount;
    private long price;
    private int operid;
    private int charge_result;
    private Date add_time;
    private Date end_time;
    private int money;
    private String order_id;
    private Date order_time;
    private Date refundTime;
    private String merid;
    private String meraccount;
    private String user_code;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getOrder_id007() {
        return order_id007;
    }

    public void setOrder_id007(String order_id007) {
        this.order_id007 = order_id007;
    }

    public long getUb_id() {
        return ub_id;
    }

    public void setUb_id(long ub_id) {
        this.ub_id = ub_id;
    }

    public long getCharge_price_id() {
        return charge_price_id;
    }

    public void setCharge_price_id(long charge_price_id) {
        this.charge_price_id = charge_price_id;
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getOperid() {
        return operid;
    }

    public void setOperid(int operid) {
        this.operid = operid;
    }

    public int getCharge_result() {
        return charge_result;
    }

    public void setCharge_result(int charge_result) {
        this.charge_result = charge_result;
    }

    public Date getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Date add_time) {
        this.add_time = add_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public Date getOrder_time() {
        return order_time;
    }

    public void setOrder_time(Date order_time) {
        this.order_time = order_time;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public String getMerid() {
        return merid;
    }

    public void setMerid(String merid) {
        this.merid = merid;
    }

    public String getMeraccount() {
        return meraccount;
    }

    public void setMeraccount(String meraccount) {
        this.meraccount = meraccount;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }
}
