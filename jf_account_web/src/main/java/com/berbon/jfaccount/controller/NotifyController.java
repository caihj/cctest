package com.berbon.jfaccount.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by chj on 2016/8/18.
 */

/**
 * 后台通知
 */
@Controller
@RequestMapping("/callback")
public class NotifyController {


    @RequestMapping(value = "/chargeBackNotify",method = {RequestMethod.POST,RequestMethod.GET})
    public String chargeCallBack(){


        return null;
    }


    @RequestMapping(value = "/transferBackNotify" , method = {RequestMethod.POST,RequestMethod.GET})
    public String transferNotify(){


        return null;
    }



}
