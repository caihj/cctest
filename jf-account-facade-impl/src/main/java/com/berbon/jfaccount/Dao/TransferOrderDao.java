package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.facade.pojo.TransferOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by chj on 2016/8/10.
 */
public class TransferOrderDao {

    @Autowired
    private JdbcTemplate masterTemplate;
    @Autowired
    private JdbcTemplate slaveTemplate;

    public TransferOrderInfo createOrder(final TransferOrderInfo order){

        KeyHolder keyHolder = new GeneratedKeyHolder();


        masterTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO `account_transfer_order`\n" +
                        "`fromUserCode`,\n" +
                        "`toUserCode`,\n" +
                        "`amount`,\n" +
                        "`payType`,\n" +
                        "`receiverType`,\n" +
                        "`bindNo`,\n" +
                        "`cardHolder`,\n" +
                        "`cardNo`,\n" +
                        "`bankId`,\n" +
                        "`cardActType`,\n" +
                        "`cardProvince`,\n" +
                        "`cardCity`,\n" +
                        "`attach`,\n" +
                        "`reference`,\n" +
                        "`createTime`,\n" +
                        "`expireTime`,\n" +
                        "`isUsePwd`,\n" +
                        "`channelId`,\n" +
                        "`businessType`,\n" +
                        "`orderState`,\n" +
                        "`orderStateDesc`,\n" +
                        "`orderId`,\n" +
                        "`tradeOrderId`," +
                        " `realName`)\n" +
                        "VALUES\n" +
                        "(" +
                         "?,?,?,?,?,"+
                        "?,?,?,?,?,"+
                        "?,?,?,?,?,"+
                        "?,?,?,?,?,"+
                        "?,?,?,?"+
                         ")";

                PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                int i = 1;
                ps.setString(i++, order.getFromUserCode());
                ps.setString(i++, order.getToUserCode());
                ps.setLong(i++, order.getAmount());
                ps.setInt(i++, order.getPayType());
                ps.setInt(i++, order.getReceiverType());
                ps.setString(i++, order.getBindNo());
                ps.setString(i++, order.getCardHolder());
                ps.setString(i++, order.getCardNo());
                ps.setString(i++, order.getBankId());
                ps.setInt(i++, order.getCardActType());
                ps.setString(i++, order.getCardProvince());
                ps.setString(i++, order.getCardCity());
                ps.setString(i++, order.getAttach());
                ps.setString(i++, order.getReference());
                ps.setDate(i++, new Date(order.getCreateTime().getTime()));
                ps.setDate(i++, new Date(order.getExpireTime().getTime()));
                ps.setInt(i++, order.getIsUsePwd());
                ps.setString(i++, order.getChannelId());
                ps.setString(i++, order.getBusinessType());
                ps.setInt(i++, order.getOrderState());
                ps.setString(i++, order.getOrderStateDesc());
                ps.setString(i++, order.getOrderId());
                ps.setString(i++, order.getTradeOrderId());
                ps.setString(i++, order.getRealName());

                return ps;
            }
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        order.setId(id);

        return order;
    }

    public String hello(){
        return slaveTemplate.queryForObject("select now()",String.class);
    }

}
