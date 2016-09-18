package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.facade.common.PageResult;
import com.berbon.jfaccount.pojo.MobieChargeOrderInfo;
import com.berbon.jfaccount.pojo.UserActFlow;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chj on 2016/9/14.
 */

@Component
public class UserActFlowDao {


    private static Logger logger = LoggerFactory.getLogger(UserActFlowDao.class);

    @Autowired
    private JdbcTemplate newpayslaveTemplate;



    public long getCount(Date startDate,Date endDate,String userid,String orderNo){
        assert (startDate!=null);
        assert (endDate!=null);
        assert (userid!=null);


        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String params [] = new String[] {userid,sd.format(startDate),sd.format(endDate)};

        String sql = String.format("select count(*) as total from t_user_act_flow_view where user_id='%s' and create_time BETWEEN '%s'and '%s' ",params);

        if(orderNo!=null && orderNo.trim().isEmpty()==false){
            sql += " and pay_no='"+orderNo+"'";
        }

        logger.info("sql is "+sql);
        Integer total = newpayslaveTemplate.queryForObject(sql, Integer.class);
        return total;

    }


    /**
     *

     * @param startDate
     * @param endDate
     * @param userid
     * @param orderNo
     * @return
     */
    public List<UserActFlow> query(int start,int count,Date startDate,Date endDate,String userid,String orderNo){

        assert (startDate!=null);
        assert (endDate!=null);
        assert (userid!=null);

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String params [] = new String[] {userid,sd.format(startDate),sd.format(endDate),orderNo};

        String sql = String.format("select count(*) as total from t_user_act_flow_view where user_id='%s' and create_time BETWEEN '%s'and '%s' ",params);

        if(orderNo!=null && orderNo.trim().isEmpty()==false){
            sql += " and pay_no='"+orderNo+"'";
        }

        logger.info("sql is "+sql);

        Integer Totalcount = newpayslaveTemplate.queryForObject(sql, Integer.class);
        if(Totalcount==0){
            return null;
        }


        if(start>=Totalcount){
            return  null;
        }

        Object dataParms [] = new Object[] {userid,sd.format(startDate),sd.format(endDate)};

        String dataSql = String.format("select * from t_user_act_flow_view where user_id='%s' and create_time BETWEEN '%s' and '%s'",dataParms);

        if(orderNo!=null && orderNo.trim().isEmpty()==false){
            dataSql +=String.format("and pay_no='%s'",orderNo);
        }

        dataSql +=String.format(" order by create_time desc limit %d,%d", start,count);

        logger.info("sql is "+dataSql);

        List<UserActFlow> flows= newpayslaveTemplate.query(dataSql, new RowMapper<UserActFlow>() {
           @Override
           public UserActFlow mapRow(ResultSet rs, int rowNum) throws SQLException {
               UserActFlow info = new UserActFlow();

               Field[] fields = UserActFlow.class.getDeclaredFields();
               try {
                   for (Field field : fields) {
                       field.setAccessible(true);
                       Class<?> type = field.getType();

                       if (type.equals(int.class) || type.equals(Integer.class)) {
                           field.set(info, rs.getInt(field.getName()));
                       } else if (type.equals(long.class) || type.equals(Long.class)) {
                           field.set(info, rs.getLong(field.getName()));
                       } else if (type.equals(String.class)) {
                           field.set(info, rs.getString(field.getName()));
                       } else if (type.equals(Timestamp.class)) {
                           field.set(info, rs.getTimestamp(field.getName()));
                       }
                   }
               } catch (IllegalAccessException e) {
                   logger.error("反射异常");
               }

               return info;
           }
        });

        return flows;

    }


}
