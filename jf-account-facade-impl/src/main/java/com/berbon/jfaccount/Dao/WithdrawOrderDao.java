package com.berbon.jfaccount.Dao;

import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by chj on 2016/8/16.
 */
public class WithdrawOrderDao {
    private static Logger logger = LoggerFactory.getLogger(WithdrawOrderDao.class);

    @Autowired
    private JdbcTemplate masterTemplate;
    @Autowired
    private JdbcTemplate slaveTemplate;





}
