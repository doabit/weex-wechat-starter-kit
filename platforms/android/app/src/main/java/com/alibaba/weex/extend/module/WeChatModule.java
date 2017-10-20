package com.alibaba.weex.extend.module;

import android.content.Context;

import com.taobao.weex.WXEnvironment;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by doabit on 2017/10/17.
 */

public class WeChatModule extends WXModule {
    private static final String WEEX_CATEGORY = "com.taobao.android.intent.category.WEEX";
    private Context mContext;
    private String appId;

    private JSCallback mCallback;
    private static volatile WeChatModule instance = null;

    public static IWXAPI wxapi = null;


    public WeChatModule(){
        super();
        mContext = WXEnvironment.getApplication().getApplicationContext();
        instance = this;
    }

    public static WeChatModule getInstance() {
        if (instance == null) {
            synchronized (WeChatModule.class) {
                if (instance == null) {
                    instance = new WeChatModule();
                }
            }
        }

        return instance;
    }


    @JSMethod(uiThread = true)
    public void login(String url, JSCallback callback) {
        mCallback = callback;

        SendAuth.Req req = new SendAuth.Req();

        //授权读取用户信息
        req.scope = "snsapi_userinfo";

        //自定义信息
        req.state = "wechat_sdk_auth";

        wxapi.registerApp(appId);

        wxapi.sendReq(req);
    }


    @JSMethod
    public void registerApp(String appid, JSCallback callback) {
        mCallback = callback;

        mContext = WXEnvironment.getApplication().getApplicationContext();

        appId = appid;

        wxapi = WXAPIFactory.createWXAPI(mContext, appid, true);

        mCallback.invoke(wxapi.registerApp(appid));
    }


    public void reciverResult(String result) {
        if (mCallback != null) {
            mCallback.invoke(result);
        }
    }

}
