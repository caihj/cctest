package com.berbon.jfaccount.facade;

import com.berbon.jfaccount.facade.pojo.*;

/**
 * Created by chj on 2016/8/5.
 */
public interface AccountFacade {


    /**
     * 创建快捷支付订单
     */
     ChargeOrderInfo createChargeQuckPay(ChargeReqData data);


    /**
     * 快捷支付短信验证
     * @param in
     * @return
     */

    QuickPayValRsp validateQuickPayMsg(QuickPayMsgInfoReq data);


     String echo(String in);


    /**
     * 检查是否可以向指定用户转账
     */

    TransferCheckRsp checkCanTransferTo(TransferCheckReq data);


    /**
     * 创建转账订单
     */

    TransferOrderCrtRsp createTransferOrder(TransferOrderCrtReq req);

}
