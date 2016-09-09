package com.berbon.jfaccount.impl;

import com.alibaba.fastjson.JSON;
import com.berbon.jfaccount.Dao.*;
import com.berbon.jfaccount.Service.PayNotifyService;
import com.berbon.jfaccount.Service.SignService;
import com.berbon.jfaccount.comm.BusinessType;
import com.berbon.jfaccount.comm.ErrorCode;
import com.berbon.jfaccount.comm.InitBean;
import com.berbon.jfaccount.facade.AccountMobileFacade;
import com.berbon.jfaccount.facade.mobpojo.*;
import com.berbon.jfaccount.facade.mobpojo.CreateChargeReq;
import com.berbon.jfaccount.facade.mobpojo.CreateChargeRsp;
import com.berbon.jfaccount.facade.pojo.*;
import com.berbon.jfaccount.pojo.*;
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
import com.sztx.usercenter.rpc.api.domain.out.UserVO;
import com.sztx.usercenter.rpc.api.service.QueryUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chj on 2016/8/10.
 */
@Service("accountMobileFacadeIMpl")
public class AccountMobileFacadeIMpl implements AccountMobileFacade {


    private static Logger logger = LoggerFactory.getLogger(AccountMobileFacadeIMpl.class);

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


    @Autowired
    private PayOrderDao payOrderDao;

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
        charge.setReturnUrl(initBean.chargefrontNotifyUrl);
        charge.setNotifyUrl(initBean.chargebackNotifyUrl);
        charge.setOrderTime(new SimpleDateFormat("yyyyMMddHHmmss").format(orderInfo.getCreateTime()));
        charge.setSignType("MD5");
        charge.setAttach("");
        //已有卡支付
        charge.setSrcChannel("2");
        charge.setBindCardFlag(false);
        charge.setBindNo(req.getBindNo());
        charge.setChannelId(initBean.channelId);
        charge.setBusinessType(BusinessType.type_2013.type+"");
        charge.setSrcIp(req.getIp());
        String sign = SignService.CalSign(charge, initBean.newPayKey);

        charge.setSign(sign);

        try {
            TradeResponse response = tradeRpcService.charge(charge);

            rsp.setResultCode(response.getResultCode());
            rsp.setResultMsg(response.getResultMsg());

            Pair<Integer,String> state = ChargeOrderDao.ChargeCodeToState(response.getResultCode());

            chargeOrderDao.update(orderInfo.getId(),response.getTradeOrderId(),state.first,state.second,1,cardInfo.getCardType(),req.getBindNo(),orderInfo.getAmount());

        }catch (Exception e){
            logger.error("支付异常"+e);
            throw new BusinessException(e.getMessage());
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

        req.setSrcIp(ip);
        req.setOrderId(orderInfo.getChargeBussOrderNo());
        req.setTradeOrderId(orderInfo.getTradeOrderId());
        req.setSrcChannel("1");
        req.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getCreateTime()));
        if(orderInfo.getExpireDate()!=null)
            req.setExpireTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getExpireDate()));
        req.setSignType("MD5");
        String sign = SignService.CalSign(req,initBean.newPayKey);
        req.setSign(sign);


        try {
            TradeResponse response = tradeRpcService.resendQuickVC(req);
            rsp.setResultCode(response.getResultCode());
            rsp.setResultMsg(response.getResultMsg());
        }catch (BusinessException e){
            logger.error("支付异常"+e);
            throw new BusinessException(e.getMessage());
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

        ChargeOrderInfo orderInfo = chargeOrderDao.getByBusOrderNo(orderId);
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
        request.setSrcIp(ip);
        request.setSrcChannel("2");
        request.setReturnUrl(initBean.chargefrontNotifyUrl);
        request.setNotifyUrl(initBean.chargebackNotifyUrl);
        request.setOrderTime(orderTime);
        request.setExpireTime(expireTime);
        request.setSignType("MD5");
        request.setOrderId(orderInfo.getChargeBussOrderNo());

        String sign = SignService.CalSign(request, initBean.newPayKey);

        request.setSign(sign);

        try {
            TradeResponse tradeResponse = tradeRpcService.verifyQuickPay(request);
            if(tradeResponse!=null){
                rsp.setResultCode(tradeResponse.getResultCode());
                rsp.setResultMsg(tradeResponse.getResultMsg());
            }

        }catch (BusinessException e){
            logger.error("发生异常:"+e.getMessage());
            throw new BusinessException(e.getMessage());
        }catch (Exception e) {
            logger.error("支付异常"+e);
            throw new BusinessException("系统繁忙，请稍后再试");
        }
        return rsp;
    }

    @Override
    public CreateTransferOrderRsp creatTransferOrder(CreateTransferOrderReq req) {

        CreateTransferOrderRsp rsp = new CreateTransferOrderRsp();
        TransferOrderInfo order = new TransferOrderInfo();

        QueryUserInfoService  queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
        if(queryUserInfoService==null){
            throw new BusinessException("系统繁忙，请稍后再试!");
        }

        UserVO fromuserVO = queryUserInfoService.getUserInfo(req.getPayerUserId());
        if(fromuserVO == null){
            throw new BusinessException("转出用户不存在!");
        }

        UserVO toUesrVo = queryUserInfoService.getUserInfo(req.getPayeeUserId());
        if(toUesrVo == null){
            throw new BusinessException("转入用户不存在!");
        }


        AccountRpcService accountRpcService = dubboClient.getDubboClient("accountRpcService");
        boolean isTongxingUser = accountRpcService.checkUserValid(req.getPayeeUserId());


        if(isTongxingUser==false){
            throw new BusinessException("系统升级，暂不支持向此用户转款!");
        }


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
        order.setChannelId(initBean.channelId);
        order.setBusinessType(BusinessType.type_2014.type + "");
        order.setCreateUserCode(req.getPayerUserId());

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
        request.setReturnUrl(initBean.transferNotifyUrl);
        request.setNotifyUrl(initBean.transferbackNotifyUrl);
        request.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getCreateTime()));
        if(orderInfo.getExpireTime()!=null)
            request.setExpireTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getExpireTime()));
        request.setIsUsePwd(orderInfo.getIsUsePwd() + "");
        request.setChannelId(orderInfo.getChannelId());
        request.setBusinessType(BusinessType.type_2014.type + "");
        request.setSignType("MD5");
        request.setSrcIp(ip);
        request.setSign(SignService.CalSign(request, initBean.newPayKey));

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

        }catch (BusinessException e){
            logger.error("发生异常，转账失败!"+e.getMessage());
            throw new BusinessException(e.getMessage());
        }


        return rsp;
    }

    @Override
    public boolean queryCanWithdrawals(String userCode) {

        WithdrawAuthRpcService withdrawAuthRpcService =  dubboClient.getDubboClient("withdrawAuthRpcService");


        WithdrawAuthListResponse rsp = withdrawAuthRpcService.getWithDrawAuthByUserId(userCode,initBean.channelId);
        if(rsp!=null) {
            if (rsp.getStatus() == 1 && rsp.getType() == 2) {
                return false;
            } else if(rsp.getStatus() ==1 && rsp.getType() == 1){
                return true;
            }
        }

        return false;
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
        request.setBusinessType(BusinessType.type_2014.type + "");
        request.setSign("MD5");
        request.setSign(SignService.CalSign(request,initBean.newPayKey));

        try{
            tradeRpcService.withdraw(request);
        }catch (Exception e){
            throw new BusinessException(e.getMessage());
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
        payorder.setFromIp(ip);
        payorder.setBankId("0000000");
        PayResult result = payOrder(payorder);

        logger.info("余额支付结果:"+ JSON.toJSONString(result));

        if(result.result== PayResult.Result.succ){
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
        }


        if(result.result == PayResult.Result.exception){
            throw new BusinessException(result.message);
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

                Date addTime = null;
                if(order.getAdd_time()!=null)
                {
                    try
                    {
                        addTime=  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0").parse(order.getAdd_time());
                    }catch (Exception e){
                        logger.error("下单时间异常:"+e.getMessage());
                    }
                }

                payorder.setDownOrderTime(addTime);
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
            if(order.getOrderStatus()==0){
                logger.info("支付游戏充值订单:" + orderId);
                payorder.setDownOrderTime(new Date(order.getOrderCreateTime()));

                BigDecimal amount = new BigDecimal(order.getTotalMoney());
                payorder.setAmount(amount.divide(new BigDecimal(10)).longValue());
                payorder.setTradeType(BusinessType.type_2005);
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
        if(accountRpcService==null){
            logger.error("获取接口异常");
            throw new BusinessException("系统繁忙，请稍后再试");
        }
        BindCardInfo cardinfo = accountRpcService.findCardInfoByBindNo(bindNo);
        if(cardinfo==null ){
            throw new BusinessException("银行卡信息错误，支付失败");
        }

        payorder.setBankId(cardinfo.getBankId());
        payorder.setFromIp(ip);
        PayResult result = payOrder(payorder);
        if(result.result!= PayResult.Result.paying){
            throw new BusinessException("支付状态异常,"+result.message);
        }

        return new QuickPayRsp();
    }

    @Override
    public void quickPayReGetMsg(String userCode,String orderId,MobOrderType type, String ip) {

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        if(tradeRpcService==null){
            logger.error("系统错误,获取TradeRpcService 失败");
        }

        Date createTime = null;
        String bussOrderNo = null;

        if(type == MobOrderType.mobile_charge) {
            MobieChargeOrderInfo order = mobileDao.queryOrder(orderId);
            if (order == null) {
                throw new BusinessException("未找到订单");
            }

            if(order.getCharge_result()!=0){
                throw new BusinessException("订单状态错误，不能支付");
            }

            try {
                createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0").parse(order.getAdd_time());
            }catch (Exception e){
                logger.error("下单时间异常"+e.getMessage());
            }
            bussOrderNo = orderId;

        }else if(type==MobOrderType.game_charge){
            GameChargeOrderInfo order = gameDao.queryOrder(orderId);
            if(order==null){
                throw new BusinessException("未找到订单");
            }

            if(order.getOrderStatus()!=0){
                throw new BusinessException("订单状态错误，不能支付");
            }

            createTime = new Date(order.getOrderCreateTime());
            bussOrderNo = orderId;

        }

        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String orderTime = d.format(createTime);
        String expireTime = null;

        ResendQuickVCRequest  request =  new ResendQuickVCRequest();

        request.setOrderId(bussOrderNo);
        //String tradeOrderId = cacheService.getTradeOrderId(orderId,type);
        PayOrderInfo payOrderInfo = payOrderDao.getByOrderIdAndType(orderId, type);
        if(payOrderInfo==null){
            throw  new BusinessException("未找到订单");
        }

        request.setTradeOrderId(payOrderInfo.getTradeOrderId());

        request.setSrcChannel("2");
        request.setReturnUrl(initBean.payfrontNotifyUrl);
        request.setNotifyUrl(initBean.payBackNotifyUrl);
        request.setOrderTime(orderTime);
        request.setExpireTime(expireTime);
        request.setSignType("MD5");
        request.setSrcIp(ip);

        String sign = SignService.CalSign(request, initBean.newPayKey);

        request.setSign(sign);

        try
        {
            TradeResponse rsp = tradeRpcService.resendQuickVC(request);
            switch (rsp.getResultCode()){
                case "3":
                    //成功
                    break;
                case "4":
                    //失败
                    throw new BusinessException("重新获取验证码失败");
            }
        }
        catch (BusinessException e){
            throw new BusinessException(e.getMessage());
        }
        catch (Exception e){
            throw new BusinessException("系统繁忙，请稍后再试!");
        }


    }

    @Override
    public boolean quickPayValMsg(String userCode,String orderId, MobOrderType type,String verifyCode, String ip) {

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        if(tradeRpcService==null){
            logger.error("系统错误,获取TradeRpcService 失败");
        }

        Date createTime = null;
        String bussOrderNo = null;

        if(type == MobOrderType.mobile_charge) {
            MobieChargeOrderInfo order = mobileDao.queryOrder(orderId);
            if (order == null) {
                throw new BusinessException("未找到订单");
            }

            if(order.getCharge_result()!=0){
                throw new BusinessException("订单状态错误，不能支付");
            }

            try {
                createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0").parse(order.getAdd_time());
            } catch (Exception e) {
                e.printStackTrace();
            }

            bussOrderNo = orderId;

        }else if(type==MobOrderType.game_charge){
            GameChargeOrderInfo order = gameDao.queryOrder(orderId);
            if(order==null){
                throw new BusinessException("未找到订单");
            }

            if(order.getOrderStatus()!=0){
                throw new BusinessException("订单状态错误，不能支付");
            }

            createTime = new Date(order.getOrderCreateTime());
            bussOrderNo = orderId;
        }


        VerifyQuickPayRequest request = new VerifyQuickPayRequest();


        request.setVerifyCode(verifyCode);
        //String tradeOrderId = cacheService.getTradeOrderId(orderId, type);
        PayOrderInfo orderInfo = payOrderDao.getByOrderIdAndType(orderId, type);
        if(orderInfo==null){
            throw  new BusinessException("支付异常");
        }

        request.setOrderId(orderInfo.getBusOrderId());
        request.setTradeOrderId(orderInfo.getTradeOrderId());
        request.setSrcIp(ip);
        request.setSrcChannel("2");
        request.setReturnUrl(initBean.payfrontNotifyUrl);
        request.setNotifyUrl(initBean.payBackNotifyUrl);
        request.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createTime));

        request.setSign("MD5");
        request.setSign(SignService.CalSign(request, initBean.newPayKey));


        try {

            TradeResponse response = tradeRpcService.verifyQuickPay(request);

            switch (response.getResultCode()){
                case "2":
                case "3":
                    return true;
                case "4"://错误
                    throw  new BusinessException(response.getResultMsg());
                default:
                    throw  new BusinessException("系统繁忙，请稍后再试");
            }
        }
        catch (BusinessException e){
            throw  new BusinessException(e.getMessage());
        }
        catch (Exception e){
            throw  new BusinessException("系统繁忙，请稍后再试");
        }
    }

    /**
     * 支付回调验证
     */
    public void payFrontCallBack(Map<String,String []> params)
    {
        payCallBack(params,NotifyType.front_notify);
    }

    /**
     * 支付回调验证
     */
    public void payBackCallBack(Map<String,String []> params)
    {
        payCallBack(params,NotifyType.back_notify);
    }


    private void payCallBack(Map<String, String[]> params , NotifyType  type) {

        logger.info("收到回调"+params+type);

        Map<String,String > paramMap = new HashMap<>();
        for(Map.Entry<String, String[]> m:params.entrySet()){

            String value = "";
            if(m.getValue().length>0){
                value=m.getValue()[0];
            }
            paramMap.put(m.getKey(),value);
        }

        String f_sign = paramMap.get("sign");

        paramMap.remove("sign");
        String sign = SignService.CalSign(paramMap, initBean.newPayKey);


        if (sign.equalsIgnoreCase(f_sign)==false){
            logger.error("验签失败");

            return ;
        }


        String outOrderId = paramMap.get("outOrderId");
        String tradeOrderId = paramMap.get("tradeOrderId");
        String orderState = paramMap.get("orderState");
        String orderType = paramMap.get("orderType");
        String payAmount = paramMap.get("payAmount");
        String payTime = paramMap.get("payTime"); // yyyyMMddHHmmss

        long payAmountl = 0 ;

        try
        {
            payAmountl =  Long.parseLong(payAmount);
        }catch (Exception e){

        }

        Date payDate = null;
        if(payTime!=null)
            try {
                payDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(payTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        PayOrderInfo.OrderState payOrderState = null;


        PayOrderInfo orderInfo = payOrderDao.getByOrderIdAndTradeOrderId(outOrderId,tradeOrderId);

        if(orderInfo==null){
            logger.error("没有找到订单");
            return ;
        }

        if(type == NotifyType.back_notify) {
            switch (orderState) {
                case "1":

                    payOrderState = PayOrderInfo.OrderState.success;
                    //1订单成功
                    break;
                case "2":
                    payOrderState = PayOrderInfo.OrderState.fail;
                    //2订单失败
                    break;
                case "3":
                    //3订单异常
                    payOrderState = PayOrderInfo.OrderState.exceptioned;
                    break;
                default:

            }
            if(payOrderState.state!=orderInfo.getOrderState()) {
                logger.info("订单状态改变,更新状态");
                payOrderDao.updateOrderState(orderInfo.getBusOrderId(), MobOrderType.parseInt(orderInfo.getOrderType()), payOrderState);
            }

            if(payOrderState == PayOrderInfo.OrderState.success ){

                PayNotifyData data = new PayNotifyData();
                data.payOrderId = tradeOrderId;
                data.orderId = outOrderId;
                data.payAmount = payAmountl;
                data.payTime = payDate;


                switch (MobOrderType.parseInt(orderInfo.getOrderType())){
                    case game_charge:
                        payNotifyService.gameChargeNotify(data);
                        break;
                    case mobile_charge:
                        payNotifyService.mobileChargeNotify(data);
                        break;
                    default:
                        logger.error("未知类型");
                }
            }

        }
    }


    /**
     * 支付订单
     */
    private PayResult payOrder(MobileOrderInfo orderInfo){
        logger.info("支付订单, 支付类型"+orderInfo.getPayType());
        TradeRpcService tradeRpcService =null;
        try{
            tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        }catch (Exception e){
            return new PayResult(PayResult.Result.exception,null,0,"系统繁忙，请稍后再试");
        }

        B2CRequest payReq = new B2CRequest();
        payReq.setOrderId(orderInfo.getOrderId());
        payReq.setPayerUserId(orderInfo.getUserCode());

        if(orderInfo.getPayType()== MobileOrderInfo.PayType.bind_quick_pay){
            payReq.setBindNo(orderInfo.getBindNo());
        }
        payReq.setBankId(orderInfo.getBankId());

        payReq.setAmount((int) orderInfo.getAmount());
        payReq.setGoodsName(orderInfo.getGoodsName());
        payReq.setGoodsDetail(orderInfo.getGoodsDetail());

        //担保交易(0及时交易，1担保交易)
        payReq.setGuaranteeType(0);
        payReq.setSrcIp(orderInfo.getFromIp());
        //来源渠道 1网站，2手机，3微信,4内部
        payReq.setSrcChannel("2");

        if(orderInfo.getDownOrderTime()!=null)
            payReq.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getDownOrderTime()));
        else

        payReq.setIsUsePwd("1");
        payReq.setChannelId(initBean.channelId);
        payReq.setBusinessType(orderInfo.getTradeType().type + "");
        payReq.setProductType(2);

        payReq.setReturnUrl(initBean.payfrontNotifyUrl);
        payReq.setNotifyUrl(initBean.payBackNotifyUrl);

        switch (orderInfo.getOrderType()){
            case game_charge:
                payReq.setPayeeUserId(initBean.GameChargePayeeUserCode);
                break;
            case mobile_charge:
                payReq.setPayeeUserId(initBean.MobileChargePayeeUserCode);
                break;
        }

        payReq.setSrcIp(orderInfo.getFromIp());
        payReq.setSign("MD5");
        String sign = SignService.CalSign(payReq,initBean.newPayKey);
        payReq.setSign(sign);

        try{
            PayOrderInfo payOrder = new  PayOrderInfo();
            payOrder.setOrderType(orderInfo.getOrderType().type);
            payOrder.setBusOrderId(orderInfo.getOrderId());
            payOrder.setCreatetime(new Date());
            TradeResponse response = tradeRpcService.b2c(payReq);
            switch (response.getResultCode()){
                case "3":

                    payOrder.setTradeOrderId(response.getTradeOrderId());

                    payOrder.setOrderState(PayOrderInfo.OrderState.success.state);
                    payOrder.setOrderStateDesc(PayOrderInfo.OrderState.success.desc);
                    payOrder.setLastUpdateTime(null);

                    payOrderDao.newOrder(payOrder);
                    return new PayResult(PayResult.Result.succ,response.getTradeOrderId(),payReq.getAmount(),"支付成功");
                case "2":
                case "1":

                    payOrder.setTradeOrderId(response.getTradeOrderId());
                    payOrder.setOrderState(PayOrderInfo.OrderState.paying.state);
                    payOrder.setOrderStateDesc(PayOrderInfo.OrderState.paying.desc);
                    payOrder.setLastUpdateTime(null);

                    payOrderDao.newOrder(payOrder);
                    //cacheService.addNew(orderInfo.getOrderId(),orderInfo.getOrderType(),response.getTradeOrderId());
                    return new PayResult(PayResult.Result.paying,response.getTradeOrderId(),payReq.getAmount(),"支付中");
                case "4":
                    return new PayResult(PayResult.Result.fail,response.getTradeOrderId(),0,"失败");
                case "5":
                    return new PayResult(PayResult.Result.exception,null,0,"系统繁忙");
                default:
                    return new PayResult(PayResult.Result.unknow,null,0,"系统繁忙");
            }

        }
        catch (BusinessException e){
            logger.error("支付异常"+e.getMessage());
            return new PayResult(PayResult.Result.exception,null,0,e.getMessage());
        }
        catch (Exception e){
            logger.error("支付异常 异常未知"+e);
            e.printStackTrace();
            return new PayResult(PayResult.Result.exception,null,0,e.getMessage());
        }

    }
}
