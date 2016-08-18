package com.berbon.jfaccount.facade;

import com.berbon.jfaccount.facade.mobpojo.*;

import java.util.List;

/**
 * Created by chj on 2016/8/10.
 */
public interface AccountMobileFacade {

    String echo(String in);

    /**
     * 充值
     */
     CreateChargeRsp createChargeOrder(CreateChargeReq req);

    /**
     * 获取验证码
     */
     GetValMsgRsp getChargeValMsg(GetValMsgReq req);

    /**
     *再次获取短信验证码
     */

    GetValMsgRsp getChargeValMsg(String orderId,String ip);

    /**
     * 验证短信验证码
     */
    ValSmsgRsp valChargeSMsg(String orderId,String verifyCode,String ip);


    /**
     * 创建转账订单
     */
    CreateTransferOrderRsp creatTransferOrder(CreateTransferOrderReq req);


    /**
     *确认转账
     */
    DoTransferRsp doTransfer(String orderId,String ip);


    /**
     * 查询是否可以提现
     */
    boolean queryCanWithdrawals(String userCode);


    /**
     * 查询提现银行卡
     */
    List<BankCardInfo> queryWithdrawalBankCard(String userCode);


    /**
     * 执行提现操作
     */
    WithdrawalRsp DowithDrawal(WithdrawalReq req);


    /**
     * 查询余额
     */
    long queryBalance(String userCode);


    /**
     * 余额支付-缴费订单
     */
    BalancePayRsp BalancePay(String orderId,MobOrderType type,String ip);

    /**
     * 快捷支付-缴费订单
     */

    QuickPayRsp quickPay(String orderId,MobOrderType type,String ip,String bindNo);

    /**
     * 快捷支付-重新获取验证码
     */
    void quickPayReGetMsg(String orderId,MobOrderType type,String ip);

    /**
     * 快捷支付-验证验证码
     */
    void quickPayValMsg(String orderId,MobOrderType type,String verifyCode,String ip);

}
