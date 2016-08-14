package com.berbon.jfaccount.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chj on 2016/8/14.
 */

@Controller
@RequestMapping("bindcard")
public class MyCardController {


    @RequestMapping(value = "/bind" , method ={ RequestMethod.POST, RequestMethod.GET})
    public String getPage1(){
        return "/myCard/addBankCard";
    }

}
