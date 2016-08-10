package com.berbon.jfaccount.facade.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by chj on 2016/8/5.
 */
public class ChargeOrderInfo implements Serializable {

    private long id;
    private String chargeBussOrderNo;   // '充值业务订单号',
    private Date createTime;     //` datetime NOT NULL COMMENT '创建时间,下单时间',
    private String  chargeUserCode;     //` varchar(45) NOT NULL COMMENT '充值userCode',
    private long  amount;     //` bigint(20) NOT NULL COMMENT '充值金额，单位分',
    private int  channelId;      //` int(11) NOT NULL COMMENT '充值渠道Id',
    private int  srcChannel;     //` int(11) NOT NULL COMMENT '来源渠道 1网站，2手机，3微信,4内部',
    private int  state;      //` int(11) NOT NULL COMMENT '充值状态,0 ,初始态 1.充值中 2. 成功 3.失败 4.未知,',
    private String  stateDesc;      //` varchar(45) NOT NULL COMMENT '结果描述',
    private Date  callbackTime;       //` datetime NOT NULL COMMENT '充值结果时间',
    private String  tradeOrderId;       //` varchar(45) NOT NULL COMMENT '充值，交易系统订单号',
    private int  payValue;       //` int(11) NOT NULL COMMENT '实际支付金额',
    private String  bankId;     //` varchar(45) DEFAULT NULL COMMENT '支付银行编号',
    private String  attach;     //` varchar(45) DEFAULT NULL COMMENT '备注',
    private String bindNo; //绑定的银行卡编号
    private String cardNo; //银行卡号
    private int type;       //类型，1.使用绑定的银行卡支付，2.使用新的银行卡绑定并支付，获取验证码 3.使用新的银行卡支付，网银支付
    private int cardType;  //卡类型：1借记卡；2信用卡
    private String cvv;  //信用卡cvv
    private String expireDate; //有效期格式yyMM
    private String realName; //真实姓名
    private String identityNo; //证件号
    private String mobileNo; //手机号

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChargeBussOrderNo() {
        return chargeBussOrderNo;
    }

    public void setChargeBussOrderNo(String chargeBussOrderNo) {
        this.chargeBussOrderNo = chargeBussOrderNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getChargeUserCode() {
        return chargeUserCode;
    }

    public void setChargeUserCode(String chargeUserCode) {
        this.chargeUserCode = chargeUserCode;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getSrcChannel() {
        return srcChannel;
    }

    public void setSrcChannel(int srcChannel) {
        this.srcChannel = srcChannel;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String stateDesc) {
        this.stateDesc = stateDesc;
    }

    public Date getCallbackTime() {
        return callbackTime;
    }

    public void setCallbackTime(Date callbackTime) {
        this.callbackTime = callbackTime;
    }

    public String getTradeOrderId() {
        return tradeOrderId;
    }

    public void setTradeOrderId(String tradeOrderId) {
        this.tradeOrderId = tradeOrderId;
    }

    public int getPayValue() {
        return payValue;
    }

    public void setPayValue(int payValue) {
        this.payValue = payValue;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getBindNo() {
        return bindNo;
    }

    public void setBindNo(String bindNo) {
        this.bindNo = bindNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}
