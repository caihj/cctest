package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.facade.pojo.UserBaseInfo;
import com.berbon.util.mapper.BaseMapper;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by chj on 2016/11/10.
 */

@Service
public class UserBaseInfoDao {

    private static Logger logger = LoggerFactory.getLogger(UserBaseInfoDao.class);

    @Autowired
    private JdbcTemplate newpayslaveTemplate;

    @Autowired
    private JdbcTemplate newpayMasterTemplate;


    /**
     * 检查用户是否进行了实名认证
     * @return
     */

    public boolean checkisAuth(String userCode){

        UserBaseInfo info = null;
        try {
            info = newpayMasterTemplate.queryForObject(" select real_name_verified  as realNameVerified FROM newpay.UserShare  " +
                            "join UserBaseInfo on UserShare.ub_id=UserBaseInfo.id  where partner_user_id=? ",
                    new Object[]{userCode}, new BaseMapper<UserBaseInfo>(UserBaseInfo.class));
        }catch (Exception e){
            logger.error("数据异常"+e);
            return  false;
        }

        if(info!=null){
            if(info.getRealNameVerified()==1)
                return true;
            else
                return false;
        }

        return false;
    }


    /**
     *获取用户信息， 真实姓名和身份证号,是否实名认证
     */
    public UserBaseInfo getPartInfo(String userCode){

        try{
            UserBaseInfo  info  = newpayMasterTemplate.queryForObject(" select UserBaseInfo.id,real_name as realName, identity_num as identityNum ,real_name_verified  as realNameVerified FROM newpay.UserShare  " +
                            "join UserBaseInfo on UserShare.ub_id=UserBaseInfo.id  where partner_user_id=? ",
                new Object[]{userCode}, new BaseMapper<UserBaseInfo>(UserBaseInfo.class));
            return info;
        }catch (Exception e){
            logger.error(e);
            return null;
        }

    }

    /**
     * 让某个用户通过实名认证
     *
     * @param id
     * @param identificate_type  实名认证方式 0-默认 1-快捷 2-打款
     */
    public void makeUserVerify(Long id,int identificate_type){
        try{
            int a= newpayMasterTemplate.update(" update UserBaseInfo set real_name_verified=?,identificate_type=?,verify_time=now() where id=? ",
                    new Object[]{1,identificate_type,id});
        }catch (Exception e){
            logger.error(e);
        }
    }






}
