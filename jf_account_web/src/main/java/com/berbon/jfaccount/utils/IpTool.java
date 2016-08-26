package com.berbon.jfaccount.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chj on 2016/8/16.
 */
public class IpTool {

    public static String getIp(HttpServletRequest request){
        return request.getRemoteAddr();
    }
}
