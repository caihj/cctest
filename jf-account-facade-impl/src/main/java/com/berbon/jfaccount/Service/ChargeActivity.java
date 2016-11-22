package com.berbon.jfaccount.Service;

import com.berbon.jfaccount.Dao.ChargeOrderDao;
import com.berbon.jfaccount.Dao.TransferOrderDao;
import com.berbon.jfaccount.comm.BusinessType;
import com.berbon.jfaccount.comm.InitBean;
import com.berbon.jfaccount.facade.pojo.ChargeOrderInfo;
import com.berbon.jfaccount.facade.pojo.TransferOrderInfo;
import com.berbon.jfaccount.util.UtilTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by chj on 2016/11/21.
 */

@Service
public class ChargeActivity {

    private static Logger logger = LoggerFactory.getLogger(ChargeActivity.class);

    @Autowired
    private ChargeOrderDao chargeOrderDao;
    @Autowired
    private TransferOrderDao transferOrderDao;
    @Autowired
    private InitBean initBean;

    /**
     * 当充值成功后，回调此函数
     * @param chargeOrderId 充值成功的订单号
     */
    public void onChargeSuccAction(String chargeOrderId){

        ChargeOrderInfo orderInfo = chargeOrderDao.getByBusOrderNo(chargeOrderId);
        if(orderInfo==null){
            logger.error("未查到订单");
        }

        //fixme 此处时间要询问。
        Date orderTime  = orderInfo.getCreateTime();

        long giveMoney = 0;

        if(orderInfo.getAmount()>=5000*100 &&  orderInfo.getAmount()<=10000*100){
            giveMoney = orderInfo.getAmount()/1000;
        }else if(orderInfo.getAmount()>10000*100){
            giveMoney = orderInfo.getAmount()/500;
        }

        if(giveMoney>0){
            logger.info("需要赠送加款活动费:{}分", giveMoney);

            TransferOrderInfo transOrder= new TransferOrderInfo();

            transOrder.setOrderId(UtilTool.generateTransferOrderId());
            transOrder.setFromUserCode(initBean.activityGiveMoneyFineUserCode);
            transOrder.setToUserCode(orderInfo.getChargeUserCode());
            transOrder.setAmount(giveMoney);

            transOrder.setReceiverType(1);

            transOrder.setAttach("活动赠送");
            //transOrder.setRealName("");
            //transOrder.setPhone();
            transOrder.setCreateTime(new Date());
            Calendar cal = Calendar.getInstance();
            cal.setTime(orderInfo.getCreateTime());
            cal.add(Calendar.SECOND, initBean.maxChargeOrderAliveSec);
            transOrder.setExpireTime(cal.getTime());

            transOrder.setChannelId(initBean.channelId);
            transOrder.setBusinessType(BusinessType.type_2014.type + "");

            transOrder.setOrderState(TransferOrderDao.OrderState.wait_pay.state);
            transOrder.setOrderStateDesc(TransferOrderDao.OrderState.wait_pay.desc);
            transOrder.setCreateUserCode(initBean.activityGiveMoneyFineUserCode);

            transOrder = transferOrderDao.createOrder(transOrder);

        }


    }

}
