package com.berbon.jfaccount.comm;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by chj on 2016/8/5.
 */

@Component
public class InitBean implements InitializingBean {

    /**
     * 充值前端通知
     */
    @Value("${chargefrontNotifyUrl}")
    public String chargefrontNotifyUrl;

    /**
     * 充值后端通知
     */
    @Value("${chargebackNotifyUrl}")
    public String chargebackNotifyUrl;

    @Value("${newPayKey}")
    public String newPayKey;

    /**
     * 转账前端通知
     */
    @Value("${transferNotifyUrl}")
    public String transferNotifyUrl;

    /**
     * 转账后端通知
     */
    @Value("${transferbackNotifyUrl}")
    public String transferbackNotifyUrl;

    /**
     * 交易前端通知
     */
    @Value("${payfrontNotifyUrl}")
    public String payfrontNotifyUrl;

    /**
     * 交易后端通知
     */
    @Value("${payBackNotifyUrl}")
    public String payBackNotifyUrl;


    @Value("${channelId}")
    public String channelId="99";

    /**
     * 最长转账订单生存时间，单位秒
     */
    @Value("${maxChargeOrderAliveSec}")
    private String __maxChargeOrderAliveSec;


    public int maxChargeOrderAliveSec;


//    /**
//     * 话费充值 支付通知 merId
//     */
//    public String mobileChargeMerId="1000000008";
//
//    /**
//     * 游戏充值 支付通知 merId
//     */
//    public String gameChargeMerId="1000000008";


    @Value("${mobileChargeSignKey}")
    public String mobileChargeSignKey;

    @Value("${gameChargeSignKey}")
    public String gameChargeSignKey;


    /**
     *手机端支付订单映射时间
     */
    @Value("${MobOrderToTradeOrderIdExistSecods}")
    public int MobOrderToTradeOrderIdExistSecods;

    /**
     * #话费余额支付收款账号--移动端
     */
    @Value("${MobileChargePayeeUserCode}")
    public String MobileChargePayeeUserCode;


    /*
    游戏余额支付收款账号--移动端
     */
    @Value("${GameChargePayeeUserCode}")
    public String GameChargePayeeUserCode;

    /*
    提现回调地址
     */
    @Value("${withDrawBackNotifyUrl}")
    public String WithDrawBackNotifyUrl;


    /**
     * 加款赠送活动
     */
    //@Value("${activityGiveMoneyFineUserCode}")
    public String activityGiveMoneyFineUserCode;

    @Override
    public void afterPropertiesSet() throws Exception {
        maxChargeOrderAliveSec = Integer.parseInt(__maxChargeOrderAliveSec);
    }
}
