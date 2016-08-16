package com.berbon.jfaccount.controller;

import com.berbon.jfaccount.commen.CheckLoginInterceptor;
import com.berbon.jfaccount.commen.JsonResult;
import com.berbon.jfaccount.commen.ResultAck;
import com.berbon.jfaccount.facade.AccountFacade;
import com.berbon.jfaccount.facade.pojo.*;
import com.berbon.user.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chj on 2016/8/14.
 */

@Controller
@RequestMapping("bindcard")
public class MyCardController {

    @Autowired
    private com.sztx.se.rpc.dubbo.source.DynamicDubboClient dubboClient;

    private AccountFacade accountFacade;

    @ModelAttribute
    public void init(){
        accountFacade = dubboClient.getDubboClient("accountFacade");
    }

    @RequestMapping(value = "/bind" , method ={ RequestMethod.POST, RequestMethod.GET})
    public String getPage1(){
        return "/myCard/addBankCard";
    }


    @RequestMapping(value = "/bindNewCard.json" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult bindCard(HttpServletRequest request){

        JsonResult result = new JsonResult();

        String bindType = request.getParameter("bindType");
        String cardType = request.getParameter("cardType");
        String cardNo = request.getParameter("cardNo");
        String cvv =  request.getParameter("cvv");
        String expireDate = request.getParameter("expireDate");
        String mobileNo = request.getParameter("mobileNo");

        Users user = CheckLoginInterceptor.getUsers(request.getSession());

        BindNewCardReq req = new BindNewCardReq();

        req.setUserCode(user.getUserCode());

        req.setBindType(Integer.parseInt(bindType));
        req.setCardType(Integer.parseInt(cardType));
        req.setCardNo(cardNo);
        req.setCvv(cvv);
        req.setExpireDate(expireDate);
        req.setMobileNo(mobileNo);

        BindNewCardRsp rsp = accountFacade.bindNewBankCard(req);

        result.setResult(ResultAck.succ);
        result.setData(rsp);


        return result;
    }


    @RequestMapping(value = "/reSendbindMsg.json" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult reSendBindMsg(HttpServletRequest request){
        JsonResult result = new JsonResult();

        String bindNo = request.getParameter("bindNo");

        Users user = CheckLoginInterceptor.getUsers(request.getSession());

        boolean isOk = accountFacade.reSendBindMsg(user.getUserCode(), bindNo);

        if(isOk==true){
            result.setResult(ResultAck.succ);
        }else {
            result.setResult(ResultAck.fail);
        }

        return result;
    }

    @RequestMapping(value = "/confirmVerifyMsg.json" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult confirmVerifyMsg(HttpServletRequest request){

        JsonResult result = new JsonResult();
        String bindNo = request.getParameter("bindNo");
        String verifyCode = request.getParameter("verifyCode");

        ConfirmBindMsgReq req = new ConfirmBindMsgReq();

        Users user = CheckLoginInterceptor.getUsers(request.getSession());

        req.setUserCode(user.getUserCode());
        req.setBindNo(bindNo);
        req.setVerifyCode(verifyCode);

        ConfirmBindMsgRsp rsp = accountFacade.confirmBindMsg(req);

        result.setResult(ResultAck.succ);
        result.setData(rsp);

        return result;
    }


    /**
     *  解绑银行卡
     * @param request
     * @return
     */

    @RequestMapping(value = "/unbind.json" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult  delBindCard(HttpServletRequest request){

        JsonResult result  = new JsonResult();
        String bindNo = request.getParameter("bindNo");
        String payPasswd = request.getParameter("payPasswd");

        if(bindNo==null ||bindNo.trim().isEmpty() || payPasswd==null ||payPasswd.trim().isEmpty()){
            result.setResult(ResultAck.para_error);
            return result;
        }

        AccountFacade accountFacade = dubboClient.getDubboClient("accountFacade");

        Users user = CheckLoginInterceptor.getUsers(request.getSession());

        UnBindBankCardRsp rsp = accountFacade.unBindBankCard(user.getUserCode(), bindNo, payPasswd);

        result.setData(rsp);
        result.setResult(ResultAck.succ);

        return result;
    }


}
