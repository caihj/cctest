package com.berbon.jfaccount.impl;

import com.berbon.jfaccount.facade.AccountMobileFacade;
import com.berbon.jfaccount.facade.mobpojo.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by chj on 2016/8/10.
 */
@Service("accountMobileFacadeIMpl")
public class AccountMobileFacadeIMpl implements AccountMobileFacade {
    @Override
    public String echo(String in) {
        return in;
    }

    @Override
    public CreateChargeRsp createChargeOrder(CreateChargeReq req) {
        return null;
    }

    @Override
    public GetValMsgRsp getChargeValMsg(GetValMsgReq req) {
        return null;
    }

    @Override
    public GetValMsgRsp getChargeValMsg(String orderId, String ip) {
        return null;
    }

    @Override
    public ValSmsgRsp valChargeSMsg(String orderId, String verifyCode, String ip) {
        return null;
    }

    @Override
    public CreateTransferOrderRsp creatTransferOrder(CreateTransferOrderReq req) {
        return null;
    }

    @Override
    public DoTransferRsp doTransfer(String orderId, String ip) {
        return null;
    }

    @Override
    public boolean queryCanWithdrawals(String userCode) {
        return false;
    }

    @Override
    public List<BankCardInfo> queryWithdrawalBankCard(String userCode) {
        return null;
    }

    @Override
    public WithdrawalRsp DowithDrawal(WithdrawalReq req) {
        return null;
    }

    @Override
    public long queryBalance(String userCode) {
        return 0;
    }

    @Override
    public BalancePayRsp BalancePay(String orderId, String ip) {
        return null;
    }

    @Override
    public QuickPayRsp quickPay(String orderId, String ip, String bindNo) {
        return null;
    }

    @Override
    public void quickPayReGetMsg(String orderId, String ip) {

    }

    @Override
    public void quickPayValMsg(String orderId, String verifyCode, String ip) {

    }
}
