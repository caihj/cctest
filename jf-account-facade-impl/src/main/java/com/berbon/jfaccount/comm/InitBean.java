package com.berbon.jfaccount.comm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by chj on 2016/8/5.
 */

@Component
public class InitBean {

    @Value("frontNotifyUrl")
    public String frontUrl;

    @Value("bakNotifyUrl")
    public String bakNotifyUrl;

    @Value("newPayKey")
    public String newPayKey;



}
