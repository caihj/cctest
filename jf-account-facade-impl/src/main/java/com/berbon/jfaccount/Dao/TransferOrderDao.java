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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static enum OrderState {
        wait_pay(1,"待支付"),
        paying(2,"支付中"),
        succ(3,"成功"),
        fail(4,"失败"),
        exception(5,"异常");
        OrderState(int state,String desc){
            this.state = state;
            this.desc = desc;
        }
        public int state;
        public String desc;
    }

    /**
     *
     * @return
     */
    public static  OrderState GetState(String state){
        if(state.equals("1")){
            return OrderState.wait_pay;
        }else if(state.equals("2")){
            return OrderState.paying;
        }
        else if(state.equals("3")){
            return OrderState.succ;
        }
        else if(state.equals("4")){
            return OrderState.fail;
        }
        else if(state.equals("5")){
            return OrderState.exception;
        }else{
            return OrderState.exception;
        }
    }

    public TransferOrderInfo createOrder(final TransferOrderInfo order){

        KeyHolder keyHolder = new GeneratedKeyHolder();


        masterTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO `account_transfer_order`\n" +
                        "(`fromUserCode`,\n" +
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
                        "`phone`," +
                        "`createUserCode`)\n" +
                        "VALUES\n" +
                        "(" +
                         "?,?,?,?,?,"+
                        "?,?,?,?,?,"+
                        "?,?,?,?,?,"+
                        "?,?,?,?,?,"+
                        "?,?,?,?,?,"+
                         "?)";

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
                if(order.getCreateTime()!=null)
                    ps.setTimestamp(i++, new Timestamp(order.getCreateTime().getTime()));
                else
                    ps.setTimestamp(i++, null);

                if(order.getExpireTime()!=null)
                    ps.setTimestamp(i++, new Timestamp(order.getExpireTime().getTime()));
                else
                    ps.setTimestamp(i++, null);

                ps.setInt(i++, order.getIsUsePwd());
                ps.setString(i++, order.getChannelId());
                ps.setString(i++, order.getBusinessType());
                ps.setInt(i++, order.getOrderState());
                ps.setString(i++, order.getOrderStateDesc());
                ps.setString(i++, order.getOrderId());
                ps.setString(i++, order.getTradeOrderId());
                ps.setString(i++, order.getRealName());
                ps.setString(i++, order.getPhone());
                ps.setString(i++, order.getCreateUserCode());
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

    public TransferOrderInfo getByTradeOrderId(String TradeorderNo){
        String sql = "select * from account_transfer_order where `tradeOrderId`=\""+TradeorderNo+"\"";
        TransferOrderInfo orderInfo;
        try{
            orderInfo  = slaveTemplate.queryForObject(sql,new BaseMapper<TransferOrderInfo>(TransferOrderInfo.class));
        }catch (Exception e){
            return  null;
        }
        return  orderInfo;
    }

    public PageResult<TransferOrderInfo>  queryTransferOrder(int pageNo,int pageSize, java.util.Date startTime, java.util.Date endTime,String orderId,String userCode){

        PageResult<TransferOrderInfo> result = new PageResult<TransferOrderInfo>();
        String sql = "select * from account_transfer_order where (`fromUserCode`=\""+userCode+"\" or (`toUserCode`=\""+userCode +"\" and orderState=3 ) ) " ;
        String countSql = "select count(*) from account_transfer_order where (`fromUserCode`=\""+userCode+"\" or (`toUserCode`=\""+userCode +"\" and orderState=3)) " ;
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
                sql +=" and createTime >=\""+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime)+"\"";
                countSql +=" and createTime >=\""+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime)+"\"";
            }

            if(endTime!=null){
                sql +=" and createTime <=\""+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime)+"\"";
                countSql +=" and createTime <=\""+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime)+"\"";
            }

            logger.info("查询转账 countSql"+countSql);
            result.total =  slaveTemplate.queryForObject(countSql, Integer.class);
            result.pageNo = pageNo;
            result.pageSize = pageSize;

            int start = (pageNo-1)*pageSize;

            if(start >= result.total){
                return result;
            }

            sql  +=" order by id desc ";
            sql +=" limit "+start+","+pageSize;
            logger.info("查询转账 sql"+sql);

            result.listData = slaveTemplate.query(sql, new BaseMapper<TransferOrderInfo>(TransferOrderInfo.class));


        }
        return result;
    }


    public int update(final long id, final int state, final String stateDesc,final String tradeOderNo,final int payType,final String bindNo){

        return masterTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("update account_transfer_order set tradeOrderId=?,orderState=?," +
                        "orderStateDesc=?,editTime=?,payType=? where id=?");

                int i=1;

                ps.setString(i++, tradeOderNo);
                ps.setInt(i++, state);
                ps.setString(i++, stateDesc);
                ps.setTimestamp(i++, new Timestamp(new java.util.Date().getTime()));
                ps.setInt(i++, payType);

                ps.setLong(i++,id);

                return ps;
            }
        });


    }


    public int update(final long id, final int state, final String stateDesc){

        return masterTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("update account_transfer_order set orderState=?," +
                        "orderStateDesc=?,editTime=? where id=?");

                int i=1;


                ps.setInt(i++, state);
                ps.setString(i++, stateDesc);
                ps.setTimestamp(i++, new Timestamp(new java.util.Date().getTime()));

                ps.setLong(i++,id);
                return ps;
            }
        });

    }


    /**
     * 获取一定时间，指定账号的转入量
     * @return
     */
    public long getTotalAmount(Date beginTime,Date endTime,String fromUserCode,String toUserCode,String bussType){

        return masterTemplate.queryForLong("SELECT sum(amount) FROM account_transfer_order\n" +
                "where fromUserCode=? and toUserCode=? and createTime between ? and ? and businessType=?",
                new Object[]{fromUserCode,toUserCode,beginTime,endTime,bussType});
    }




}
