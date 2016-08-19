package com.berbon.jfaccount.impl;

import com.berbon.jfaccount.Dao.*;
import com.berbon.jfaccount.Service.PayNotifyService;
import com.berbon.jfaccount.Service.SignService;
import com.berbon.jfaccount.comm.BusinessType;
import com.berbon.jfaccount.comm.ErrorCode;
import com.berbon.jfaccount.comm.InitBean;
import com.berbon.jfaccount.facade.AccountMobileFacade;
import com.berbon.jfaccount.facade.mobpojo.*;
import com.berbon.jfaccount.facade.pojo.ChargeOrderInfo;
import com.berbon.jfaccount.facade.pojo.QuickPayValRsp;
import com.berbon.jfaccount.facade.pojo.TransferOrderInfo;
import com.berbon.jfaccount.facade.pojo.WithdrawOrderInfo;
import com.berbon.jfaccount.pojo.GameChargeOrderInfo;
import com.berbon.jfaccount.pojo.MobieChargeOrderInfo;
import com.berbon.jfaccount.pojo.MobileOrderInfo;
import com.berbon.jfaccount.pojo.PayNotifyData;
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
    private PayNotifyService payNotifyService;

    @Autowired
    private WithdrawOrderDao withdrawOrderDao;

    @Autowired
    private BusChargeOrderDao mobileDao;

    @Autowired
    private GameChargeOrderDao gameDao;

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
            logger.error("发生异常，转账失败!");
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
            throw new BusinessException("系统繁忙，请稍后再试");
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
    public BalancePayRsp BalancePay(String userCode,String orderId,MobOrderType type, String ip) {

        MobileOrderInfo payorder = queryPayOrder(userCode, orderId, type);
        PayResult result = payOrder(payorder);


        //支付成功
        PayNotifyData data = new PayNotifyData();

        data.orderId = payorder.getOrderId();
        data.payAmount = result.payValue;
        data.payTime  = new Date();
        data.payOrderId = result.payOrderId;

        switch (payorder.getOrderType()){
            case  mobile_charge:
                payNotifyService.mobileChargeNotify(data);
                break;
            case game_charge:
                payNotifyService.gameChargeNotify(data);
                break;
        }
        return new BalancePayRsp();
    }

    private MobileOrderInfo  queryPayOrder(String userCode,String orderId,MobOrderType type){

        MobileOrderInfo payorder = new MobileOrderInfo();
        payorder.setUserCode(userCode);
        payorder.setOrderId(orderId);
        payorder.setPayType(MobileOrderInfo.PayType.balance_pay);
        payorder.setOrderType(type);


        if(type == MobOrderType.mobile_charge){
            MobieChargeOrderInfo order = mobileDao.queryOrder(orderId);
            if(order==null){
                throw new BusinessException("未找到订单");
            }
            //判断订单状态
            if(order.getCharge_result()==0){

                logger.info("支付话费充值订单:"+orderId);

                payorder.setDownOrderTime(order.getAdd_time());
                payorder.setAmount(order.getPrice());
                payorder.setTradeType(BusinessType.type_2002);
                payorder.setGoodsName("话费充值");
                payorder.setGoodsDetail("话费充值"+order.getMob());

            }else {
                throw new BusinessException("订单状态错误，不能支付");
            }
        }else if(type == MobOrderType.game_charge ){
            GameChargeOrderInfo order = gameDao.queryOrder(orderId);
            if(order==null){
                throw new BusinessException("未找到订单");
            }

            //判断订单状态
            if(order.getStatus()==0){

                logger.info("支付游戏充值订单:" + orderId);

                payorder.setDownOrderTime(new Date(order.getOrderCreateTime()));
                payorder.setAmount(order.getTotalMoney());
                payorder.setTradeType(BusinessType.type_2002);
                payorder.setGoodsName("游戏充值");
                payorder.setGoodsDetail("游戏充值");

            }else{
                throw new BusinessException("订单状态错误，不能支付");
            }
        }
        return payorder;
    }

    @Override
    public QuickPayRsp quickPay(String userCode,String orderId,MobOrderType type, String ip, String bindNo) {

        MobileOrderInfo payorder = queryPayOrder(userCode, orderId, type);
        payorder.setPayType(MobileOrderInfo.PayType.bind_quick_pay);
        payorder.setBindNo(bindNo);
        //查询bindno

        AccountRpcService accountRpcService = dubboClient.getDubboClient("accountRpcService");
        BindCardInfo cardinfo = accountRpcService.findCardInfoByBindNo(bindNo);
        if(cardinfo==null || cardinfo.getUserId().equals(userCode)==false){
            throw new BusinessException("银行卡信息错误，支付失败");
        }

        payorder.setBankId(cardinfo.getBankId());
        PayResult result = payOrder(payorder);
        if(result.result!= PayResult.Result.paying){
            throw new BusinessException("支付状态异常");
        }

        return new QuickPayRsp();
    }

    @Override
    public void quickPayReGetMsg(String userCode,String orderId,MobOrderType type, String ip) {

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        if(tradeRpcService==null){
            logger.error("系统错误,获取TradeRpcService 失败");
        }

        if(type == MobOrderType.mobile_charge) {
            MobieChargeOrderInfo order = mobileDao.queryOrder(orderId);
            if (order == null) {
                throw new BusinessException("未找到订单");
            }
        }else if(type==MobOrderType.game_charge){
            GameChargeOrderInfo order = gameDao.queryOrder(orderId);
            if(order==null){
                throw new BusinessException("未找到订单");
            }
        }


        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String orderTime = d.format(orderInfo.getCreateTime());
//        String expireTime = null;
//
//        VerifyQuickPayRequest request =  new VerifyQuickPayRequest();
//
//        request.setVerifyCode(data.getVerifyCode());
//        request.setTradeOrderId(data.getTradeOrderId());
//        request.setAttach(data.getAttach());
//
//        request.setSrcChannel(data.getSrcChannel());
//        request.setReturnUrl(initBean.frontUrl);
//        request.setNotifyUrl(initBean.backNotifyUrl);
//        request.setOrderTime(orderTime);
//        request.setExpireTime(expireTime);
//        request.setSignType("MD5");
//        request.setOrderId(orderInfo.getChargeBussOrderNo());
//        request.setSrcIp(ip);

//
//        String sign = SignService.CalSign(request, initBean.newPayKey);
//
//        request.setSign(sign);

//
//        QuickPayValRsp rsp = new QuickPayValRsp();
//
//        try {
//            TradeResponse tradeResponse = tradeRpcService.verifyQuickPay(request);
//            if(tradeResponse!=null){
//                rsp.setResultCode(tradeResponse.getResultCode());
//                rsp.setResultMsg(tradeResponse.getResultMsg());
//                rsp.setTradeOrderId(tradeResponse.getTradeOrderId());
//                rsp.setPayUrl(tradeResponse.getPayUrl());
//                rsp.setPayParams(tradeResponse.getPayParams());
//                //更新充值结果
//                Pair<Integer,String> state = ChargeOrderDao.ChargeCodeToState(tradeResponse.getResultCode());
//                //dao.update(orderInfo.getId(),state.first, state.second);
//
//            }
//        }catch (Exception e ){
//            rsp.setResultCode(ErrorCode.sys_error.code);
//            rsp.setResultMsg(ErrorCode.sys_error.desc);
//        }



    }

    @Override
    public void quickPayValMsg(String userCode,String orderId, MobOrderType type,String verifyCode, String ip) {

    }


    /**
     * 支付订单
     */
    private PayResult payOrder(MobileOrderInfo orderInfo){
        TradeRpcService tradeRpcService =null;
        try{
            tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        }catch (Exception e){
            throw new BusinessException("系统繁忙，请稍后再试");
        }

        B2CRequest payReq = new B2CRequest();
        payReq.setOrderId(orderInfo.getOrderId());
        payReq.setPayerUserId(orderInfo.getUserCode());

        if(orderInfo.getPayType()== MobileOrderInfo.PayType.bind_quick_pay){
            payReq.setBindNo(orderInfo.getBindNo());
            payReq.setBankId(orderInfo.getBankId());
        }

        payReq.setAmount((int) orderInfo.getAmount());
        payReq.setGoodsName(orderInfo.getGoodsName());
        payReq.setGoodsDetail(orderInfo.getGoodsDetail());

        //担保交易(0及时交易，1担保交易)
        payReq.setGuaranteeType(0);
        payReq.setSrcIp(orderInfo.getFromIp());
        //来源渠道 1网站，2手机，3微信,4内部
        payReq.setSrcChannel("2");

        payReq.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getDownOrderTime()));

        payReq.setIsUsePwd("1");
        payReq.setChannelId(initBean.channelId);
        payReq.setBusinessType(orderInfo.getTradeType().type + "");
        payReq.setProductType(2);

        payReq.setSign("MD5");
        String sign = SignService.CalSign(payReq,initBean.newPayKey);
        payReq.setSign(sign);

        try{
            TradeResponse response = tradeRpcService.b2c(payReq);
            switch (response.getResultCode()){
                case "3":return new PayResult(PayResult.Result.succ,response.getTradeOrderId(),payReq.getAmount());
                case "2":
                case "1":
                    return new PayResult(PayResult.Result.paying,response.getTradeOrderId(),payReq.getAmount());
                case "4":
                    return new PayResult(PayResult.Result.fail,response.getTradeOrderId(),0);
                case "5":
                    return new PayResult(PayResult.Result.exception,null,0);
                default:
                    return new PayResult(PayResult.Result.unknow,null,0);
            }

        }
        catch (BusinessException e){
            logger.error("支付异常"+e);
            return new PayResult(PayResult.Result.exception,null,0);
        }
        catch (Exception e){
            logger.error("支付异常 异常未知"+e);
            return new PayResult(PayResult.Result.exception,null,0);
        }

    }
}
