package com.berbon.jfaccount.impl;

import com.berbon.jfaccount.Dao.ChargeOrderDao;
import com.berbon.jfaccount.facade.AccountFacade;
import com.berbon.jfaccount.facade.pojo.ChargeOrderInfo;
import com.berbon.jfaccount.facade.pojo.ChargeReqData;
import com.berbon.jfaccount.util.UtilTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by chj on 2016/8/5.
 */

@Service
public class AccountFacadeImpl implements AccountFacade {


    @Autowired
    private ChargeOrderDao dao;

    @Override
    public ChargeOrderInfo createChargeQuckPay(ChargeReqData data) {

        String orderId = UtilTool.createOrderId(UtilTool.ChargeOrderPrefix);

        ChargeOrderInfo info = new ChargeOrderInfo();

        info.setChargeBussOrderNo(orderId);
        info.setBankId(data.getBankId());
        info.setAmount(data.getAmount());
        info.setCreateTime(new Date());
        info.setSrcChannel(data.getSrcChannel());
        info.setChannelId(0);
        info.setState(0);
        info.setStateDesc("初始态");
        info.setAttach(data.getAttach());
        info.setBindNo(data.getBindNo());
        info.setCardNo(data.getBindNo());


        info.setType(data.getType());
        info.setCardType(data.getCardType());
        info.setCvv(data.getCvv());
        info.setExpireDate(data.getExpireDate());
        info.setRealName(data.getRealName());
        info.setIdentityNo(data.getIdentityNo());
        info.setMobileNo(data.getMobileNo());


        info = dao.newOrder(info);

        return info;
    }
}
