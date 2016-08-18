package com.berbon.jfaccount.comm;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by chj on 2016/8/5.
 */

@Component
public class InitBean implements InitializingBean {

    @Value("${frontNotifyUrl}")
    public String frontUrl;

    @Value("${bakNotifyUrl}")
    public String backNotifyUrl;

    @Value("${newPayKey}")
    public String newPayKey;

    @Value("${transferNotifyUrl}")
    public String transferNotifyUrl;

    @Value("${transferbackNotifyUrl}")
    public String transferbackNotifyUrl;

    @Value("${channelId}")
    public String channelId="99";

    /**
     * 最长转账订单生存时间，单位秒
     */
    @Value("${maxChargeOrderAliveSec}")
    private String __maxChargeOrderAliveSec;


    @Value("${chargeBusinessType}")
    public String chargeBusinessType;

    @Value("${tranferBusinessType}")
    public String tranferBusinessType;

    @Value("${withdrawBusinessType}")
    public String withdrawBusinessType;

    public int maxChargeOrderAliveSec;


    @Override
    public void afterPropertiesSet() throws Exception {
        maxChargeOrderAliveSec = Integer.parseInt(__maxChargeOrderAliveSec);
    }
}
