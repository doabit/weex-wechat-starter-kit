package com.alibaba.weex.extend.model;

/**
 * Created by doabit on 2017/10/21.
 */

public class WeChatAuthResultModel extends BaseResultModel {

    public String code;

    public WeChatAuthResultModel(int resCode, String msg, String code) {
        this.resCode = resCode;
        this.msg = msg;
        this.code = code;
    }

    public WeChatAuthResultModel() {

    }

}
