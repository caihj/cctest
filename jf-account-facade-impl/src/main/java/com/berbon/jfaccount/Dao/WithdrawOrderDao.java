package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.facade.pojo.WithdrawOrderInfo;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by chj on 2016/8/16.
 */
@Component
public class WithdrawOrderDao {
    private static Logger logger = LoggerFactory.getLogger(WithdrawOrderDao.class);

    @Autowired
    private JdbcTemplate masterTemplate;
    @Autowired
    private JdbcTemplate slaveTemplate;


    public static enum OrderState{
        init(0,"初始态"),
        succ(1,"成功"),
        fail(2,"失败"),
        exception(3,"异常");

        OrderState(int state,String desc){
            this.state = state;
            this.desc = desc;
        }
        public int state;
        public String desc;
    }


    public WithdrawOrderInfo newOrder(final WithdrawOrderInfo orderInfo){

        masterTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

                String sql= "INSERT INTO `account_withdraw_order`\n" +
                        "(\n" +
                        "`usercode`,\n" +
                        "`amount`,\n" +
                        "`bindNo`,\n" +
                        "`cardNo`,\n" +
                        "`createTime`,\n" +
                        "`state`,\n" +
                        "`stateDesc`,\n" +
                        "`resultTime`)\n" +
                        "VALUES\n" +
                        "(\n" +
                        "?,\n" +
                        "?,\n" +
                        "?,\n" +
                        "?,\n" +
                        "?,\n" +
                        "?,\n" +
                        "?,\n" +
                        "?);\n";

                PreparedStatement preparedStatement = con.prepareStatement("sql");

                int i=1;
                preparedStatement.setString(i++, orderInfo.getUsercode());
                preparedStatement.setLong(i++, orderInfo.getAmount());
                preparedStatement.setString(i++,orderInfo.getBindNo());
                preparedStatement.setString(i++, orderInfo.getCardNo());

                preparedStatement.setTimestamp(i++, new Timestamp(orderInfo.getCreatetime().getTime()));
                preparedStatement.setInt(i++, orderInfo.getState());
                preparedStatement.setString(i++, orderInfo.getStateDesc());
                preparedStatement.setTimestamp(i++, new Timestamp(orderInfo.getResultTime().getTime()));


                return preparedStatement;
            }
        });

        return orderInfo;
    }


}
