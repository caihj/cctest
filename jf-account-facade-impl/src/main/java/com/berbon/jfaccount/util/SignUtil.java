package com.berbon.jfaccount.util;

import com.alibaba.fastjson.JSONObject;
import com.berbon.util.MD5.MD5Util;
import com.berbon.util.String.StringUtil;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by chj on 2016/8/8.
 */
public class SignUtil {

    private static Logger logger = LoggerFactory.getLogger(SignUtil.class);
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
}
