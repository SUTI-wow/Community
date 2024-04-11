package com.suti.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.security.Key;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    /**
     * 生成随机字符串
     * 定义成static是为了便于全局都能调用
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * MD5算法加密
     * 密码+随机字符串--加密--安全性更高
     */
    public static String md5(String key){
        if(StringUtils.isBlank(key))
            return null;
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     *
     * @param code 给浏览器返回的编码
     * @param msg  给浏览器返回的提示信息
     * @param map  给浏览器返回的业务数据
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String,Object>map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        if(map!=null){
            for(String key:map.keySet()){
                jsonObject.put(key,map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

}
