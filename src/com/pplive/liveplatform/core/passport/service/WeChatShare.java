package com.pplive.liveplatform.core.passport.service;
import android.content.Context;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class WeChatShare {

    public static  WeChatShare sInstance = new WeChatShare();
    
    private static final String APP_ID = "";
    
    private IWXAPI api;
    
    public static WeChatShare getInstance(){
        return sInstance;
    }
    
    private void regToWx(Context context){
        api = WXAPIFactory.createWXAPI(context, APP_ID, true);
        api.registerApp(APP_ID);
    }
    
    public void init(Context context){
        regToWx(context);
    }
    
}
