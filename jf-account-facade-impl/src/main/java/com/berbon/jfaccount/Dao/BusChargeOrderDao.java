package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.pojo.MobieChargeOrderInfo;
import com.berbon.util.mapper.BaseMapper;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by chj on 2016/8/17.
 */

@Component
public class BusChargeOrderDao  {

    private static Logger logger = LoggerFactory.getLogger(ChargeOrderDao.class);

    @Autowired
    private JdbcTemplate masterTemplate;
    @Autowired
    private JdbcTemplate slaveTemplate;

    public MobieChargeOrderInfo queryOrder(String orderId){

        String sql = "select * from charge_order where order_sn=\""+ orderId+"\"";
        MobieChargeOrderInfo info =null;
        try {
             info= slaveTemplate.queryForObject(sql, new RowMapper<MobieChargeOrderInfo>() {
                 @Override
                 public MobieChargeOrderInfo mapRow(ResultSet rs, int rowNum) throws SQLException {

                     MobieChargeOrderInfo info = new MobieChargeOrderInfo();

                     Field[] fields = MobieChargeOrderInfo.class.getDeclaredFields();
                     try{
                         for(Field field:fields) {
                             field.setAccessible(true);
                             Class<?> type = field.getType();

                             if (type.equals(int.class)) {
                                 field.set(info, rs.getInt(field.getName()));
                             } else if (type.equals(long.class)) {
                                 field.set(info, rs.getLong(field.getName()));
                             } else if (type.equals(String.class)) {
                                 field.set(info, rs.getString(field.getName()));
                             } else {

                             }
                         }
                     }
                     catch ( IllegalAccessException e){
                         logger.error("反射异常");
                     }

                     return info;
                 }
             });
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return info;
    }
}
