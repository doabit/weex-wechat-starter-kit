package com.alibaba.weex.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.weex.extend.module.WeChatModule;
import com.taobao.weex.utils.WXLogUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

//import android.content.Intent;
/**
 * Created by doabit on 2017/10/20.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册API
        WeChatModule.wxapi.handleIntent(getIntent(), this);
    }



    @Override
    public void onReq(BaseReq baseReq) {

    }


    @Override
    public void onResp(BaseResp resp) {

        WXLogUtils.e(TAG, "error_code:---->" + resp.errCode);

        if (resp instanceof SendAuth.Resp) {
            SendAuth.Resp newResp = (SendAuth.Resp) resp;

            WXLogUtils.e(newResp.code, newResp.url);

            //获取微信传回的code
            String code = newResp.code;
            WXLogUtils.e(TAG, code);

            WeChatModule.getInstance().reciverResult(code);


            finish();
        }
        finish();
    }

}
