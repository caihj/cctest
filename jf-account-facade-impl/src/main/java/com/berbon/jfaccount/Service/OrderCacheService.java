package com.berbon.jfaccount.Service;

import com.berbon.jfaccount.comm.InitBean;
import com.berbon.jfaccount.facade.mobpojo.MobOrderType;
import com.berbon.jfaccount.impl.AccountFacadeImpl;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by chj on 2016/8/22.
 */

@Service
public class OrderCacheService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private InitBean initBean;

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(AccountFacadeImpl.class);

    public  void addNew(String orderId,MobOrderType type,String tradeOrderId){

        String key = null;
        if(type == MobOrderType.game_charge){
            key = "game_"+orderId;
        }else if(type == MobOrderType.mobile_charge){
            key = "phone_"+orderId;
        }

        logger.info("将"+type.name +"订单:" + orderId+" =>"+tradeOrderId+" 加入缓存");

        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps("MobGameOrderMap");

        ops.put(key,tradeOrderId);
        //ops.expire(initBean.MobOrderToTradeOrderIdExistSecods, TimeUnit.SECONDS);
    }

    public  String  getTradeOrderId(String orderId,MobOrderType type){

        String key = null;
        if(type == MobOrderType.game_charge){
            key = "game_"+orderId;
        }else if(type == MobOrderType.mobile_charge){
            key = "phone_"+orderId;
        }

        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps("MobGameOrderMap");
        return ops.get(key);
    }

    public void hello(){

        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps("user");

        ops.put( "name", "张三");
        ops.expire(10, TimeUnit.SECONDS);
        ops.put( "name2", "张三2");

        ops.expire(2, TimeUnit.SECONDS);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(ops.get("name"));


    }
}
