package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.pojo.MobieChargeOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
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

    private MobieChargeOrderInfo DaoqueryOrder(String orderId){

        String sql = "select * from charge_order where order_sn=\""+ orderId+"\"";




        return null;
    }
}