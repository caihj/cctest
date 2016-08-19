package com.berbon.jfaccount.pojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/18.
 */

public class GameChargeOrderInfo implements Serializable {

    private long	id;
    private String	downOrderID;
    private String	sequence;
    private String	cutomerID;
    private long	totalMoney;
    private long	reverse;
    private long	orderCreateTime;
    private long	payTime;
    private String	remark;
    private int	orderStatus;
    private int	status;
    private String	agentsID;
    private String	ip;
    private String	backUrl;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDownOrderID() {
        return downOrderID;
    }

    public void setDownOrderID(String downOrderID) {
        this.downOrderID = downOrderID;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getCutomerID() {
        return cutomerID;
    }

    public void setCutomerID(String cutomerID) {
        this.cutomerID = cutomerID;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public long getReverse() {
        return reverse;
    }

    public void setReverse(long reverse) {
        this.reverse = reverse;
    }

    public long getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(long orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public long getPayTime() {
        return payTime;
    }

    public void setPayTime(long payTime) {
        this.payTime = payTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAgentsID() {
        return agentsID;
    }

    public void setAgentsID(String agentsID) {
        this.agentsID = agentsID;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }
}
