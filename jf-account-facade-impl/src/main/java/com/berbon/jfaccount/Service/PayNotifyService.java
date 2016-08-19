package com.berbon.jfaccount.Service;

import com.alibaba.fastjson.JSON;
import com.berbon.jfaccount.comm.InitBean;
import com.berbon.jfaccount.pojo.NotifyData;
import com.berbon.jfaccount.pojo.PayNotifyData;
import com.berbon.msgsrv.proxy.facade.MsgSrvProxyFacade;
import com.berbon.msgsrv.proxy.pojo.ProxyRequestParam;
import com.berbon.util.String.StringUtil;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

/**
 * Created by chj on 2016/8/18.
 */

@Component
public class PayNotifyService {


    private static  Logger logger = LoggerFactory.getLogger(PayNotifyService.class);

    @Autowired
    private MsgSrvProxyFacade msgSrvProxyFacade;

    @Autowired
    private InitBean initBean;

    public  void mobileChargeNotify(PayNotifyData data){

        NotifyData notifyData = new NotifyData();

        notifyData.setCurrency("156");
        notifyData.setMerId(initBean.mobileChargeMerId);
        notifyData.setMerOrderId(data.orderId);
        notifyData.setPayOrderId(data.payOrderId);
        notifyData.setPayTime(new SimpleDateFormat("yyyyMMddHHmmss").format(data.payTime));
        notifyData.setPayValue(data.payAmount + "");
        notifyData.setResultCode("1001");
        notifyData.setResultMsg("支付成功");
        notifyData.setService("mob_payment_res");
        notifyData.setSignType("MD5");
        notifyData.setVersion("1.0.0");

        notifyData.createSign(initBean.mobileChargeSignKey);

        String packetId = StringUtil.getSystemUniqueNo();

        String msgsrvCmd = null;
        try {
            msgsrvCmd = "BerbonMobileChargeNewApp PayResultNotify  ConnId=" + packetId +
                    " NotifyData=" + Base64Utils.encode(JSON.toJSONString(notifyData).getBytes("GBK"))+" Other=";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ProxyRequestParam param = new ProxyRequestParam(1, packetId, 2, msgsrvCmd);

        String result = msgSrvProxyFacade.getResult(param);

        logger.info("通知结果:"+result);

    }


    public void gameChargeNotify(PayNotifyData data){

        NotifyData notifyData = new NotifyData();

        notifyData.setCurrency("156");
        notifyData.setMerId(initBean.mobileChargeMerId);
        notifyData.setMerOrderId(data.orderId);
        notifyData.setPayOrderId(data.payOrderId);
        notifyData.setPayTime(new SimpleDateFormat("yyyyMMddHHmmss").format(data.payTime));
        notifyData.setPayValue(data.payAmount + "");
        notifyData.setResultCode("1001");
        notifyData.setResultMsg("支付成功");
        notifyData.setService("mob_payment_res");
        notifyData.setSignType("MD5");
        notifyData.setVersion("1.0.0");

        notifyData.createSign(initBean.mobileChargeSignKey);

        String packetId = StringUtil.getSystemUniqueNo();

        String msgsrvCmd = null;
        try {
            msgsrvCmd = "GamePayCallClt PayResultNotify  ConnId=" + packetId +
                    " NotifyData=" + Base64Utils.encode(JSON.toJSONString(notifyData).getBytes("GBK"))+" Other=";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ProxyRequestParam param = new ProxyRequestParam(1, packetId, 2, msgsrvCmd);

        String result = msgSrvProxyFacade.getResult(param);

        logger.info("通知结果:"+result);

    }

}
