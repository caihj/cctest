package com.berbon.jfaccount.controller;

import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by chj on 2016/8/2.
 */
@Controller
@RequestMapping("account")
public class AccountController {

    private static Logger logger = LoggerFactory.getLogger(AccountController.class);

    @RequestMapping(value = "/index" , method = RequestMethod.GET)
    public String getPage(){
        logger.info("recv account request");
        return "/account/index";
    }
}
