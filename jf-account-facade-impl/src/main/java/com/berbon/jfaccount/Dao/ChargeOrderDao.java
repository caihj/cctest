package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.facade.pojo.ChargeOrderInfo;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import com.sztx.util.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by chj on 2016/8/5.
 */
@Component
public class ChargeOrderDao {

    private static Logger logger = LoggerFactory.getLogger(ChargeOrderDao.class);

    @Autowired
    private JdbcTemplate masterTemplate;
    @Autowired
    private JdbcTemplate slaveTemplate;

    public ChargeOrderInfo newOrder(final ChargeOrderInfo info){

        KeyHolder keyHolder = new GeneratedKeyHolder();

        final String sql = "INSERT INTO `account_charge_order`\n" +
                "("+
                "`chargeBussOrderNo`,\n" +
                "`createTime`,\n" +
                "`chargeUserCode`,\n" +
                "`amount`,\n" +
                "`channelId`,\n" +
                "`srcChannel`,\n" +
                "`state`,\n" +
                "`stateDesc`,\n" +
                "`callbackTime`,\n" +
                "`tradeOrderId`,\n" +
                "`payValue`,\n" +
                "`bankId`,\n" +
                "`attach`,\n" +
                "`bindNo`,\n" +
                "`cardNo`,\n" +
                "`type`,\n" +
                "`cardType`,\n" +
                "`cvv`,\n" +
                "`expireDate`,\n" +
                "`realName`,\n" +
                "`identityNo`,\n" +
                "`mobileNo`)\n" +
                "VALUES\n" +
                "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)\n";

        masterTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con)
                    throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                int i = 1;
                ps.setString(i++, info.getChargeBussOrderNo());
                ps.setDate(i++, new Date(info.getCreateTime().getTime()));
                ps.setString(i++, info.getChargeUserCode());
                ps.setLong(i++, info.getAmount());
                ps.setInt(i++, info.getChannelId());

                ps.setInt(i++, info.getSrcChannel());
                ps.setInt(i++, info.getState());
                ps.setString(i++, info.getStateDesc());
                ps.setDate(i++, new Date(info.getCallbackTime().getTime()));
                ps.setString(i++, info.getTradeOrderId());

                ps.setLong(i++, info.getPayValue());
                ps.setString(i++, info.getBankId());
                ps.setString(i++, info.getAttach());
                ps.setString(i++, info.getBindNo());
                ps.setString(i++, info.getCardNo());
                ps.setInt(i++, info.getType());
                ps.setInt(i++, info.getCardType());
                ps.setString(i++, info.getCvv());
                ps.setString(i++, info.getExpireDate());
                ps.setString(i++, info.getRealName());
                ps.setString(i++, info.getIdentityNo());
                ps.setString(i++, info.getMobileNo());
                return ps;
            }
        }, keyHolder);

        long id= keyHolder.getKey().longValue();
        info.setId(id);

        return info;
    }

    public String hello(){
        return slaveTemplate.queryForObject("select now()",String.class);
    }


    public ChargeOrderInfo getByeTradeOrderNo(String tradeOrderId){

        String sql = "select * from account_charge_order where tradeOrderId=\""+tradeOrderId+"\"";

        ChargeOrderInfo order = slaveTemplate.queryForObject(sql,new BaseMapper<ChargeOrderInfo>(ChargeOrderInfo.class));

        return order;
    }

}
