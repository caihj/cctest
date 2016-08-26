package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.facade.mobpojo.MobOrderType;
import com.berbon.jfaccount.pojo.PayOrderInfo;
import com.berbon.util.mapper.BaseMapper;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * Created by chj on 2016/8/25.
 */
@Component
public class PayOrderDao {

    private static Logger logger = LoggerFactory.getLogger(PayOrderDao.class);

    @Autowired
    private JdbcTemplate masterTemplate;
    @Autowired
    private JdbcTemplate slaveTemplate;

    public PayOrderInfo newOrder(final PayOrderInfo orderInfo){

        KeyHolder keyHolder = new GeneratedKeyHolder();

        masterTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

                String sql = "INSERT INTO `account_pay_order`\n" +
                        "(\n" +
                        "`orderType`,\n" +
                        "`busOrderId`,\n" +
                        "`tradeOrderId`,\n" +
                        "`createtime`,\n" +
                        "`orderState`,\n" +
                        "`orderStateDesc`,\n" +
                        "`lastUpdateTime`)\n" +
                        "VALUES\n" +
                        "(?,?,?,?,?,?,?);";

                PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                int i = 1;
                ps.setInt(i++, orderInfo.getOrderType());
                ps.setString(i++, orderInfo.getBusOrderId());
                ps.setString(i++, orderInfo.getTradeOrderId());
                ps.setTimestamp(i++, new Timestamp(orderInfo.getCreatetime().getTime()));
                ps.setInt(i++, orderInfo.getOrderState());
                ps.setString(i++, orderInfo.getOrderStateDesc());
                if(orderInfo.getLastUpdateTime()!=null)
                    ps.setTimestamp(i++, new Timestamp(orderInfo.getLastUpdateTime().getTime()));
                else
                    ps.setTimestamp(i++,null);

                return ps;

            }
        });
        long id= keyHolder.getKey().longValue();
        orderInfo.setId(id);

        return orderInfo;

    }

    public PayOrderInfo getByOrderIdAndType(String orderID,MobOrderType orderType){
        PayOrderInfo info = null;
        try {
            String sql ="select * from account_pay_order where busOrderId=\""+orderID+"\""+" and orderType="+orderType.type;
            info = slaveTemplate.queryForObject(sql,new BaseMapper<PayOrderInfo>(PayOrderInfo.class));
        }
        catch (Exception e){
            return null;
        }

        return info;
    }


    public PayOrderInfo getByOrderIdAndTradeOrderId(String orderID,String tradeOrderId){
        PayOrderInfo info = null;
        try {
            String sql ="select * from account_pay_order where busOrderId=\""+orderID+"\""+" and tradeOrderId=\""+tradeOrderId+"\"";
            info = slaveTemplate.queryForObject(sql,new BaseMapper<PayOrderInfo>(PayOrderInfo.class));
        }
        catch (Exception e){
            return null;
        }

        return info;
    }



    public void updateOrderState(final String orderID, final MobOrderType orderType, final PayOrderInfo.OrderState orderState){

        masterTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

                String sql = "update account_pay_order set lastUpdateTime=now(), orderState="+orderState.state + " ,orderStateDesc=\""+orderState.desc+"\"";
                sql+=" where busOrderId=\""+orderID+"\""+" and orderType="+orderType.type;
                PreparedStatement ps = con.prepareStatement(sql);
                return ps;
            }
        });
    }

}
