package com.alibaba.weex.extend.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * Created by doabit on 2017/10/21.
 */

public class ParseModule {
    public <T> T parseObject(String jsonString, Class<T> clazz) {
        return JSON.parseObject(jsonString, clazz);
    }

    public String toJsonString(Object object) {
        return JSON.toJSONString(object);
    }

    public JSONObject parseObject(String jsonString) {
        return JSON.parseObject(jsonString);
    }
}
