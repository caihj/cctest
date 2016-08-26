package com.berbon.jfaccount.controller;

import com.berbon.jfaccount.commen.ConstStr;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by chj on 2016/8/23.
 */
@Controller
public class ErrorController {

    @RequestMapping(value = "/error.htm")
    public String error(){
        return ConstStr.error_page;
    }
}
