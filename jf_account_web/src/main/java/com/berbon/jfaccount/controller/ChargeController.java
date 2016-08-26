package com.berbon.jfaccount.controller;

import com.berbon.jfaccount.commen.CheckLoginInterceptor;
import com.berbon.jfaccount.commen.InitBean;
import com.berbon.jfaccount.commen.JsonResult;
import com.berbon.jfaccount.commen.ResultAck;
import com.berbon.jfaccount.facade.pojo.*;
import com.berbon.jfaccount.utils.IpTool;
import com.berbon.jfaccount.utils.SignUtil;
import com.berbon.user.pojo.Users;
import com.berbon.util.String.StringUtil;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import com.sun.tools.corba.se.idl.StringGen;
import com.sztx.pay.center.rpc.api.domain.BindCardRequest;
import com.sztx.pay.center.rpc.api.domain.ChargeRequest;
import com.sztx.pay.center.rpc.api.domain.TradeResponse;
import com.sztx.pay.center.rpc.api.service.AccountRpcService;
import com.sztx.pay.center.rpc.api.service.TradeRpcService;
import com.sztx.usercenter.rpc.api.domain.out.UserVO;
import com.sztx.usercenter.rpc.api.service.QueryUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;

/**
 * Created by chj on 2016/8/5.
 */


@Controller
@RequestMapping("charge")
public class ChargeController {

    private static Logger logger = LoggerFactory.getLogger(ChargeController.class);

    @Autowired
    private com.sztx.se.rpc.dubbo.source.DynamicDubboClient dubboClient;

    private QueryUserInfoService queryUserInfoService;

    private TradeRpcService tradeRpcService;

    private AccountRpcService accountRpcService;

    private com.berbon.jfaccount.facade.AccountFacade accountFacade;

    @ModelAttribute
    public  void init() {
        accountRpcService = dubboClient.getDubboClient("accountRpcService");
        queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
        tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        accountFacade = dubboClient.getDubboClient("accountFacade");
    }



    @Autowired
    private InitBean initBean;


    /**
     * 绑定快捷支付,充值
     */
    @RequestMapping(value = "/bindCardAndPay" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult bindQuickPay(HttpServletRequest request) {


        JsonResult  result =new JsonResult();


        String bindNo = request.getParameter("bindNo");
        Integer amount = Integer.parseInt(request.getParameter("amount"));
        int type =   Integer.parseInt(request.getParameter("type"));

        String cardType = request.getParameter("cardType");
        String cardNo = request.getParameter("cardNo");
        String cvv = request.getParameter("cvv");
        String expireDate = request.getParameter("expireDate");
        String realName =  request.getParameter("realName");
        String identityNo = request.getParameter("identityNo");
        String mobileNo = request.getParameter("mobileNo");
        String bankId = request.getParameter("bankId");
        String paypwd = request.getParameter("paypwd");

        Users user = CheckLoginInterceptor.getUsers(request.getSession());

        CreateChargeReq data =  new CreateChargeReq();
        data.setUserCode(user.getUserCode());
        data.setSrcChannel(1);

        data.setType(type);
        data.setBindNo(bindNo);

        if(cardType!=null && cardType.isEmpty()==false)
            data.setCardType(Integer.parseInt(cardType));

        data.setCardNo(cardNo);
        data.setCvv(cvv);
        data.setExpireDate(expireDate);
        data.setRealName(realName);
        data.setIdentityNo(identityNo);
        data.setMobileNo(mobileNo);
        data.setAmount(amount);
        data.setBankId(bankId);
        data.setPayPwd(paypwd);

        data.setIp(IpTool.getIp(request));

        CreateChargeRsp rsp = accountFacade.createChargeOrder(data);
        result.setResult(ResultAck.succ);
        result.setData(rsp);

        return result;
    }

    @RequestMapping(value = "/resendMsg", method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult reSendMsg(HttpServletRequest request){

        String tradeOrderId = request.getParameter("tradeOrderId");

        JsonResult result  = new JsonResult();

        ReSendChargeValMsgRsp rsp = accountFacade.reSendQuickValMsg(tradeOrderId, BusOrderType.charge_order, IpTool.getIp(request));

        result.setResult(ResultAck.succ);
        result.setData(rsp);

        return result;
    }

    @RequestMapping(value = "/validateQuickMsg" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult validateQuickPayMsg(HttpServletRequest request){
        JsonResult result =  new JsonResult();

        QuickPayMsgInfoReq req = new QuickPayMsgInfoReq();

        String type = request.getParameter("type");

        req.setVerifyCode(request.getParameter("verifyCode"));
        req.setTradeOrderId(request.getParameter("tradeOrderId"));
        req.setSrcChannel("1");
        req.setIp(IpTool.getIp(request));
        switch (type){
            case "charge":
                req.setType(BusOrderType.charge_order);
                break;
            case "transfer":
                req.setType(BusOrderType.transfer_order);
                break;
            default:
                logger.error("参数错误");
                result.setResult(ResultAck.para_error);
                return result;
        }

        if(StringUtil.isNull(req.getVerifyCode(),req.getTradeOrderId())){
            logger.error("参数错误");
            result.setResult(ResultAck.para_error.getCode());
            result.setRetinfo(ResultAck.para_error.getDesc());
            return  result;
        }

        QuickPayValRsp rsp = accountFacade.validateQuickPayMsg(req);
        if(rsp!=null){
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
            result.setData(rsp);
        }else{
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
        }

        return result;
    }

}
