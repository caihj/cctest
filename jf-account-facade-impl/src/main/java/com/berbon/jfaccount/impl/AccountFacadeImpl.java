package com.berbon.jfaccount.impl;

import com.alibaba.fastjson.JSONObject;
import com.berbon.jfaccount.Dao.ChargeOrderDao;
import com.berbon.jfaccount.Dao.TransferOrderDao;
import com.berbon.jfaccount.Dao.UserActFlowDao;
import com.berbon.jfaccount.Dao.WithdrawOrderDao;
import com.berbon.jfaccount.Service.SignService;
import com.berbon.jfaccount.comm.BusinessType;
import com.berbon.jfaccount.comm.ErrorCode;
import com.berbon.jfaccount.comm.InitBean;
import com.berbon.jfaccount.facade.AccountFacade;
import com.berbon.jfaccount.facade.common.PageResult;
import com.berbon.jfaccount.facade.pojo.*;
import com.berbon.jfaccount.pojo.NotifyType;
import com.berbon.jfaccount.pojo.UserActFlow;
import com.berbon.jfaccount.util.MyUtils;
import com.berbon.jfaccount.util.Pair;
import com.berbon.jfaccount.util.UtilTool;
import com.berbon.util.String.StringUtil;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import com.sztx.pay.center.rpc.api.domain.*;
import com.sztx.pay.center.rpc.api.service.AccountRpcService;
import com.sztx.pay.center.rpc.api.service.TradeRpcService;
import com.sztx.se.common.exception.BusinessException;
import com.sztx.se.rpc.dubbo.source.DynamicDubboClient;
import com.sztx.usercenter.rpc.api.domain.out.UserVO;
import com.sztx.usercenter.rpc.api.service.QueryUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chj on 2016/8/5.
 */

@Service("accountFacadeImpl")
public class AccountFacadeImpl implements AccountFacade {


    private static Logger logger = LoggerFactory.getLogger(AccountFacadeImpl.class);

    @Autowired
    private ChargeOrderDao dao;
    @Autowired
    private TransferOrderDao transferOrderDao;

    @Autowired
    private UserActFlowDao userActFlowDao;

    @Autowired
    private WithdrawOrderDao withdrawOrderDao;

    @Autowired
    private DynamicDubboClient dubboClient;

    @Autowired
    private InitBean initBean;

    @Autowired
    private SignService signService;

    @Override
    public ChargeOrderInfo createChargeQuckPay(CreateChargeReq data) {

        String orderId = UtilTool.generateChargeOrderId();

        ChargeOrderInfo info = new ChargeOrderInfo();

        info.setChargeUserCode(data.getUserCode());

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
        info.setFromIp(data.getIp());


        info = dao.newOrder(info);

        return info;
    }

    @Override
    public ReSendChargeValMsgRsp reSendQuickValMsg(String tradeOrderId ,BusOrderType type,String ip) {

        ReSendChargeValMsgRsp rsp = new ReSendChargeValMsgRsp();

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        if(tradeRpcService==null){
            logger.error("系统错误,获取TradeRpcService 失败");
        }

        ResendQuickVCRequest  request =  new ResendQuickVCRequest();
        Date createTime = null;
        String bussOrderNo = null;

        if(type == BusOrderType.charge_order){
            ChargeOrderInfo info = dao.getByeTradeOrderNo(tradeOrderId);
            if(info==null){
                logger.info("未找到，订单");
                rsp.setOk(false);
                rsp.setMsg("未找到订单");
                return rsp;
            }
            request.setReturnUrl(initBean.chargefrontNotifyUrl);
            request.setNotifyUrl(initBean.chargebackNotifyUrl);
            bussOrderNo = info.getChargeBussOrderNo();
            createTime = info.getCreateTime();
        }else if(type == BusOrderType.transfer_order){
            TransferOrderInfo info = transferOrderDao.getByTradeOrderId(tradeOrderId);
            if(info==null){
                logger.info("未找到，订单");
                rsp.setOk(false);
                rsp.setMsg("未找到订单");
                return rsp;
            }
            request.setReturnUrl(initBean.transferNotifyUrl);
            request.setNotifyUrl(initBean.transferbackNotifyUrl);
            bussOrderNo = info.getOrderId();
            createTime = info.getCreateTime();

        }


        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String orderTime = d.format(createTime);
        String expireTime = null;



        request.setOrderId(bussOrderNo);

        request.setTradeOrderId(tradeOrderId);

        request.setSrcChannel("2");

        request.setOrderTime(orderTime);
        request.setExpireTime(expireTime);
        request.setSignType("MD5");
        request.setSrcIp(ip);

        String sign = SignService.CalSign(request, initBean.newPayKey);

        request.setSign(sign);

        try
        {
            TradeResponse response = tradeRpcService.resendQuickVC(request);
            switch (response.getResultCode()){
                case "3":
                    //成功
                    rsp.setOk(true);
                    rsp.setMsg(response.getResultMsg());
                    break;
                case "4":
                    //失败
                    rsp.setOk(false);
                    rsp.setMsg(response.getResultMsg());
            }
        }
        catch (BusinessException e){
            logger.error(e);
            rsp.setOk(false);
            rsp.setMsg(e.getMessage());
        }
        catch (Exception e){
            logger.error(e);
            rsp.setOk(false);
            rsp.setMsg("系统繁忙，请稍后再试!");
        }

        return rsp;
    }

    @Override
    public QuickPayValRsp validateQuickPayMsg(QuickPayMsgInfoReq data) {

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        if(tradeRpcService==null){
            logger.error("系统错误,获取TradeRpcService 失败");
            return null;
        }

        QuickPayValRsp rsp = new QuickPayValRsp();

        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String orderTime = null;
        String expireTime = null;
        String busOrderId = null;

        VerifyQuickPayRequest request =  new VerifyQuickPayRequest();

        switch (data.getType()){
            case transfer_order:

                TransferOrderInfo tradeOrder = transferOrderDao.getByTradeOrderId(data.getTradeOrderId());
                if(tradeOrder==null){
                    logger.error("未找到充值订单");
                    rsp.setResultCode("5");
                    rsp.setResultMsg("未找到充值订单");
                    return null;
                }
                orderTime = d.format(tradeOrder.getCreateTime());
                if(tradeOrder.getExpireTime()!=null)
                    expireTime = d.format(tradeOrder.getExpireTime());
                busOrderId = tradeOrder.getOrderId();
                request.setReturnUrl(initBean.transferNotifyUrl);
                request.setNotifyUrl(initBean.transferbackNotifyUrl);
                break;
            case charge_order:
                ChargeOrderInfo chargeOrder = dao.getByeTradeOrderNo(data.getTradeOrderId());
                if(chargeOrder==null){
                    logger.error("未找到充值订单");
                    rsp.setResultCode("5");
                    rsp.setResultMsg("未找到充值订单");
                    return rsp;
                }

                orderTime = d.format(chargeOrder.getCreateTime());
                busOrderId = chargeOrder.getChargeBussOrderNo();
                request.setReturnUrl(initBean.chargefrontNotifyUrl);
                request.setNotifyUrl(initBean.chargebackNotifyUrl);
                break;
        }



        request.setVerifyCode(data.getVerifyCode());
        request.setTradeOrderId(data.getTradeOrderId());
        request.setAttach(data.getAttach());

        request.setSrcChannel(data.getSrcChannel());

        request.setOrderTime(orderTime);
        request.setExpireTime(expireTime);
        request.setSignType("MD5");
        request.setOrderId(busOrderId);
        request.setSrcIp(data.getIp());


        String sign = SignService.CalSign(request, initBean.newPayKey);

        request.setSign(sign);




        try {
            TradeResponse tradeResponse = tradeRpcService.verifyQuickPay(request);
            logger.info("短信验证结果:"+JSONObject.toJSONString(tradeResponse));
            if(tradeResponse!=null){
                rsp.setResultCode(tradeResponse.getResultCode());
                rsp.setResultMsg(tradeResponse.getResultMsg());
                rsp.setTradeOrderId(tradeResponse.getTradeOrderId());
                rsp.setPayUrl(tradeResponse.getPayUrl());
                rsp.setPayParams(tradeResponse.getPayParams());
//                //更新充值结果
//                Pair<Integer,String> state = ChargeOrderDao.ChargeCodeToState(tradeResponse.getResultCode());
//                dao.update(orderInfo.getId(),state.first, state.second);
            }
        }catch (Exception e ){
            logger.error(e);
            rsp.setResultCode(ErrorCode.sys_error.code);
            rsp.setResultMsg(e.getMessage());
        }



        return rsp;
    }

    @Override
    public String echo(String in) {
        return in;
    }

    @Override
    public TransferCheckRsp checkCanTransferTo(TransferCheckReq data) {

        TransferCheckRsp rsp = new TransferCheckRsp();


        if(data.validateSelf()==false){
            rsp.setCanTransfer(false);
            rsp.setMsg("参数错误或缺失");
            return rsp;
        }

        QueryUserInfoService  queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
        if(queryUserInfoService ==null){
            logger.error("系统错误,获取TradeRpcService 失败");
            rsp.setCanTransfer(false);
            rsp.setMsg("系统错误，请稍后再试");
            return rsp;
        }


        UserVO fromUser = queryUserInfoService.getUserInfo(data.getFromUserCode());
        if(fromUser==null){
            rsp.setCanTransfer(false);
            rsp.setMsg("转出用户不存在");
            return rsp;
        }

        UserVO toUser = queryUserInfoService.getUserInfo(data.getToUserCode());
        if(toUser==null){
            rsp.setCanTransfer(false);
            rsp.setMsg("转入用户不存在");
            return rsp;
        }

        data.setToUserCode(toUser.getUserCode());

        if(data.getFromUserCode().equals(data.getToUserCode())){
            rsp.setCanTransfer(false);
            rsp.setMsg("不能转账到自己账户");
            return rsp;
        }

        logger.info("转入账户用户信息"+JSONObject.toJSONString(toUser));

        if(toUser.getRealname()!=null){
            if(toUser.getRealname().equals(data.getRealName())==false){
                rsp.setCanTransfer(false);
                rsp.setMsg("用户姓名不正确");
                return rsp;
            }
        }


        AccountRpcService accountRpcService = dubboClient.getDubboClient("accountRpcService");
        boolean isTongxingUser =true;// accountRpcService.checkUserValid(data.getToUserCode());


        if(isTongxingUser==false){
            rsp.setCanTransfer(false);
            rsp.setMsg("系统升级，暂不支持向此用户转款!");
            return rsp;
        }


        //检查限额
        rsp.setCanTransfer(true);
        rsp.setMsg("OK");

        return rsp;
    }

    @Override
    public CreateChargeRsp createChargeOrder(CreateChargeReq req) {

        CreateChargeRsp rsp = new CreateChargeRsp();
        QueryUserInfoService queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
        if(queryUserInfoService==null){
            logger.error("获取接口 queryUserInfoService，失败");
            rsp.setResultCode(ErrorCode.sys_busy.code);
            rsp.setResultMsg(ErrorCode.sys_busy.desc);
            return rsp;
        }
        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        if(tradeRpcService==null){
            logger.error("获取接口 tradeRpcService，失败");
            rsp.setResultCode(ErrorCode.sys_busy.code);
            rsp.setResultMsg(ErrorCode.sys_busy.desc);
            return rsp;
        }



        String attach="";
        if(req.getType()==1){
            attach = "已有卡支付";
        }else if(req.getType()==2){
            attach = "绑卡并支付";
        }else if(req.getType()==3) {
            attach = "网银支付";
        }

        req.setAttach(attach);

        if(req.getType()==1 || req.getType()==2){

            Pair<Boolean, String> passwd = signService.checkUserPayPasswd(req.getUserCode(), req.getPayPwd());

            if(passwd.first==false){
                logger.error("支付密码错误");
                rsp.setResultCode(ErrorCode.paypwd_error.code);
                rsp.setResultMsg(passwd.second);
                return rsp;
            }
        }

        ChargeOrderInfo orderInfo = createChargeQuckPay(req);
        if(orderInfo==null){
            rsp.setResultCode(ErrorCode.sys_error.code);
            rsp.setResultMsg(ErrorCode.sys_error.desc);
            return rsp;
        }

        ChargeRequest charge = new ChargeRequest();

        UserVO uservo = queryUserInfoService.getUserInfo(req.getUserCode());

        charge.setPayerUserId(req.getUserCode());
        charge.setAmount(req.getAmount());
        charge.setOrderId(orderInfo.getChargeBussOrderNo());
        charge.setReturnUrl(initBean.chargefrontNotifyUrl);
        charge.setNotifyUrl(initBean.chargebackNotifyUrl);
        charge.setOrderTime(new SimpleDateFormat("yyyyMMddHHmmss").format(orderInfo.getCreateTime()));
        charge.setSignType("MD5");
        charge.setBusinessType(BusinessType.type_2013.type+"");
        charge.setChannelId(initBean.channelId);
        charge.setSrcIp(req.getIp());
        charge.setSrcChannel("1");
        charge.setAttach(attach);

        if(req.getType()==1){
            logger.info("已有卡支付");
            //已有卡支付
            charge.setSrcChannel("1");
            charge.setBindCardFlag(false);
            charge.setBindNo(req.getBindNo());
        }else  if(req.getType()==2 ) {
            logger.info("绑卡并支付");
            //绑卡并支付
            charge.setBindCardFlag(true);
            if((req.getCardType()!=1 && req.getCardType()!=2)){
                rsp.setResultCode(ErrorCode.para_error.code);
                rsp.setResultMsg(ErrorCode.para_error.desc);
                return rsp;
            }

            if(req.getCardType()==2){
                if (StringUtil.isNull(req.getCvv(), req.getExpireDate())){
                    rsp.setResultCode(ErrorCode.para_error.code);
                    rsp.setResultMsg(ErrorCode.para_error.desc);
                    return rsp;
                }
            }

            if (uservo.getIsAuth() == 1) {
                logger.info("设置用户信息");
                //已实名
                charge.setRealName(uservo.getRealname());
                charge.setIdentityNo(uservo.getIdentityid());

            } else {
                //未实名
                if (StringUtil.isNull(req.getRealName(), req.getIdentityNo(), req.getMobileNo())) {
                    rsp.setResultCode(ErrorCode.para_error.code);
                    rsp.setResultMsg(ErrorCode.para_error.desc);
                    return rsp;
                }
                charge.setRealName(req.getRealName());
                charge.setIdentityNo(req.getIdentityNo());
            }


            charge.setBindType(1);
            charge.setCardActType(1);
            charge.setCardType(req.getCardType());
            charge.setCardNo(req.getCardNo());
            charge.setMobileNo(req.getMobileNo());
            charge.setBankId(req.getBankId());

            if(req.getCardType()==2){
                //信用卡，设置cvv
                charge.setCvv(req.getCvv());
                charge.setExpireDate(req.getExpireDate());
            }

        }else if(req.getType()==3){
            if(StringUtil.isNull(req.getBankId())){
                rsp.setResultCode(ErrorCode.para_error.code);
                rsp.setResultMsg(ErrorCode.para_error.desc);
                return rsp;
            }

            charge.setBankId(req.getBankId());
        }

        String sign = SignService.CalSign(charge, initBean.newPayKey);

        charge.setSign(sign);




        try {
            logger.info("调用支付，报文" + JSONObject.toJSONString(charge));
            TradeResponse response = tradeRpcService.charge(charge);

            logger.info("response+" + JSONObject.toJSONString(response));

            rsp.setResultCode(response.getResultCode());
            rsp.setResultMsg(response.getResultMsg());
            rsp.setOrderId(orderInfo.getChargeBussOrderNo());
            rsp.setTradeOrderId(response.getTradeOrderId());
            //rsp.setPayValue();
            rsp.setAttach(orderInfo.getAttach());
            rsp.setPayUrl(response.getPayUrl());
            rsp.setPayParams(response.getPayParams());
            rsp.setCodeImgUrl(response.getCodeImgUrl());

            Pair<Integer,String> state = ChargeOrderDao.ChargeCodeToState(response.getResultCode());
            dao.update(orderInfo.getId(),response.getTradeOrderId(),state.first,state.second,req.getType(),req.getCardType(),req.getBindNo(),orderInfo.getAmount());

        }catch (BusinessException e){
            e.printStackTrace();
            logger.error("支付rpc异常" + e);
            logger.error(e.getMessage());
            e.printStackTrace();
            rsp.setResultCode(ErrorCode.sys_error.code);
            rsp.setResultMsg(e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error("支付rpc异常" + e);
            logger.error(e.getMessage());
            e.printStackTrace();
            rsp.setResultCode(ErrorCode.sys_busy.code);
            rsp.setResultMsg(ErrorCode.sys_busy.desc);
        }
        return  rsp;
    }


    /**
     * 创建转账订单
     * @param req
     * @return
     */
    @Override
    public TransferOrderCrtRsp createTransferOrder(TransferOrderCrtReq req) {

        TransferOrderCrtRsp rsp = new TransferOrderCrtRsp();

        TransferCheckReq checkReq = new TransferCheckReq();
        checkReq.setFromUserCode(req.getFromUserCode());
        checkReq.setToUserCode(req.getToUserCode());
        checkReq.setRealName(req.getRealName());
        checkReq.setAmount(req.getAmount());
        checkReq.setPhone(req.getPhone());

        TransferCheckRsp checkRsp  = checkCanTransferTo(checkReq);
        if(checkRsp.isCanTransfer()==false){
            rsp.setMsg("检验失败" + checkRsp.getMsg());
            return rsp;
        }


        QueryUserInfoService queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
        UserVO toUser = queryUserInfoService.getUserInfo(req.getToUserCode());

        TransferOrderInfo orderInfo = new TransferOrderInfo();

        orderInfo.setOrderId(UtilTool.generateTransferOrderId());
        orderInfo.setFromUserCode(req.getFromUserCode());
        orderInfo.setToUserCode(req.getToUserCode());
        orderInfo.setAmount(req.getAmount());

        orderInfo.setReceiverType(1);

        orderInfo.setAttach(req.getNote());
        orderInfo.setRealName(req.getRealName());
        orderInfo.setPhone(req.getPhone());
        orderInfo.setCreateTime(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(orderInfo.getCreateTime());
        cal.add(Calendar.SECOND, initBean.maxChargeOrderAliveSec);
        orderInfo.setExpireTime(cal.getTime());

        orderInfo.setChannelId(initBean.channelId);
        orderInfo.setBusinessType(BusinessType.type_2014.type+"");
        orderInfo.setRealName(toUser.getRealname());
        orderInfo.setReference(req.getReference());
        orderInfo.setOrderState(TransferOrderDao.OrderState.wait_pay.state);
        orderInfo.setOrderStateDesc(TransferOrderDao.OrderState.wait_pay.desc);
        orderInfo.setCreateUserCode(req.getFromUserCode());

        orderInfo = transferOrderDao.createOrder(orderInfo);

        rsp.setOrderInfo(orderInfo);

        return rsp;
    }

    @Override
    public PageResult<TransferOrderInfo> queryTransferOrder(int pageNo, int pageSize, Date startTime, Date endTime, String orderId,String userCode) {
        return transferOrderDao.queryTransferOrder(pageNo,pageSize,startTime,endTime,orderId,userCode);
    }

    @Override
    public TransferOrderInfo queryTransferDetail(String orderId) {
       return transferOrderDao.getByOrderId(orderId);
    }

    /**
     * 支付转账订单
     * @param pay
     * @return
     */

    @Override
    public TransferOrderPayResp payTransferOrder(TransferOrderPayReq pay) {

        TransferOrderPayResp rsp = new TransferOrderPayResp();

        TransferOrderInfo orderInfo = transferOrderDao.getByOrderId(pay.getOrderId());
        if(orderInfo==null){
            logger.error("未找到充值订单");
            return null;
        }

        Pair<Boolean, String> passwd = signService.checkUserPayPasswd(orderInfo.getCreateUserCode(), pay.getPaypwd());

        if(passwd.first==false){
            logger.error("支付密码错误");
            rsp.setResultCode(ErrorCode.paypwd_error.code);
            rsp.setResultMsg(passwd.second);
            return rsp;
        }

        TransferRequest request = new TransferRequest();
        request.setOrderId(orderInfo.getOrderId());
        request.setPayerUserId(orderInfo.getFromUserCode());
        request.setPayeeUserId(orderInfo.getToUserCode());
        request.setPayType(pay.getType());
        request.setReceiverType(orderInfo.getReceiverType());
        request.setAmount(orderInfo.getAmount() + "");
        request.setBindNo(pay.getBindNo());
        request.setAttach(orderInfo.getAttach());
        request.setSrcChannel("1");
        request.setReturnUrl(initBean.transferNotifyUrl);
        request.setNotifyUrl(initBean.transferbackNotifyUrl);
        request.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getCreateTime()));
        if(orderInfo.getExpireTime()!=null)
            request.setExpireTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getExpireTime()));
        request.setIsUsePwd(orderInfo.getIsUsePwd() + "");
        request.setChannelId(orderInfo.getChannelId());
        request.setBusinessType(BusinessType.type_2014.type+"");
        request.setSignType("MD5");
        request.setSrcIp(pay.getIp());


        String sign = SignService.CalSign(request, initBean.newPayKey);
        request.setSign(sign);

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        if(tradeRpcService == null){
            logger.error("获取TradeRpcService 失败!");
            rsp.setResultCode(ErrorCode.sys_error.code);
            rsp.setResultMsg(ErrorCode.sys_error.desc);
            return rsp;
        }

        try {
            TradeResponse resp = tradeRpcService.transfer(request);
            rsp.setTradeOrderId(resp.getTradeOrderId());
            rsp.setOrderId(orderInfo.getOrderId());
            rsp.setResultCode(resp.getResultCode());
            rsp.setResultMsg(resp.getResultMsg());
            rsp.setAttach(orderInfo.getAttach());
            rsp.setPayUrl(resp.getPayUrl());
            rsp.setPayParams(resp.getPayParams());

            //更新转账订单状态
            TransferOrderDao.OrderState state = TransferOrderDao.GetState(resp.getResultCode());
            transferOrderDao.update(orderInfo.getId(),state.state,state.desc,resp.getTradeOrderId(), request.getPayType(),pay.getBindNo());

        }catch (Exception e) {
            logger.error(e);
            rsp.setResultCode(ErrorCode.sys_error.code);
            rsp.setResultMsg(e.getMessage());
        }

        return rsp;
    }

    @Override
    public UnBindBankCardRsp unBindBankCard(String userCode, String bindNo, String paypwd) {
        UnBindBankCardRsp rsp = new UnBindBankCardRsp();

        Pair<Boolean, String> isOk =  signService.checkUserPayPasswd(userCode, paypwd);

        if(isOk.first==false){
            rsp.setIsOk(false);
            rsp.setMsg(isOk.second);
            return  rsp;
        }

        AccountRpcService accountRpcService = dubboClient.getDubboClient("accountRpcService");

        try{
            accountRpcService.unbindCard(userCode,bindNo);
        }catch (Exception e){
            logger.error("发生异常"+e);
            e.printStackTrace();
            rsp.setIsOk(false);
            rsp.setMsg(e.getMessage());
            return  rsp;
        }

        rsp.setIsOk(true);
        rsp.setMsg("成功");
        return  rsp;
    }

    @Override
    public BindNewCardRsp bindNewBankCard(BindNewCardReq req) {


        BindNewCardRsp rsp = new BindNewCardRsp();

        AccountRpcService accountRpcService = dubboClient.getDubboClient("accountRpcService");
        QueryUserInfoService  queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");


        BindCardRequest bindReq = new BindCardRequest();

        bindReq.setUserId(req.getUserCode());
        bindReq.setBindType(req.getBindType());
        bindReq.setCardActType(1);
        bindReq.setCardType(req.getCardType());
        bindReq.setCardNo(req.getCardNo());
        bindReq.setCvv(req.getCvv());
        bindReq.setExpireDate(req.getExpireDate());


        UserVO  uservo = queryUserInfoService.getUserInfo(req.getUserCode());

        if(uservo.getIsAuth()==0){
            logger.error("该用户未实名，不能绑卡");

            rsp.setIsOk(false);
            rsp.setMsg("未实名认证，不能绑卡!");
            return rsp;
        }

        bindReq.setRealName(uservo.getRealname());
        bindReq.setIdentityNo(uservo.getIdentityid());
        bindReq.setMobileNo(req.getMobileNo());
        bindReq.setChannelId(initBean.channelId);
        bindReq.setIsWithdrawCard(0);

        String bindNO;

        try{
            bindNO = accountRpcService.bindCard(bindReq);
        }catch (Exception e){
            logger.error("绑卡失败,系统异常"+e);
            logger.error("msg:"+e.getMessage());
            rsp.setIsOk(false);
            rsp.setMsg(e.getMessage());
            return rsp;
        }

        if(bindNO!=null && bindNO.trim().isEmpty()==false) {
            rsp.setIsOk(true);
            rsp.setMsg("成功");
            rsp.setBindNo(bindNO);
        }else{
            rsp.setIsOk(false);
            rsp.setMsg("系统繁忙，请稍后再试");
        }
        return rsp;
    }

    @Override
    public ConfirmBindMsgRsp confirmBindMsg(ConfirmBindMsgReq req) {

        AccountRpcService accountRpcService = dubboClient.getDubboClient("accountRpcService");

        ConfirmBindMsgRsp rsp = new ConfirmBindMsgRsp();

        boolean ret;

        try {
            ret = accountRpcService.verifyBindCard(req.getBindNo(), req.getVerifyCode());
            rsp.setIsOk(ret);
            if(ret==false ) {
                rsp.setMsg("系统繁忙，请稍后再试");
            }else{
                rsp.setMsg("成功");
            }
        }catch (Exception e){
            logger.error("发生异常:"+e);
            logger.error("发生异常:" + e.getMessage());
            rsp.setMsg(e.getMessage());
        }



        return rsp;
    }

    @Override
    public boolean reSendBindMsg(String userCode, String bindNo) {

        AccountRpcService accountRpcService = dubboClient.getDubboClient("accountRpcService");

        boolean ret;

        try{
            ret = accountRpcService.resendVerifyCode(bindNo);
        }
        catch (Exception e){
            logger.error("发生异常:"+e);
            ret = false;
        }


        return ret;
    }

    @Override
    public ValNotifyRsp valFrontNotify(Map<String, String[]> params, NotifyOrderType type) {
        return valNotify(params,type,NotifyType.front_notify);
    }

    @Override
    public ValNotifyRsp  valBackNotify(Map<String, String []> params,NotifyOrderType type){
        return valNotify(params,type,NotifyType.back_notify);
    }

    private ValNotifyRsp valNotify(Map<String, String[]> params, NotifyOrderType notifyOrderType, NotifyType type) {

        logger.info("收到回调"+params+notifyOrderType+type);
        ValNotifyRsp rsp = new ValNotifyRsp();

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
            rsp.setCode(ValNotifyRsp.CODE.exception);
            rsp.setErrorMsg("验签失败");
            return rsp;
        }


        /**
         * 所有参数
         */
        String outOrderId = paramMap.get("outOrderId");
        String tradeOrderId = paramMap.get("tradeOrderId");
        String orderState = paramMap.get("orderState");
        String orderType = paramMap.get("orderType");

        rsp.setOrderId(outOrderId);
        rsp.setTradeOrderId(tradeOrderId);

        if(notifyOrderType== NotifyOrderType.charge_notify){

            ChargeOrderInfo orderInfo = dao.getByBusOrderNo(outOrderId);
            if(orderInfo==null){
                logger.error("订单未找到");
                rsp.setCode(ValNotifyRsp.CODE.exception);
                rsp.setErrorMsg("订单未找到");
                return rsp;
            }


            //更新订单状态
            int state;
            String stateDesc;

            switch (orderState) {
                case "1":
                    state = 2;
                    stateDesc = "成功";
                    rsp.setCode(ValNotifyRsp.CODE.succ);
                    rsp.setErrorMsg("成功");
                    break;
                case "2":
                    state = 3;
                    stateDesc = "失败";
                    rsp.setCode(ValNotifyRsp.CODE.fail);
                    rsp.setErrorMsg("失败");
                    break;
                case "3":
                    state = 4;
                    stateDesc = "未知-异常";
                    rsp.setCode(ValNotifyRsp.CODE.exception);
                    rsp.setErrorMsg("回调状态异常");
                    break;
                default:
                    state = 4;
                    stateDesc = "未知-通知状态错误";
                    rsp.setCode(ValNotifyRsp.CODE.exception);
                    rsp.setErrorMsg("回调状态异常");
            }

            if(type == NotifyType.back_notify) {
                logger.info("更新订单状态:"+outOrderId);
                dao.update(orderInfo.getId(), state, stateDesc);
            }

            rsp.setAmount(orderInfo.getAmount());

        }else if(notifyOrderType== NotifyOrderType.transfer_notify){

            TransferOrderInfo orderInfo = transferOrderDao.getByOrderId(outOrderId);
            if(orderInfo==null){
                logger.error("订单未找到");
                rsp.setCode(ValNotifyRsp.CODE.exception);
                rsp.setErrorMsg("订单未找到");
                return rsp;
            }
            if(type == NotifyType.back_notify) {
                //更新订单状态
                int state;
                String stateDesc;

                switch (orderState) {
                    case "1":
                        state = 2;
                        stateDesc = "成功";
                        rsp.setCode(ValNotifyRsp.CODE.succ);
                        rsp.setErrorMsg("成功");
                        break;
                    case "2":
                        state = 3;
                        stateDesc = "失败";
                        rsp.setCode(ValNotifyRsp.CODE.fail);
                        rsp.setErrorMsg("失败");
                        break;
                    case "3":
                        state = 4;
                        stateDesc = "未知-异常";
                        rsp.setCode(ValNotifyRsp.CODE.exception);
                        rsp.setErrorMsg("回调状态异常");
                        break;
                    default:
                        state = 4;
                        stateDesc = "未知-通知状态错误";
                        rsp.setCode(ValNotifyRsp.CODE.exception);
                        rsp.setErrorMsg("回调状态异常");
                }

                transferOrderDao.update(orderInfo.getId(), state, stateDesc);
            }
            rsp.setAmount(orderInfo.getAmount());
            rsp.setPayeeName(MyUtils.markName(orderInfo.getRealName()));
        }else if(notifyOrderType== NotifyOrderType.withdraw_notify){

            WithdrawOrderInfo orderInfo = withdrawOrderDao.getByOrderId(outOrderId);
            if(orderInfo==null){
                logger.error("订单未找到");
                return rsp;
            }
            if(type == NotifyType.back_notify) {
                //更新订单状态
                int state;
                String stateDesc;

                switch (orderState) {
                    case "1":
                        state = 2;
                        stateDesc = "成功";
                        rsp.setCode(ValNotifyRsp.CODE.succ);
                        rsp.setErrorMsg("成功");
                        break;
                    case "2":
                        state = 3;
                        stateDesc = "失败";
                        rsp.setCode(ValNotifyRsp.CODE.fail);
                        rsp.setErrorMsg("失败");
                        break;
                    case "3":
                        state = 4;
                        stateDesc = "未知-异常";
                        rsp.setCode(ValNotifyRsp.CODE.exception);
                        rsp.setErrorMsg("回调状态异常");
                        break;
                    default:
                        state = 4;
                        stateDesc = "未知-通知状态错误";
                        rsp.setCode(ValNotifyRsp.CODE.exception);
                        rsp.setErrorMsg("回调状态异常");
                }

                withdrawOrderDao.update(orderInfo.getId(), state, stateDesc);
            }
        }


        return rsp;
    }

    @Override
    public ChargeOrderInfo queryChargeOrderInfo(String tradeOrderId) {

        try{
             return  dao.getByeTradeOrderNo(tradeOrderId);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public TransferOrderInfo queryTransferOrderInfo(String tradeOrderId) {

       try{
           return transferOrderDao.getByTradeOrderId(tradeOrderId);
       }catch (Exception e){
           return null;
       }
    }

    @Override
    public List<PayFlowData> queryHisPayFlow(int start, int count, Date startDate, Date endDate, String userid, String orderNo) {

        /*
        B2C_TRADE_ORDER(ReleOrderType,1,"B2C交易"),
		CHARGE_ORDER(ReleOrderType,2,"充值"),
		WITHDRAW_ORDER(ReleOrderType,3,"提现"),
		TRANSFER_ORDER(ReleOrderType,4,"转账"),
		REFUND_ORDER(ReleOrderType,5,"退款"),
		SETTLE_ORDER(ReleOrderType,6,"结算"),
		BANK_PAY_SETTLE(ReleOrderType,7, "支付结算单"),BANK_WITHDRAW_SETTLE(ReleOrderType,8,"提现结算")
		,BANK_REFUND_SETTLE(ReleOrderType,9,"退款结算"),INTEREST(ReleOrderType,10,"利息"),ACT_MANAGE_FEE(ReleOrderType,11,"账户管理费"),
		TRANSFER_FEE(ReleOrderType,12,"转账手续费"),PAGER_FEE(ReleOrderType,13,"单据工本费"),REQUISITION(ReleOrderType,14,"资金调拨单"),REPEAT_PAY_REFUND(ReleOrderType,15,"重复支付退款"),
		OFFLINE_CHARGE(ReleOrderType,16,"线下充值到商户现金账户"),OFFLINE_WITHDRAW(ReleOrderType,17,"线下提现"),
		DISTRIBUTION_SETTLE_ORDER(ReleOrderType,18,"代发结算订单"),OFFLINE_CHARGE_TO_DEPOSIT(ReleOrderType,19,"线下充值到商户保证金账户"),
		PARTNER_SETTLE_WITH_TRANSFER_ORDER(ReleOrderType,20,"WIFI商城结算订单"),
		CHARGE_REFUND_ORDER(ReleOrderType,21,"充值退款"),MANUAL_SETTLE_ORDER(ReleOrderType,22,"人工结算"),
		FINANCING_PURCHASE_ORDER(ReleOrderType,23,"基金购买"),FINANCING_REDEEM_ORDER(ReleOrderType,24,"基金赎回"),
		FINANCING_PROFIT_ORDER(ReleOrderType,25,"基金收益");


		=>  类型 1 B2C 2充值 3 余额提现 4返利金提现 5 转账 6退款 7 结算 8绑卡 9交易手续费 10结算返利金 11 商户结算 12 灰度用户 13 余额迁移
         */

        int [][] a =new int[][]{{0,0},{1,1},{2,2},{3,3},{4,5},{5,6},{6,7},{7,7},{8,7},{9,7},{10,7},{11,7},{12,9},{13,9},{14,7},{15,6},{16,2},{17,3},{18,7},{19,7},{20,7},{21,6},{22,7},{23,7},{24,7}};


        List<PayFlowData> flows = new ArrayList<>();
        List<UserActFlow> data = userActFlowDao.query(start, count, startDate, endDate, userid, orderNo);
        if(data!=null){
            for(UserActFlow flow:data){
                PayFlowData d = new PayFlowData();


                d.setTradeOrderNo(flow.pay_no);
                d.setAmount(MyUtils.fen2yuan(flow.tran_amount).toString());
                d.setBalance(MyUtils.fen2yuan(flow.act_balance).toString());
                d.setPayDate(flow.trade_time);
                if(flow.order_type>0 && flow.order_type < a.length)
                    d.setTradeOrderType(a[flow.order_type][1]);
                d.setOtherAccount(flow.opposite_side);
                if(flow.dc_type==1)
                    d.setType("out");
                else if(flow.dc_type==2)
                    d.setType("in");

                flows.add(d);

            }
        }

        return flows;
    }

    @Override
    public long queryHisPayFlowCount( Date startDate, Date endDate, String userid, String orderNo) {

        return userActFlowDao.getCount(startDate,endDate,userid,orderNo);
    }




}
