package com.berbon.jfaccount.impl;


import com.alibaba.dubbo.common.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.berbon.jfaccount.Dao.UserBaseInfoDao;
import com.berbon.jfaccount.facade.pojo.UserBaseInfo;
import com.berbon.jfaccount.pojo.MqBindNotifyObj;
import com.sztx.se.core.mq.consumer.MqMessageListener;
import com.sztx.se.rpc.dubbo.source.DynamicDubboClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by chj on 2016/11/11.
 */

public class MqMessageHandler  implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger(MqMessageHandler.class);

    @Autowired
    private UserBaseInfoDao baseInfoDao;

    @Override
    public void onMessage(Message message) {
        logger.info("收到消息 {} ", new String(message.getBody()));

        try {
            String usercode = "";

            MqBindNotifyObj obj = JSON.parseObject(new String(message.getBody()), MqBindNotifyObj.class);

            usercode = obj.userId;

            UserBaseInfo info = baseInfoDao.getPartInfo(usercode);
            if (info != null && info.getRealNameVerified() != null && info.getRealNameVerified() == 0) {
                logger.info("用户实名 {}", usercode);
                baseInfoDao.makeUserVerify(info.getId(), 1);
            }
        }catch (Exception e){
            logger.error("异常",e);
        }
    }

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        logger.info("初始化完成 {} {} {}",1,2,3);
//    }
}
