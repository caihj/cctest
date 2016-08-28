package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.pojo.MobieChargeOrderInfo;
import com.berbon.util.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by chj on 2016/8/17.
 */

@Component
public class BusChargeOrderDao  {


    @Autowired
    private JdbcTemplate masterTemplate;
    @Autowired
    private JdbcTemplate slaveTemplate;

    public MobieChargeOrderInfo queryOrder(String orderId){

        String sql = "select * from charge_order where order_sn=\""+ orderId+"\"";
        MobieChargeOrderInfo info =null;
        try {
             info= slaveTemplate.queryForObject(sql, new BaseMapper<MobieChargeOrderInfo>(MobieChargeOrderInfo.class));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return info;
    }
}
