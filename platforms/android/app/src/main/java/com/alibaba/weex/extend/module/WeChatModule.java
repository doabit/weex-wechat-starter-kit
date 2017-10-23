package com.alibaba.weex.extend.module;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.weex.R;
import com.alibaba.weex.extend.model.BaseResultModel;
import com.alibaba.weex.extend.model.WeChatPayModel;
import com.alibaba.weex.extend.model.WeChatShareModel;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.UUID;


/**
 * Created by doabit on 2017/10/17.
 */

public class WeChatModule extends WXModule {
    private static final String WEEX_CATEGORY = "com.taobao.android.intent.category.WEEX";
    private final static int THUMB_SIZE = 120;

    private Context mContext;
    private JSCallback mCallback;

    private static String appId = null;
    public static IWXAPI wxapi = null;
    private static volatile WeChatModule instance = null;

    public WeChatModule() {
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

    public String getAppId() {
        return appId;
    }


    @JSMethod
    public void registerApp(String appid, JSCallback callback) {
        mCallback = callback;

        mContext = WXEnvironment.getApplication().getApplicationContext();

        appId = appid;

        wxapi = WXAPIFactory.createWXAPI(mContext, appid, true);

        mCallback.invoke(wxapi.registerApp(appid));
    }


    @JSMethod(uiThread = true)
    public void login(String params, JSCallback callback) {
        mCallback = callback;

        SendAuth.Req req = new SendAuth.Req();

        //授权读取用户信息
        req.scope = "snsapi_userinfo";

        //自定义信息
        req.state = "wechat_sdk_auth";

        wxapi.sendReq(req);
    }

    @JSMethod(uiThread = true)
    public void pay(String params, JSCallback callback) {
        mCallback = callback;
        ParseModule parseModule = new ParseModule();
        WeChatPayModel weChatPayModal = parseModule.parseObject(params, WeChatPayModel.class);

        if (!wxapi.isWXAppInstalled()) {
            BaseResultModel result = new BaseResultModel();
            result.resCode = 9;
            result.msg = "请先安装微信客户端";
            if (mCallback != null) {
                mCallback.invokeAndKeepAlive(result);
            }
            return;
        }

        PayReq request = new PayReq();
        request.appId = weChatPayModal.getAppid();
        request.partnerId = weChatPayModal.getPartnerid();
        request.nonceStr = weChatPayModal.getNoncestr();
        request.packageValue = weChatPayModal.getPackageValue();
        request.prepayId = weChatPayModal.getPrepayid();
        request.timeStamp = weChatPayModal.getTimestamp();
        request.sign = weChatPayModal.getSign();
//        wxapi.registerApp(appId);
        wxapi.sendReq(request);

    }

    @JSMethod(uiThread = false)
    public void shareToTimeLine(String params, JSCallback callback) {
        mCallback = callback;
        share(params, SendMessageToWX.Req.WXSceneTimeline);
    }

    @JSMethod(uiThread = false)
    public void shareToSession(String params, JSCallback callback) {
        mCallback = callback;
        share(params, SendMessageToWX.Req.WXSceneSession);
    }


    public void reciverResult(BaseResultModel result) {
        if (mCallback != null) {
            mCallback.invoke(result);
        }
    }

    private boolean share(String params, int shareType) {
        ParseModule parseModule = new ParseModule();
        WeChatShareModel weChatShareModal = parseModule.parseObject(params, WeChatShareModel.class);

        WXMediaMessage msg = getMessage(weChatShareModal);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = UUID.randomUUID().toString();
        req.message = msg;
        req.scene = shareType;

        return wxapi.sendReq(req);
    }


    private WXMediaMessage getMessage(WeChatShareModel weChatShareModel) {
        WXMediaMessage msg = null;
        switch (weChatShareModel.getType()) {
            case "text":
                msg = wxTextObject(weChatShareModel);
                break;
            case "image":
                msg = wxImageObject(weChatShareModel);
                break;
            case "video":
                msg = wxVideoObject(weChatShareModel);
                break;
            case "webpage":
                msg = wxWebpageObject(weChatShareModel);
                break;
            default:
                break;
        }

        return msg;
    }

    /*
     * 分享文字
    */
    private WXMediaMessage wxTextObject(WeChatShareModel weChatShareModel) {
        String text = weChatShareModel.getContent();
        //初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        //用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;
        return msg;
    }

    /*
     * 分享图片
     */
    private WXMediaMessage wxImageObject(WeChatShareModel weChatShareModel) {
        Bitmap bitmap, thumb;

        if (!TextUtils.isEmpty(weChatShareModel.getImage())) {
            byte[] bytes = Util.getHtmlByteArray(weChatShareModel.getImage());
            bitmap = Util.Bytes2Bimap(bytes);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wechat);
        }

        thumb = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);

        WXImageObject wxImageObject = new WXImageObject(bitmap);

        WXMediaMessage msg = new WXMediaMessage(wxImageObject);

        if (thumb == null) {
            Toast.makeText(mContext, "图片为空", Toast.LENGTH_SHORT).show();
        } else {
            msg.thumbData = Util.bmpToByteArray(thumb, true);
            thumb.recycle();
        }

        if (bitmap != null) {
            bitmap.recycle();
        }

        return msg;
    }

    /*
     * 分享链接
    */
    private WXMediaMessage wxWebpageObject(WeChatShareModel weChatShareModel) {
        Bitmap bitmap, thumb;

        if (!TextUtils.isEmpty(weChatShareModel.getImage())) {
            byte[] bytes = Util.getHtmlByteArray(weChatShareModel.getImage());
            bitmap = Util.Bytes2Bimap(bytes);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wechat);
        }

        thumb = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);

        WXWebpageObject webPageObject = new WXWebpageObject();
        webPageObject.webpageUrl = weChatShareModel.getUrl();

        WXMediaMessage msg = new WXMediaMessage(webPageObject);

        msg.title = weChatShareModel.getTitle();
        msg.description = weChatShareModel.getContent();


        if (thumb == null) {
            Toast.makeText(mContext, "图片为空", Toast.LENGTH_SHORT).show();
        } else {
            msg.thumbData = Util.bmpToByteArray(thumb, true);

            thumb.recycle();
        }

        if (bitmap != null) {
            bitmap.recycle();
        }

        return msg;
    }

    /*
    * 分享视频
    */
    private WXMediaMessage wxVideoObject(WeChatShareModel weChatShareModel) {
        Bitmap bitmap, thumb;

        if (!TextUtils.isEmpty(weChatShareModel.getImage())) {
            byte[] bytes = Util.getHtmlByteArray(weChatShareModel.getImage());
            bitmap = Util.Bytes2Bimap(bytes);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wechat);
        }

        thumb = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);

        WXVideoObject video = new WXVideoObject();
        video.videoUrl = weChatShareModel.getUrl();

        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = weChatShareModel.getTitle();
        msg.description = weChatShareModel.getContent();

        if (thumb == null) {
            Toast.makeText(mContext, "图片为空", Toast.LENGTH_SHORT).show();
        } else {
            msg.thumbData = Util.bmpToByteArray(thumb, true);

            thumb.recycle();
        }

        if (bitmap != null) {
            bitmap.recycle();
        }
        return msg;
    }


}
