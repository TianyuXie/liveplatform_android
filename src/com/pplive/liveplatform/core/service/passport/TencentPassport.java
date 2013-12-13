package com.pplive.liveplatform.core.service.passport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.pplive.liveplatform.core.service.passport.model.LoginResult;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;

import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class TencentPassport
{
    
    public interface ThirdpartyLoginListener
    {
        void LoginSuccess(LoginResult res);
        void LoginFailed();
    };
    
    private static final TencentPassport sInstance = new TencentPassport();
    
    public  Tencent mTencent;
    public static String mAppid = "100570681";
    private Context txContext;
    private Activity mActivity;
    private LoginResult mLoginResult;
    ThirdpartyLoginListener mLoginListener;

    public static TencentPassport getInstance() {
        return sInstance;
    }
    
    
    public void login()
    {
        
        mLoginResult = new LoginResult();
        if (!mTencent.isSessionValid()) {
            IUiListener listener = new BaseUiListener() {
                @Override
                protected void doComplete(JSONObject values) {
                    try {
                        mLoginResult.setThirdPartyID(values.getString("openid"));
                        mLoginResult.setThirdPartyToken(values.getString("access_token"));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    updateUserInfo();
                    //updateLoginButton();
                }

                @Override
                public void onError(UiError e)
                {
                    // TODO Auto-generated method stub
                    super.onError(e);
                }
                
                
            };
            mTencent.login(mActivity, "all", listener);
            
        }
    }
    
    public void setActivity(Activity activity)
    {
        mActivity = activity;
    }
    
    public void setLoginListener(ThirdpartyLoginListener lst)
    {
        mLoginListener = lst;
    }
    
    public void init(Context context)
    {
        txContext = context.getApplicationContext();
        mTencent = Tencent.createInstance(mAppid, txContext);
    }
    
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(JSONObject response) {
            doComplete(response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
             mLoginListener.LoginFailed();
        }

        @Override
        public void onCancel() {

        }
    }
    
    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IRequestListener requestListener = new IRequestListener() {

                @Override
                public void onUnknowException(Exception e, Object state) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onSocketTimeoutException(SocketTimeoutException e,
                        Object state) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onNetworkUnavailableException(
                        NetworkUnavailableException e, Object state) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onMalformedURLException(MalformedURLException e,
                        Object state) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onJSONException(JSONException e, Object state) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onIOException(IOException e, Object state) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onHttpStatusException(HttpStatusException e,
                        Object state) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onConnectTimeoutException(
                        ConnectTimeoutException e, Object state) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onComplete(final JSONObject response, Object state) {
                    // TODO Auto-generated method stub
                    try {
                        mLoginResult.setThirdPartyNickName(response.getString("nickname"));
                        mLoginResult.setThirdPartyFaceUrl(response.getString("figureurl_qq_2"));
                        LoginResult temp = PassportService.getInstance().thirdpartyRegister(mLoginResult.getThirdPartyID(), mLoginResult.getThirdPartyFaceUrl(), mLoginResult.getThirdPartyNickName(), "qq");
                        if(temp != null) {
                            mLoginResult.setToken(temp.getToken());
                            mLoginResult.setUsername(temp.getUsername());
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mLoginListener.LoginSuccess(mLoginResult);
                }
            };
              mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
                        Constants.HTTP_GET, requestListener, null);
        } else {

        }
    }
    
    public LoginResult getLoginResult()
    {
        return mLoginResult;
        
    }
    
    private void doShareToQQ(final Bundle params) {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mTencent.shareToQQ(mActivity, params, new IUiListener() {

                    @Override
                    public void onComplete(JSONObject response) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void onError(UiError e) {
                        
                    }

                    @Override
                    public void onCancel() {

                    }

                });
            }
        }).start();
    }
    
}
