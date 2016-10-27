package com.berbon.jfaccount.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.berbon.common.facade.SmsFacade;
import com.berbon.common.model.SmsModel;
import com.berbon.jfaccount.commen.JsonResult;
import com.berbon.jfaccount.facade.AccountFacade;
import com.berbon.jfaccount.facade.AccountMobileFacade;
import com.berbon.jfaccount.facade.pojo.NotifyOrderType;
import com.berbon.jfaccount.facade.pojo.TransferOrderInfo;
import com.berbon.jfaccount.facade.pojo.ValNotifyRsp;
import com.berbon.jfaccount.utils.MyUtils;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import com.sztx.bbmall.sms.proj.SmsSendTemplateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by chj on 2016/8/18.
 */

/**
 * 后台通知
 */
@Controller
@RequestMapping("/callback")
public class NotifyController {

    @Autowired
    private com.sztx.bbmall.sms.facade.SmsFacade smsFacade;

    private static Logger logger = LoggerFactory.getLogger(ChargeController.class);

    private static final int instannId = 10 ;

    private static final String JIAOFEI_TRANSFER_RESULT_NOFITY = "JIAOFEI_TRANSFER_RESULT_NOFITY";

    @Autowired
    private com.sztx.se.rpc.dubbo.source.DynamicDubboClient dubboClient;

    private AccountMobileFacade accountMobileFacade;

    private AccountFacade accountFacade;

    @ModelAttribute
    public void init(){
        accountFacade = dubboClient.getDubboClient("accountFacade");
        accountMobileFacade = dubboClient.getDubboClient("accountMobileFacade");
    }

    @RequestMapping(value = "/chargeBackNotify",method = {RequestMethod.POST,RequestMethod.GET})
    public void chargeCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Map<String,String []> params = request.getParameterMap();
        logger.info("收到充值后台回调"+ JSONObject.toJSONString(params));

        accountFacade.valBackNotify(params, NotifyOrderType.charge_notify);
        response.getWriter().write("OK");
    }

    @RequestMapping(value = "/transferBackNotify" , method = {RequestMethod.POST,RequestMethod.GET})
    public void transferNotify(HttpServletRequest request , HttpServletResponse response) throws IOException {

        Map<String,String []> params = request.getParameterMap();
        logger.info("收到转账后台回调" + JSONObject.toJSONString(params));

        ValNotifyRsp rsp =  accountFacade.valBackNotify(params, NotifyOrderType.transfer_notify);

        if(rsp.getCode() == ValNotifyRsp.CODE.succ) {
            String[] orderId = params.get("outOrderId");
            if (orderId != null && orderId.length >= 1) {
                TransferOrderInfo orderInfo = accountFacade.queryTransferDetail(orderId[0]);
                if (orderInfo != null) {
                    //发送短信
                    if (orderInfo.getPhone() != null && orderInfo.getPhone().trim().isEmpty() == false) {

                        SmsSendTemplateRequest smsRequest = new SmsSendTemplateRequest();

                        smsRequest.setPhone(orderInfo.getPhone());
                        smsRequest.setUserID(orderInfo.getFromUserCode());
                        smsRequest.setInstallId(instannId);
                        smsRequest.setCmd(JIAOFEI_TRANSFER_RESULT_NOFITY);

                        String attach = orderInfo.getAttach();
                        if(attach==null){
                            attach="";
                        }

                        /**
                         * 您收到来自%s的转款 %s元，附言：%s
                         */
                        String[] argArr = new String[]{orderInfo.getFromUserCode(), MyUtils.fen2yuan(orderInfo.getAmount()).toString(), attach};
                        smsRequest.setTemplateArray(argArr);
                        logger.info("发送短信:" + JSON.toJSONString(smsRequest));

                        smsFacade.SendTemplate(smsRequest);
                    }
                }
            }
        }

        response.getWriter().write("OK");
    }

    @RequestMapping(value = "/payFrontNotify" , method = {RequestMethod.POST,RequestMethod.GET})
    public void payFrontNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Map<String,String []> params = request.getParameterMap();
        logger.info("收到交易前台回调" + JSONObject.toJSONString(params));
        accountMobileFacade.payFrontCallBack(params);
        response.getWriter().write("OK");
    }

    @RequestMapping(value = "/payBackNotify" , method = {RequestMethod.POST,RequestMethod.GET})
    public void payBackNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Map<String,String []> params = request.getParameterMap();
        logger.info("收到交易后台回调" + JSONObject.toJSONString(params));
        accountMobileFacade.payBackCallBack(params);

        response.getWriter().write("OK");
    }

    @RequestMapping(value = "/withDrawBackNotify",method = {RequestMethod.POST,RequestMethod.GET})
    public void withDrawCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Map<String,String []> params = request.getParameterMap();
        logger.info("收到提现后台回调"+ JSONObject.toJSONString(params));

        accountFacade.valBackNotify(params, NotifyOrderType.withdraw_notify);
        response.getWriter().write("OK");
    }
}
