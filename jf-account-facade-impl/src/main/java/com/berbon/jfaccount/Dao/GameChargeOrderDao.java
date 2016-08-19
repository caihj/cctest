package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.pojo.GameChargeOrderInfo;
import com.berbon.util.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by chj on 2016/8/18.
 */
@Component
public class GameChargeOrderDao {

    @Autowired
    private JdbcTemplate gameslaveTemplate;


    public GameChargeOrderInfo queryOrder(String orderId){

        String sql = "select * from t_downOrders where downOrderID=\""+ orderId + "\"";

        return gameslaveTemplate.queryForObject(sql,new BaseMapper<GameChargeOrderInfo>(GameChargeOrderInfo.class));
    }
}
