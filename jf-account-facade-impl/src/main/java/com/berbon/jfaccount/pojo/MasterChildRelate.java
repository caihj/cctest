package com.berbon.jfaccount.pojo;

import java.util.Date;

/**
 * Created by chj on 2016/11/22.
 */
public class MasterChildRelate {

    private Long id;

    /**
     * 主账号
     */
    private String masterUserCode;

    /**
     * 子账号
     */
    private String childUserCode;


    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 0:无效 1:有效
     */
    private int state;

    /**
     * 更新时间
     */
    private Date ediTime;

    /**
     * 单月总限额
     */
    private Long maxTransMonth;

    /**
     * 单笔限额
     */
    private Long maxSingleTrans;


    /**
     * 日总限额
     */
    private Long maxDayAmount;

    /**
     * 日总次数
     */
    private Long maxDayCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMasterUserCode() {
        return masterUserCode;
    }

    public void setMasterUserCode(String masterUserCode) {
        this.masterUserCode = masterUserCode;
    }

    public String getChildUserCode() {
        return childUserCode;
    }

    public void setChildUserCode(String childUserCode) {
        this.childUserCode = childUserCode;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getEdiTime() {
        return ediTime;
    }

    public void setEdiTime(Date ediTime) {
        this.ediTime = ediTime;
    }

    public Long getMaxTransMonth() {
        return maxTransMonth;
    }

    public void setMaxTransMonth(Long maxTransMonth) {
        this.maxTransMonth = maxTransMonth;
    }

    public Long getMaxSingleTrans() {
        return maxSingleTrans;
    }

    public void setMaxSingleTrans(Long maxSingleTrans) {
        this.maxSingleTrans = maxSingleTrans;
    }

    public Long getMaxDayAmount() {
        return maxDayAmount;
    }

    public void setMaxDayAmount(Long maxDayAmount) {
        this.maxDayAmount = maxDayAmount;
    }

    public Long getMaxDayCount() {
        return maxDayCount;
    }

    public void setMaxDayCount(Long maxDayCount) {
        this.maxDayCount = maxDayCount;
    }
}
