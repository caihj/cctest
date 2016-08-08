package com.berbon.jfaccount.controller;

import com.berbon.jfaccount.commen.CheckLoginInterceptor;
import com.berbon.jfaccount.commen.InitBean;
import com.berbon.jfaccount.commen.JsonResult;
import com.berbon.jfaccount.commen.ResultAck;
import com.berbon.jfaccount.facade.pojo.ChargeOrderInfo;
import com.berbon.jfaccount.facade.pojo.ChargeReqData;
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

    @ModelAttribute
    public  void init() {
        accountRpcService = dubboClient.getDubboClient("accountRpcService");
        queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
        tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
    }


    @Autowired
    private com.berbon.jfaccount.facade.AccountFacade accountFacade;

    @Autowired
    private InitBean initBean;



    /**
     * 绑定快捷支付,充值
     */
    @RequestMapping(value = "/bindCardAndPay" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult bindQuickPay(HttpServletRequest request) {

        /*
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

        Users user = CheckLoginInterceptor.getUsers(request.getSession());
        ChargeRequest charge = new ChargeRequest();
        UserVO uservo = queryUserInfoService.getUserInfo(user.getUserCode());

        String attach="";
        if(type==1){
            attach = "已有卡支付";
        }else if(type==2){
            attach = "绑卡并支付";
        }else if(type==3) {
            attach = "网银支付";
        }




        ChargeReqData data =  new ChargeReqData();
        data.setUserCode(user.getUserCode());
        data.setSrcChannel(1);

        data.setType(type);
        data.setBindNo(bindNo);
        data.setCardType(Integer.parseInt(cardType));
        data.setCardNo(cardNo);
        data.setCvv(cvv);
        data.setExpireDate(expireDate);
        data.setRealName(realName);
        data.setIdentityNo(identityNo);
        data.setMobileNo(mobileNo);
        data.setAmount(amount);
        data.setBankId(bankId);


        ChargeOrderInfo order = accountFacade.createChargeQuckPay(data);

        charge.setPayerUserId(user.getUserCode());
        charge.setAmount(amount);
        charge.setOrderId(order.getChargeBussOrderNo());
        charge.setReturnUrl(initBean.frontUrl);
        charge.setNotifyUrl(initBean.bakNotifyUrl);
        charge.setOrderTime(new SimpleDateFormat("yyyyMMddHHmmss").format(order.getCreateTime()));
        charge.setSignType("MD5");

        if(type==1){
            //已有卡支付
            charge.setAttach(attach);
            charge.setSrcChannel("1");
            charge.setBindCard(false);
            charge.setBindNo(bindNo);
        }else  if(type==2 ) {
            //绑卡并支付
            charge.setBindCard(true);
            if(cardType==null || (cardType.equals("1")==false && cardType.equals("2")==false)){
                result.setResult(ResultAck.para_error.getCode());
                result.setRetinfo(ResultAck.para_error.getDesc());
                return result;
            }

            if(cardType.equals("2")){
                if (StringUtil.isNotNull(cvv, expireDate)){
                    result.setResult(ResultAck.para_error.getCode());
                    result.setRetinfo(ResultAck.para_error.getDesc());
                    return result;
                }
            }

            if (uservo.getIsAuth() == 1) {
                //已实名
                charge.setRealName(uservo.getRealname());
                charge.setIdentityNo(uservo.getIdentityid());
            } else {
                //未实名
                if (StringUtil.isNotNull(realName, identityNo, mobileNo)) {
                    result.setResult(ResultAck.para_error.getCode());
                    result.setRetinfo(ResultAck.para_error.getDesc());
                    return result;
                }
                charge.setRealName(realName);
                charge.setIdentityNo(identityNo);
            }


            charge.setBindType(1);
            charge.setCardActType(1);
            charge.setCardType(Integer.parseInt(cardType));
            charge.setCardNo(cardNo);
            charge.setMobileNo(mobileNo);
            charge.setBankId(bankId);

            if(cardType.equals("2")){
                //信用卡，设置cvv
                charge.setCvv(cvv);
                charge.setExpireDate(expireDate);
            }

        }else if(type==3){
            if(StringUtil.isNull(bankId)){
                result.setResult(ResultAck.para_error.getCode());
                result.setRetinfo(ResultAck.para_error.getDesc());
                return result;
            }

            charge.setBankId(bankId);
        }


        String sign = SignUtil.CalSign(charge,initBean.newPayKey);

        charge.setSign(sign);

        try {
            TradeResponse response = tradeRpcService.charge(charge);
            result.setData(response);
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
        }catch (Exception e){
            logger.error("支付rpc异常"+e);
            e.printStackTrace();
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo("系统异常，请稍后再试");
        }



        return result;
        */
        return null;
    }

}
