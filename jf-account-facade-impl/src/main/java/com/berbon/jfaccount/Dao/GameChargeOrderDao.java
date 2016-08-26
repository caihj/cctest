package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.pojo.GameChargeOrderInfo;
import com.berbon.util.mapper.BaseMapper;
import com.pay1pay.framework.core.spring.Pay1payJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by chj on 2016/8/18.
 */
@Component
public class GameChargeOrderDao {

    @Autowired
    @Qualifier("gameslaveTemplate")
    private Pay1payJdbcTemplate  gameslaveTemplate;


    public GameChargeOrderInfo queryOrder(String orderId){

        String sql = "select * from t_downOrders where downOrderID=\""+ orderId + "\"";

        return gameslaveTemplate.queryForObject(sql,new BaseMapper<GameChargeOrderInfo>(GameChargeOrderInfo.class));
    }
}
