package com.pplive.liveplatform.core.service.passport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.passport.model.LoginResult;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.openapi.legacy.UsersAPI;
import com.sina.weibo.sdk.utils.Utility;

public class WeiboPassport
{
    private static final String CONSUMER_KEY = "3353159992";
    private static final String CONSUMER_SECRET = "";
    
    public static final String APP_KEY      = "3353159992";

    public static final String REDIRECT_URL = "http://open.weibo.com/";

    public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    
    public static final String PARAM_TARGET_URL = "targetUrl";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_SUMMARY = "summary";
    public static final String PARAM_BITMAP = "bitmap";
    
    /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    public SsoHandler mSsoHandler;
    public static  WeiboPassport sInstance = new WeiboPassport();
    
    private IWeiboShareAPI  mWeiboShareAPI = null;
    
    public Activity mActivity;
    
    public Context mContext;
    
    private LoginResult mLoginResult;
    
    ThirdpartyLoginListener mLoginListener;
    
    public static WeiboPassport getInstance() {
        return sInstance;
    }
    
    public void setActivity(Activity activity)
    {
        mActivity = activity;
    }
    
    public void setLoginListener(ThirdpartyLoginListener lst)
    {
        mLoginListener = lst;
    }
    
    public void login()
    {
        mLoginResult = new LoginResult();
        mSsoHandler = new SsoHandler(mActivity, mWeiboAuth);
        mSsoHandler.authorize(new AuthListener());
    }
    
    public void init(Context context)
    {
        mContext = mActivity;
        mWeiboAuth = new WeiboAuth(mContext, APP_KEY, REDIRECT_URL, SCOPE);
        
    }
    
    public void initShare(Context context){
        mWeiboShareAPI  = WeiboShareSDK.createWeiboAPI(context, APP_KEY);
        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
            mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
                @Override
                public void onCancel() {

                }
            });
        }
        
    }
    
    public void shareToWeibo(Bundle url)
    {
        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            mWeiboShareAPI.registerApp();
            sendSingleMessage(url);
        } else {
            Toast.makeText(mContext, R.string.share_weibo_not_install, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSingleMessage(Bundle url) {

        WeiboMessage weiboMessage = new WeiboMessage();

        weiboMessage.mediaObject = getWebpageObj(url);

        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();

        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;

        mWeiboShareAPI.sendRequest(request);
    }
    
    private WebpageObject getWebpageObj(Bundle url) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = url.getString(PARAM_TITLE);
        mediaObject.description = url.getString(PARAM_SUMMARY);
        mediaObject.actionUrl = url.getString(PARAM_TARGET_URL);
        mediaObject.defaultText = url.getString(PARAM_SUMMARY);
        mediaObject.setThumbImage(((BitmapDrawable)mActivity.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap());
        return mediaObject;
    }
    
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                //updateTokenView(false);
                
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
                updateLoginResult(values);
                updateUserInfo(values.getString("uid"));
                //mLoginResult.setThirdPartyID(String.valueOf(values.getLong("uid")));

            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code");
                mLoginListener.LoginFailed();
            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onWeiboException(WeiboException e) {
            mLoginListener.LoginFailed();
        }
    }

    public void updateLoginResult(Bundle values) {
        // TODO Auto-generated method stub
        
    }
    
    public void logout(){
        new LogoutAPI(AccessTokenKeeper.readAccessToken(mActivity)).logout(new RequestListener(){
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    
                }
            }

            @Override
            public void onComplete4binary(ByteArrayOutputStream responseOS) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onIOException(IOException e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onError(WeiboException e) {
                // TODO Auto-generated method stub
            }
            });
    }

    public void updateUserInfo(String uid) {
        // TODO Auto-generated method stub
        UsersAPI userapi = new UsersAPI(mAccessToken);
        userapi.show(Long.valueOf(uid), new RequestListener()
        {

            @Override
            public void onComplete(String response) {
                // TODO Auto-generated method stub
                
                mLoginResult.setThirdPartyToken(mAccessToken.toString());
                try {
                    JSONObject res = new JSONObject(response);
                    mLoginResult.setThirdPartyNickName(res.getString("name"));
                    mLoginResult.setThirdPartyID(res.getString("id"));
                    mLoginResult.setThirdPartyFaceUrl(res.getString("avatar_large"));
                    LoginResult tempresult = PassportService.getInstance().thirdpartyRegister(mLoginResult.getThirdPartyID(), mLoginResult.getThirdPartyFaceUrl(), mLoginResult.getThirdPartyNickName(), "sina");
                    if(tempresult != null) {
                        mLoginResult.setToken(tempresult.getToken());
                        mLoginResult.setUsername(tempresult.getUsername());
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mLoginListener.LoginSuccess(mLoginResult);
            }

            @Override
            public void onComplete4binary(ByteArrayOutputStream responseOS) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onIOException(IOException e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onError(WeiboException e) {
                // TODO Auto-generated method stub
                
            }
            
        });
    }
    
    public LoginResult getLoginResult()
    {
        return mLoginResult;
    }
}
