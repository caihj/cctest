package com.berbon.jfaccount.facade;

import com.berbon.jfaccount.facade.common.PageResult;
import com.berbon.jfaccount.facade.pojo.*;

import java.util.Date;

/**
 * Created by chj on 2016/8/5.
 */
public interface AccountFacade {


    /**
     * 创建快捷支付订单
     */
     ChargeOrderInfo createChargeQuckPay(CreateChargeReq data);


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
     * 创建充值订单，并充值
     * @param req
     * @return
     */
    CreateChargeRsp createChargeOrder(CreateChargeReq req);


    /**
     * 创建转账订单
     */

    TransferOrderCrtRsp createTransferOrder(TransferOrderCrtReq req);


    /**
     * 查询转账订单
     */
    PageResult<TransferOrderInfo> queryTransferOrder(int pageNo,int pageSize,Date startTime,Date endTime,String orderId);

    /**
     * 查询转账订单
     */

    TransferOrderInfo queryTransferDetail(String orderId);

    /**
     * 支付转账订单，调用接口转账
     */
    TransferOrderPayResp payTransferOrder(TransferOrderPayReq pay);

}
