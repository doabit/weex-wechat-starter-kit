package com.alibaba.weex.extend.model;

/**
 * Created by doabit on 2017/10/23.
 */

public class WeChatShareResultModel extends BaseResultModel {
    public String openid;

    public WeChatShareResultModel(int resCode, String msg, String openid) {
        this.resCode = resCode;
        this.msg = msg;
        this.openid = openid;
    }

    public WeChatShareResultModel() {
    }
}
