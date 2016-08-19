package com.berbon.jfaccount.pojo;

import com.berbon.jfaccount.comm.BusinessType;
import com.berbon.jfaccount.facade.mobpojo.MobOrderType;

import java.util.Date;

/**
 * Created by chj on 2016/8/17.
 */
public class MobileOrderInfo {


    /**
     * 订单类型
     */
    private MobOrderType orderType;

    private String userCode;

    private String orderId;

    /**
     * 金额，单位分
     */
    private long amount;
    /**
     * 交易类型
     */
    private BusinessType tradeType;

    /**
     * 下单时间
     */
    private Date downOrderTime;


    private  PayType payType;

    /**
     * 快捷支付绑定的额银行卡号
     */
    private String bindNo;

    private String bankId;

    private String goodsName;

    private String goodsDetail;


    private String fromIp;

    public MobOrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(MobOrderType orderType) {
        this.orderType = orderType;
    }

    public String getFromIp() {
        return fromIp;
    }

    public void setFromIp(String fromIp) {
        this.fromIp = fromIp;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsDetail() {
        return goodsDetail;
    }

    public void setGoodsDetail(String goodsDetail) {
        this.goodsDetail = goodsDetail;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public BusinessType getTradeType() {
        return tradeType;
    }

    public void setTradeType(BusinessType tradeType) {
        this.tradeType = tradeType;
    }

    public Date getDownOrderTime() {
        return downOrderTime;
    }

    public void setDownOrderTime(Date downOrderTime) {
        this.downOrderTime = downOrderTime;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public String getBindNo() {
        return bindNo;
    }

    public void setBindNo(String bindNo) {
        this.bindNo = bindNo;
    }

    public static  enum PayType{
        balance_pay("余额支付"),
        bind_quick_pay("快捷支付");
        PayType(String desc){}
    }


}
