package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.pojo.GameChargeOrderInfo;
import com.berbon.util.mapper.BaseMapper;
import com.pay1pay.framework.core.spring.Pay1payJdbcTemplate;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
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

    private static Logger logger = LoggerFactory.getLogger(GameChargeOrderDao.class);


    public GameChargeOrderInfo queryOrder(String orderId){

        String sql = "select * from t_downOrders where downOrderID=\""+ orderId + "\"";

        GameChargeOrderInfo info = null;
        try {
            info = gameslaveTemplate.queryForObject(sql,new BaseMapper<GameChargeOrderInfo>(GameChargeOrderInfo.class));
        } catch (DataAccessException e) {
            logger.error("异常"+e);
            e.printStackTrace();
        }

        return info;
    }
}
