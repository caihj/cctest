package com.berbon.jfaccount.impl;

import com.berbon.jfaccount.Dao.ChargeOrderDao;
import com.berbon.jfaccount.Dao.TransferOrderDao;
import com.berbon.jfaccount.comm.InitBean;
import com.berbon.jfaccount.facade.AccountFacade;
import com.berbon.jfaccount.facade.pojo.*;
import com.berbon.jfaccount.util.SignUtil;
import com.berbon.jfaccount.util.UtilTool;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import com.sztx.pay.center.rpc.api.domain.TradeResponse;
import com.sztx.pay.center.rpc.api.domain.VerifyQuickPayRequest;
import com.sztx.pay.center.rpc.api.service.TradeRpcService;
import com.sztx.se.rpc.dubbo.source.DynamicDubboClient;
import com.sztx.usercenter.rpc.api.domain.out.UserVO;
import com.sztx.usercenter.rpc.api.service.QueryUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chj on 2016/8/5.
 */

@Service
public class AccountFacadeImpl implements AccountFacade {


    private static Logger logger = LoggerFactory.getLogger(AccountFacadeImpl.class);

    @Autowired
    private ChargeOrderDao dao;

    private TransferOrderDao transferOrderDao;

    @Autowired
    private DynamicDubboClient dubboClient;

    @Autowired
    private InitBean initBean;

    @Override
    public ChargeOrderInfo createChargeQuckPay(ChargeReqData data) {

        String orderId = UtilTool.createOrderId(UtilTool.ChargeOrderPrefix);

        ChargeOrderInfo info = new ChargeOrderInfo();

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


        info = dao.newOrder(info);

        return info;
    }

    @Override
    public QuickPayValRsp validateQuickPayMsg(QuickPayMsgInfoReq data) {

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        if(tradeRpcService==null){
            logger.error("系统错误,获取TradeRpcService 失败");
            return null;
        }

        ChargeOrderInfo orderInfo = dao.getByeTradeOrderNo(data.getTradeOrderId());
        if(orderInfo==null){
            logger.error("未找到充值订单");
            return null;
        }
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String orderTime = d.format(orderInfo.getCreateTime());
        String expireTime = null;

        VerifyQuickPayRequest request =  new VerifyQuickPayRequest();

        request.setVerifyCode(data.getVerifyCode());
        request.setTradeOrderId(data.getTradeOrderId());
        request.setAttach(data.getAttach());
        //no reference
        request.setSrcChannel(data.getSrcChannel());
        request.setReturnUrl(initBean.frontUrl);
        request.setNotifyUrl(initBean.bakNotifyUrl);
        request.setOrderTime(orderTime);
        request.setExpireTime(expireTime);
        request.setSignType("MD5");

        String sign = SignUtil.CalSign(request,initBean.newPayKey);

        request.setSign(sign);

        TradeResponse tradeResponse =  tradeRpcService.verifyQuickPay(request);

        QuickPayValRsp rsp =null;

        if(tradeResponse!=null){
            rsp.setResultCode(tradeResponse.getResultCode());
            rsp.setResultMsg(tradeResponse.getResultMsg());
            rsp.setTradeOrderId(tradeResponse.getTradeOrderId());
            rsp.setPayUrl(tradeResponse.getPayUrl());
            rsp.setPayParams(tradeResponse.getPayParams());
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

        QueryUserInfoService  queryUserInfoService = dubboClient.getDubboClient("QueryUserInfoService");
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

        if(toUser.getRealname()!=null){
            if(toUser.getRealname().equals(data.getRealName())==false){
                rsp.setCanTransfer(false);
                rsp.setMsg("用户姓名不正确");
                return rsp;
            }
        }

        //检查限额



        return rsp;
    }


    /**
     * 创建转账订单
     * @param req
     * @return
     */
    @Override
    public TransferOrderCrtRsp createTransferOrder(TransferOrderCrtReq req) {
        return null;
    }
}
