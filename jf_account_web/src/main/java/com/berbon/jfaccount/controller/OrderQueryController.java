package com.berbon.jfaccount.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by chj on 2016/8/5.
 */
@Controller
@RequestMapping("orderQuery")
public class OrderQueryController {


    @RequestMapping(value = "/chargeOrderPage" , method = RequestMethod.GET)
    public String getPage1(){
        return "tradequery/chargeRecord";
    }

    @RequestMapping(value = "/paymentOrderPage" , method = RequestMethod.GET)
    public String getPage2(){
        return "tradequery/paymentDetails";
    }
    @RequestMapping(value = "/transferOrderPage" , method = RequestMethod.GET)
    public String getPage3(){
        return "tradequery/transferRecord";
    }


}
