package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.facade.pojo.ChargeOrderInfo;
import com.berbon.jfaccount.util.Pair;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import com.sztx.util.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.sql.*;

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

    public static Pair<Integer,String> ChargeCodeToState(String code){

        Pair<Integer,String> t0 = new Pair<Integer,String>(0,"初始态");
        Pair<Integer,String> t1 = new Pair<Integer,String>(1,"充值中");
        Pair<Integer,String> t2 = new Pair<Integer,String>(2,"成功");
        Pair<Integer,String> t3 = new Pair<Integer,String>(3,"失败");
        Pair<Integer,String> t4 = new Pair<Integer,String>(4,"未知");

        Object [] arr =new Object[]{t0,t1,t2,t3,t4};

        int iCode=4;

        try {
            iCode = Integer.parseInt(code);
        }catch (Exception e){
            logger.error("发生异常，未识别的订单号+"+code);
        }

        return (Pair)arr[iCode-1];
    }

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
                "`mobileNo`," +
                "`fromIp`," +
                "`fromMob`)\n" +
                "VALUES\n" +
                "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)\n";

        masterTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con)
                    throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                int i = 1;
                ps.setString(i++, info.getChargeBussOrderNo());
                ps.setTimestamp(i++, new Timestamp(info.getCreateTime().getTime()));
                ps.setString(i++, info.getChargeUserCode());
                ps.setLong(i++, info.getAmount());
                ps.setInt(i++, info.getChannelId());

                ps.setInt(i++, info.getSrcChannel());
                ps.setInt(i++, info.getState());
                ps.setString(i++, info.getStateDesc());

                if(info.getCallbackTime()!=null)
                    ps.setTimestamp(i++, new Timestamp(info.getCallbackTime().getTime()));
                else
                    ps.setTimestamp(i++, null);

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
                ps.setString(i++,info.getFromIp());
                ps.setString(i++,info.getFromMob());
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

    public ChargeOrderInfo getByBusOrderNo(String chargeBussOrderNo){

        String sql = "select * from account_charge_order where chargeBussOrderNo=\""+chargeBussOrderNo+"\"";

        ChargeOrderInfo order = slaveTemplate.queryForObject(sql,new BaseMapper<ChargeOrderInfo>(ChargeOrderInfo.class));

        return order;
    }


    public int update(final long id, final String tradeOderNo, final int state, final String stateDesc, final int cardType,final int type
    ,final String bindNo,final long payValue){

        return masterTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("update account_charge_order set tradeOrderId=?,state=?," +
                        "stateDesc=?,callbackTime=?,payValue=?,bindNo=?,type=?,cardType=? where id=?");


                int i=1;
                ps.setString(i++, tradeOderNo);
                ps.setInt(i++, state);
                ps.setString(i++, stateDesc);
                ps.setTimestamp(i++, new Timestamp(new java.util.Date().getTime()));

                ps.setLong(i++,payValue);
                ps.setString(i++,bindNo);
                ps.setInt(i++, type);
                ps.setInt(i++, cardType);


                ps.setLong(i++,id);

                return ps;
            }
        });
    }

    public int update(final long id, final int state, final String stateDesc) {

        return masterTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("update account_charge_order set state=?," +
                        "stateDesc=?,callbackTime=? where id=?");

                int i=1;
                ps.setInt(i++, state);
                ps.setString(i++, stateDesc);
                ps.setTimestamp(i++, new Timestamp(new java.util.Date().getTime()));

                ps.setLong(i++,id);

                return ps;
            }
        });
    }
}
