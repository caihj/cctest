package com.berbon.jfaccount.controller;

import com.berbon.jfaccount.commen.CheckLoginInterceptor;
import com.berbon.jfaccount.commen.JsonResult;
import com.berbon.jfaccount.facade.pojo.TransferCheckReq;
import com.berbon.jfaccount.facade.pojo.TransferCheckRsp;
import com.berbon.user.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chj on 2016/8/9.
 */

@Controller
@RequestMapping("transfer")
public class TransferController {


    @Autowired
    private com.berbon.jfaccount.facade.AccountFacade accountFacade;

    @RequestMapping(value = "/checkUser" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult checkUser(HttpServletRequest request){
        JsonResult result = new JsonResult();

        String userCode = request.getParameter("userCode");
        String amount = request.getParameter("amount");
        String note = request.getParameter("note");
        String phone = request.getParameter("phone");
        String realName = request.getParameter("realName");

        TransferCheckReq req = new TransferCheckReq();

        Users user = CheckLoginInterceptor.getUsers(request.getSession());
        if(user==null){
            return null;
        }

        req.setFromUserCode(user.getUserCode());
        req.setToUserCode(userCode);
        req.setAmount(Long.parseLong(amount));
        req.setPhone(phone);
        req.setRealName(realName);

        TransferCheckRsp rsp = accountFacade.checkCanTransferTo(req);

        result.setData(rsp);

        return result;
    }



    @RequestMapping(value = "/createOrder" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult createTransferOrder(HttpServletRequest request){
        JsonResult result = new JsonResult();


        return result;
    }


}
