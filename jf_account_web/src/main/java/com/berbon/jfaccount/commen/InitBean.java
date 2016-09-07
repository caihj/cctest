package com.berbon.jfaccount.commen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by chj on 2016/8/5.
 */
@Component
public class InitBean {

    @Value("${berbonsessionId}")
    public String berbonsessionId;

    @Value("${redictUrl}")
    public String redictUrl;

    @Value("${channelId}")
    public String channelId;

    @Value("${userCreditCard}")
    public boolean userCreditCard;
}
