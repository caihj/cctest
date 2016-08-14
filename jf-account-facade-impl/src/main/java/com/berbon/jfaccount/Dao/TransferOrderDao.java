package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.facade.common.PageResult;
import com.berbon.jfaccount.facade.pojo.TransferOrderInfo;
import com.berbon.util.mapper.BaseMapper;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
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
import java.text.SimpleDateFormat;

/**
 * Created by chj on 2016/8/10.
 */

@Component
public class TransferOrderDao  {

    private static Logger logger = LoggerFactory.getLogger(TransferOrderDao.class);

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
                        " `realName`," +
                        "`phone`)\n" +
                        "VALUES\n" +
                        "(" +
                         "?,?,?,?,?,"+
                        "?,?,?,?,?,"+
                        "?,?,?,?,?,"+
                        "?,?,?,?,?,"+
                        "?,?,?,?,?"+
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
                ps.setString(i++, order.getPhone());
                return ps;
            }
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        order.setId(id);

        return order;
    }


    public TransferOrderInfo getByOrderId(String orderId){
        String sql = "select * from account_transfer_order where `orderId`=\""+orderId+"\"";
        TransferOrderInfo orderInfo;
        try{
            orderInfo  = slaveTemplate.queryForObject(sql,new BaseMapper<TransferOrderInfo>(TransferOrderInfo.class));
        }catch (Exception e){
            return  null;
        }
        return  orderInfo;
    }

    public PageResult<TransferOrderInfo>  queryTransferOrder(int pageNo,int pageSize, java.util.Date startTime, java.util.Date endTime,String orderId){

        PageResult<TransferOrderInfo> result = new PageResult<TransferOrderInfo>();
        String sql = "select * from account_transfer_order where 1=1 ";
        String countSql = "select count(*) from account_transfer_order where 1=1 ";
        if(orderId!=null && orderId.trim().isEmpty()==false){
            sql +=" and `orderId`=\""+orderId+"\"";

            result.listData = slaveTemplate.query(sql, new BaseMapper<TransferOrderInfo>(TransferOrderInfo.class));
            result.pageNo = 1;
            result.pageSize = 1;
            if(result.listData!=null && result.listData.size()>0){
                result.total = result.listData.size();
            }

        }else{

            if(startTime!=null){
                sql +=" and createTime >="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime);
                countSql +=" and createTime >="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime);
            }

            if(endTime!=null){
                sql +=" and createTime <="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime);
                countSql +=" and createTime <="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime);
            }

            result.total =  slaveTemplate.queryForObject(countSql, Integer.class);
            result.pageNo = pageNo;
            result.pageSize = pageSize;

            int start = (pageNo-1)*pageSize;

            if(start >= result.total){
                return result;
            }

            sql +=" limit "+start+","+pageSize;

            result.listData = slaveTemplate.query(sql, new BaseMapper<TransferOrderInfo>(TransferOrderInfo.class));


        }
        return result;
    }

}
