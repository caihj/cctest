package com.berbon.jfaccount.impl;


import com.berbon.jfaccount.Dao.UserBaseInfoDao;
import com.berbon.jfaccount.facade.pojo.UserBaseInfo;
import com.sztx.se.core.mq.consumer.MqMessageListener;
import com.sztx.se.rpc.dubbo.source.DynamicDubboClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by chj on 2016/11/11.
 */

public class MqMessageHandler  extends MqMessageListener implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(MqMessageHandler.class);

    @Autowired
    private UserBaseInfoDao baseInfoDao;

    @Override
    public Object handleMessage(String messageId, String messageContent, String queue) {
        logger.info("收到消息 {} {} {}", messageId, messageContent, queue);

        String usercode = "";

        UserBaseInfo info = baseInfoDao.getPartInfo(usercode);
        if(info!=null && info.getRealNameVerified()!=null && info.getRealNameVerified()==0)
            baseInfoDao.makeUserVerify(info.getId(),1);

        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("初始化完成 {} {} {}",1,2,3);
    }
}
