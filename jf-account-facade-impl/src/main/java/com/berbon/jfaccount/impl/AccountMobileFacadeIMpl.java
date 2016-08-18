package com.berbon.jfaccount.impl;

import com.berbon.jfaccount.Dao.ChargeOrderDao;
import com.berbon.jfaccount.Dao.TransferOrderDao;
import com.berbon.jfaccount.Dao.WithdrawOrderDao;
import com.berbon.jfaccount.Service.SignService;
import com.berbon.jfaccount.comm.ErrorCode;
import com.berbon.jfaccount.comm.InitBean;
import com.berbon.jfaccount.facade.AccountMobileFacade;
import com.berbon.jfaccount.facade.mobpojo.*;
import com.berbon.jfaccount.facade.pojo.ChargeOrderInfo;
import com.berbon.jfaccount.facade.pojo.QuickPayValRsp;
import com.berbon.jfaccount.facade.pojo.TransferOrderInfo;
import com.berbon.jfaccount.facade.pojo.WithdrawOrderInfo;
import com.berbon.jfaccount.util.Pair;
import com.berbon.jfaccount.util.UtilTool;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import com.sztx.pay.center.rpc.api.domain.*;
import com.sztx.pay.center.rpc.api.service.AccountRpcService;
import com.sztx.pay.center.rpc.api.service.TradeRpcService;
import com.sztx.pay.center.rpc.api.service.WithdrawAuthRpcService;
import com.sztx.se.common.exception.BusinessException;
import com.sztx.se.rpc.dubbo.source.DynamicDubboClient;
import com.sztx.usercenter.rpc.api.service.QueryUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chj on 2016/8/10.
 */
@Service("accountMobileFacadeIMpl")
public class AccountMobileFacadeIMpl implements AccountMobileFacade {


    private static Logger logger = LoggerFactory.getLogger(AccountFacadeImpl.class);

    @Autowired
    private InitBean initBean;

    @Autowired
    private ChargeOrderDao chargeOrderDao;

    @Autowired
    private TransferOrderDao transferOrderDao;

    @Autowired
    private DynamicDubboClient dubboClient;

    @Autowired
    private SignService signService;

    @Autowired
    private WithdrawOrderDao withdrawOrderDao;

    @Override
    public String echo(String in) {
        return in;
    }

    @Override
    public CreateChargeRsp createChargeOrder(CreateChargeReq req) {

        CreateChargeRsp rsp = new CreateChargeRsp();

        ChargeOrderInfo order = new ChargeOrderInfo();

        order.setChargeBussOrderNo(UtilTool.generateChargeOrderId());

        order.setCreateTime(new Date());
        order.setChargeUserCode(req.getUserCode());
        order.setAmount(req.getPayAmount());
        order.setChannelId(0);
        order.setSrcChannel(2);
        order.setState(0);
        order.setStateDesc("初始态");

        order.setFromIp(req.getFromIp());
        order.setFromMob(req.getFromMob());

        order = chargeOrderDao.newOrder(order);

        rsp.setOrderId(order.getChargeBussOrderNo());

        return rsp;
    }

    @Override
    public GetValMsgRsp getChargeValMsg(GetValMsgReq req) {

        GetValMsgRsp rsp = new GetValMsgRsp();

        ChargeOrderInfo orderInfo =  chargeOrderDao.getByBusOrderNo(req.getOrderId());
        if(orderInfo==null){
            throw new BusinessException("订单不存在");
        }

        QueryUserInfoService queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");

        AccountRpcService accountRpcService = dubboClient.getDubboClient("accountRpcService");

        BindCardInfo cardInfo = accountRpcService.findCardInfoByBindNo(req.getBindNo());
        if(cardInfo==null){
            throw new BusinessException("银行卡不存在");
        }

        ChargeRequest charge = new ChargeRequest();

        charge.setPayerUserId(req.getUserId());
        charge.setAmount((int) orderInfo.getAmount());
        charge.setOrderId(orderInfo.getChargeBussOrderNo());
        charge.setReturnUrl(initBean.frontUrl);
        charge.setNotifyUrl(initBean.backNotifyUrl);
        charge.setOrderTime(new SimpleDateFormat("yyyyMMddHHmmss").format(orderInfo.getCreateTime()));
        charge.setSignType("MD5");
        charge.setAttach("");
        //已有卡支付
        charge.setSrcChannel("2");
        charge.setBindCardFlag(false);
        charge.setBindNo(req.getBindNo());
        charge.setChannelId(initBean.channelId);
        String sign = SignService.CalSign(charge, initBean.newPayKey);

        charge.setSign(sign);

        try {
            TradeResponse response = tradeRpcService.charge(charge);

            rsp.setResultCode(response.getResultCode());
            rsp.setResultMsg(response.getResultMsg());

            Pair<Integer,String> state = ChargeOrderDao.ChargeCodeToState(response.getResultCode());

            chargeOrderDao.update(orderInfo.getId(),response.getTradeOrderId(),state.first,state.second,1,cardInfo.getCardType(),req.getBindNo(),orderInfo.getAmount());

        }catch (Exception e){
            throw new BusinessException("系统繁忙，请稍后再试");
        }


        return rsp;
    }

    @Override
    public GetValMsgRsp getChargeValMsg(String orderId, String ip) {
        GetValMsgRsp rsp = new GetValMsgRsp();
        ChargeOrderInfo orderInfo =  chargeOrderDao.getByBusOrderNo(orderId);
        if(orderInfo==null){
            throw new BusinessException("订单不存在");
        }

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");

        ResendQuickVCRequest req = new ResendQuickVCRequest();

        req.setTradeOrderId(orderInfo.getTradeOrderId());
        req.setSrcChannel("1");
        req.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getCreateTime()));
        req.setExpireTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getExpireDate()));
        req.setSignType("MD5");
        String sign = SignService.CalSign(req,initBean.newPayKey);
        req.setSign(sign);

        try {
            TradeResponse response = tradeRpcService.resendQuickVC(req);
            rsp.setResultCode(response.getResultCode());
            rsp.setResultMsg(response.getResultMsg());
        }catch (Exception e){
            throw new BusinessException("系统繁忙，请稍后再试");
        }

        return rsp;
    }

    @Override
    public ValSmsgRsp valChargeSMsg(String orderId, String verifyCode, String ip) {
        ValSmsgRsp rsp = new ValSmsgRsp();


        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        if(tradeRpcService==null){
            logger.error("系统错误,获取TradeRpcService 失败");
            throw new BusinessException("系统繁忙，请稍后再试");
        }

        ChargeOrderInfo orderInfo = chargeOrderDao.getByeTradeOrderNo(orderId);
        if(orderInfo==null){
            logger.error("未找到充值订单");
            throw new BusinessException("订单不存在");
        }
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String orderTime = d.format(orderInfo.getCreateTime());
        String expireTime = null;

        VerifyQuickPayRequest request =  new VerifyQuickPayRequest();

        request.setVerifyCode(verifyCode);
        request.setTradeOrderId(orderInfo.getTradeOrderId());
        request.setAttach("");
        //no reference
        request.setSrcChannel("2");
        request.setReturnUrl(initBean.frontUrl);
        request.setNotifyUrl(initBean.backNotifyUrl);
        request.setOrderTime(orderTime);
        request.setExpireTime(expireTime);
        request.setSignType("MD5");

        String sign = SignService.CalSign(request, initBean.newPayKey);

        request.setSign(sign);

        try {
            TradeResponse tradeResponse = tradeRpcService.verifyQuickPay(request);
            if(tradeResponse!=null){
                rsp.setResultCode(tradeResponse.getResultCode());
                rsp.setResultMsg(tradeResponse.getResultMsg());
            }

        }catch (Exception e){
            throw new BusinessException("系统繁忙，请稍后再试");
        }

        return rsp;
    }

    @Override
    public CreateTransferOrderRsp creatTransferOrder(CreateTransferOrderReq req) {

        CreateTransferOrderRsp rsp = new CreateTransferOrderRsp();
        TransferOrderInfo order = new TransferOrderInfo();

        order.setOrderId(UtilTool.generateTransferOrderId());
        order.setFromUserCode(req.getPayerUserId());
        order.setToUserCode(req.getPayeeUserId());
        order.setAmount(req.getPayAmount());
        order.setFromMob(req.getMob());
        order.setReference(req.getIp());

        order.setReceiverType(1);
        order.setCreateTime(new Date());
        order.setOrderState(1);
        order.setOrderStateDesc("待支付");

        transferOrderDao.createOrder(order);

        rsp.setOrderId(order.getOrderId());

        return rsp;
    }

    @Override
    public DoTransferRsp doTransfer(String orderId, String ip) {

        TransferOrderInfo orderInfo = transferOrderDao.getByOrderId(orderId);
        if(orderInfo==null){
            throw new BusinessException("未找到订单");
        }

        TransferRequest request = new TransferRequest();
        request.setOrderId(orderInfo.getOrderId());
        request.setPayerUserId(orderInfo.getFromUserCode());
        request.setPayeeUserId(orderInfo.getToUserCode());
        request.setPayType(1);
        request.setReceiverType(orderInfo.getReceiverType());
        request.setAmount(orderInfo.getAmount() + "");
        request.setBindNo(orderInfo.getBindNo());
        request.setAttach(orderInfo.getAttach());
        request.setSrcChannel("1");
        //request.setReturnUrl(initBean.transferNotifyUrl);
        request.setNotifyUrl(initBean.transferbackNotifyUrl);
        request.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getCreateTime()));
        request.setExpireTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getExpireTime()));
        request.setIsUsePwd(orderInfo.getIsUsePwd() + "");
        request.setChannelId(orderInfo.getChannelId());
        request.setBusinessType("1114");
        request.setSignType("MD5");


        DoTransferRsp rsp = new DoTransferRsp();

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        if(tradeRpcService == null){
            logger.error("获取TradeRpcService 失败!");
            throw new BusinessException("系统繁忙，请稍后再试");
        }

        try {
            TradeResponse brsp = tradeRpcService.transfer(request);
            TransferOrderDao.OrderState state = TransferOrderDao.GetState(brsp.getResultCode());
            transferOrderDao.update(orderInfo.getId(),state.state,state.desc,brsp.getTradeOrderId(),1,"");

        }catch (Exception e){
            logger.error("发生异常，转账失败!"+e);
        }


        return rsp;
    }

    @Override
    public boolean queryCanWithdrawals(String userCode) {

        WithdrawAuthRpcService withdrawAuthRpcService =  dubboClient.getDubboClient("withdrawAuthRpcService");


        WithdrawAuthListResponse rsp = withdrawAuthRpcService.getWithDrawAuthByUserId(userCode);
        if(rsp!=null) {
            if (rsp.getStatus() == 1 && rsp.getType() == 2) {
                return false;
            } else{
                return true;
            }
        }

        return true;
    }

    @Override
    public List<BankCardInfo> queryWithdrawalBankCard(String userCode) {

        AccountRpcService accountRpcService = dubboClient.getDubboClient("accountRpcService");
        List<BindCardInfo> cards = accountRpcService.queryBindCardList(userCode, 2);

        List<BankCardInfo> ret = new ArrayList<>();
        if(cards!=null)
        for(BindCardInfo bindCardInfo:cards){
            BankCardInfo cardInfo = new BankCardInfo();

            cardInfo.setBindNo(bindCardInfo.getBindNo());
            cardInfo.setCardNo(bindCardInfo.getCardNo());
            cardInfo.setBankName(bindCardInfo.getBankName());
            cardInfo.setSwiftCode(bindCardInfo.getSwiftCode());
            ret.add(cardInfo);
        }

        return ret;
    }

    @Override
    public WithdrawalRsp DowithDrawal(WithdrawalReq req) {

        WithdrawalRsp rsp = new WithdrawalRsp();

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");


        WithdrawOrderInfo orderInfo = new WithdrawOrderInfo();
        orderInfo.setOrderId(UtilTool.generateWithdrawOrderId());
        orderInfo.setUsercode(req.getUserId());
        orderInfo.setAmount(req.getAmount());
        orderInfo.setBindNo(req.getBindNo());
        //orderInfo.setCardNo();
        orderInfo.setCreatetime(new Date());
        orderInfo.setState(WithdrawOrderDao.OrderState.init.state);
        orderInfo.setStateDesc(WithdrawOrderDao.OrderState.init.desc);
        orderInfo.setIp(req.getIp());

        orderInfo = withdrawOrderDao.newOrder(orderInfo);


        WithdrawRequest request = new WithdrawRequest();


        request.setPayerUserId(req.getUserId());
        request.setWithdrawType(1);
        request.setBindNo(req.getBindNo());
        request.setOrderId(orderInfo.getOrderId());
        request.setAmount((int) orderInfo.getAmount());
        request.setSrcIp(req.getIp());

        request.setSrcChannel("2");
        request.setOrderTime(new SimpleDateFormat("yyyyMMddHHmmss").format(orderInfo.getCreatetime()));
        request.setChannelId(initBean.channelId);
        request.setBusinessType(initBean.withdrawBusinessType);
        request.setSign("MD5");
        request.setSign(SignService.CalSign(request,initBean.newPayKey));

        try{
            tradeRpcService.withdraw(request);
        }catch (Exception e){
            throw new BusinessException("系统繁忙，请稍后再试"+e);
        }

        return rsp;
    }

    @Override
    public long queryBalance(String userCode) {

        AccountRpcService accountRpcService = dubboClient.getDubboClient("accountRpcService");

        long balance = accountRpcService.queryBalance(userCode, 8);

        return balance;
    }

    @Override
    public BalancePayRsp BalancePay(String orderId,MobOrderType type, String ip) {








        return null;
    }

    @Override
    public QuickPayRsp quickPay(String orderId,MobOrderType type, String ip, String bindNo) {



        return null;
    }

    @Override
    public void quickPayReGetMsg(String orderId,MobOrderType type, String ip) {




    }

    @Override
    public void quickPayValMsg(String orderId, MobOrderType type,String verifyCode, String ip) {

    }


    /**
     * 支付订单
     */
    private PayResult payOrder(MobileOrderInfo orderInfo){

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("");
        return null;
    }
}
