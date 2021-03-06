package com.berbon.jfaccount.Service;

import com.alibaba.fastjson.JSONObject;
import com.berbon.jfaccount.util.Pair;
import com.berbon.util.MD5.MD5Util;
import com.berbon.util.String.StringUtil;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;

import com.sztx.se.rpc.dubbo.source.DynamicDubboClient;
import com.sztx.usercenter.rpc.api.domain.out.UserBaseInfoVO;
import com.sztx.usercenter.rpc.api.service.QueryPayUserInfoService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by chj on 2016/8/8.
 */

@Service
public class SignService {

    private static Logger logger = LoggerFactory.getLogger(SignService.class);

    @Autowired
    private DynamicDubboClient dubboClient;


    public SignService(){
        System.out.println("SignService construct call");
    }

    /**
     * 计算请求对象的签名
     * @return
     */
    public static String CalSign(Object obj,String key){

        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(obj);

        TreeMap<String, String> ordDataMap = new TreeMap<String, String>();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            if (StringUtil.isNull(entry.getValue()) || "sign".equals(entry.getKey())) {
                continue;
            }
            ordDataMap.put(entry.getKey(), entry.getValue() + "");
        }

        String resultSrc = "";
        Iterator iter = ordDataMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry ent = (Map.Entry) iter.next();
            resultSrc += ent.getKey() + "=" + ent.getValue() + "&";
        }
        logger.info("计算签名原串:" + resultSrc);
        // logger.info(key);
        return MD5Util.encrypt(resultSrc + "key=" + key);

    }

    public Pair<Boolean,String> checkUserPayPasswd(String userCode,String payPwd){

        logger.info("检验支付密码"+userCode);

        QueryPayUserInfoService queryPayUserInfoService = dubboClient.getDubboClient("queryPayUserInfoService");
        UserBaseInfoVO vo = queryPayUserInfoService.getUser(userCode);
        String salt = vo.getSalt();
        String encPwd=payPwd;// DigestUtils.md5Hex(payPwd+salt);
        try {
            queryPayUserInfoService.checkPayPwd(userCode, encPwd, null, 1);
        }catch (Exception e){
            logger.error("密码错误"+e);
            return new Pair<>(false,e.getMessage());
        }

        return  new Pair<>(true,"成功");
    }
}
