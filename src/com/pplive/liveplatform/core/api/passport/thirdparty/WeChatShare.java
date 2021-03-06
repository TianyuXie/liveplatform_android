package com.pplive.liveplatform.core.api.passport.thirdparty;

import android.content.Context;
import android.graphics.Bitmap;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

public class WeChatShare {

    public static WeChatShare sInstance = new WeChatShare();

    private static final String APP_ID = "wxc45978b92c09c3c6";
//    private static final String APP_KEY = "c0a5d059ec2d2891c343855eb106ac14";

    public static final int SHARE_SNS = 1;
    public static final int SHARE_WECHAT = 0;

    private IWXAPI api;

    public static WeChatShare getInstance() {
        return sInstance;
    }

    private void regToWx(Context context) {
        api = WXAPIFactory.createWXAPI(context, APP_ID, true);
        api.registerApp(APP_ID);
    }

    public void init(Context context) {
        regToWx(context);
    }

    public boolean sendToWeChat(Context context, String description, String url, String title, Bitmap thumb, int sns) {
        if (api == null) {
            init(context);
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        msg.thumbData = Util.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = sns > 0 ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        return api.sendReq(req);
    }

}
