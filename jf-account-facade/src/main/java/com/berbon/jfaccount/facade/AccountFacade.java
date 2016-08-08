package com.berbon.jfaccount.facade;

import com.berbon.jfaccount.facade.pojo.ChargeOrderInfo;
import com.berbon.jfaccount.facade.pojo.ChargeReqData;

/**
 * Created by chj on 2016/8/5.
 */
public interface AccountFacade {


    /**
     * 创建快捷支付订单
     * bankNo 支付银行编号，不是银行卡号
     * amount,金额，单位分
     */
     ChargeOrderInfo createChargeQuckPay(ChargeReqData data);

}
