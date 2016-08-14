package com.berbon.jfaccount;


import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Created by chj on 2016/8/5.
 */
public class Start {

    private static Logger logger = LoggerFactory.getLogger(Start.class);

    public static void main(String[] args) {
        try {
            com.pay1pay.hsf.container.Main.main(args);
        } catch (Exception e) {
            logger.error("jf-account-impl start error!", e);
        }
    }
}
