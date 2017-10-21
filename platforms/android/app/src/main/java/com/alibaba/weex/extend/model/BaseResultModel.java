package com.alibaba.weex.extend.model;

import java.io.Serializable;

/**
 * Created by doabit on 2017/10/21.
 */

public class BaseResultModel implements Serializable {
    public int resCode;
    public String msg;

    public BaseResultModel(int resCode, String msg) {
        this.resCode = resCode;
        this.msg = msg;
    }

    public BaseResultModel() {
    }
}
