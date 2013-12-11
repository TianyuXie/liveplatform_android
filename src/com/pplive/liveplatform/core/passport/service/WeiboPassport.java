package com.pplive.liveplatform.core.passport.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.pplive.liveplatform.core.passport.service.TencentPassport.ThirdpartyLoginListener;
import com.pplive.liveplatform.core.service.passport.PassportService;
import com.pplive.liveplatform.core.service.passport.model.LoginResult;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.UsersAPI;


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
    
    /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    public SsoHandler mSsoHandler;
    public static  WeiboPassport sInstance = new WeiboPassport();
    
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
                    PassportService.getInstance().thirdpartyRegister(mLoginResult.getThirdPartyID(), mLoginResult.getThirdPartyFaceUrl(), mLoginResult.getThirdPartyNickName(), "sina");
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
