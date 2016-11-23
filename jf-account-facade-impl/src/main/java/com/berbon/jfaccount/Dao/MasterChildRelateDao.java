package com.berbon.jfaccount.Dao;

import com.berbon.jfaccount.pojo.MasterChildRelate;
import com.berbon.util.mapper.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by chj on 2016/11/22.
 */
@Component
public class MasterChildRelateDao {

    private static Logger logger = LoggerFactory.getLogger(MasterChildRelateDao.class);

    @Autowired
    private JdbcTemplate masterTemplate;
    @Autowired
    private JdbcTemplate slaveTemplate;

    public List<MasterChildRelate> get (String childUserCode) throws DataAccessException {
        List<MasterChildRelate> relate = slaveTemplate.query("select * from account_master_child_relate where state=1 and childUserCode=?",
                new Object[]{childUserCode}, new BaseMapper<MasterChildRelate>(MasterChildRelate.class));
        return  relate;
    }

}
