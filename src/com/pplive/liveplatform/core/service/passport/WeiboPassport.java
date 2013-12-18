package com.pplive.liveplatform.core.service.passport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
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

public class WeiboPassport {
    private static final String CONSUMER_KEY = "3353159992";
    private static final String CONSUMER_SECRET = "";

    public static final String APP_KEY = "3353159992";

    public static final String REDIRECT_URL = "http://open.weibo.com/";

    public static final String SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,"
            + "friendships_groups_write,statuses_to_me_read, follow_app_official_microblog,invitation_write";

    public static final String PARAM_TARGET_URL = "targetUrl";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_SUMMARY = "summary";
    public static final String PARAM_BITMAP = "bitmap";

    private Oauth2AccessToken mAccessToken;
    public SsoHandler mSsoHandler;
    private IWeiboShareAPI mWeiboShareAPI;

    private Activity mActivity;
    private LoginResult mLoginResult;
    private ThirdpartyLoginListener mLoginListener;

    private static WeiboPassport sInstance;

    public static synchronized WeiboPassport getInstance() {
        if (sInstance == null) {
            sInstance = new WeiboPassport();
        }
        return sInstance;
    }

    private WeiboPassport() {
    }

    public void setLoginListener(ThirdpartyLoginListener lst) {
        mLoginListener = lst;
    }

    public void login(Activity activity) {
        mActivity = activity;
        WeiboAuth weiboAuth = new WeiboAuth(activity, APP_KEY, REDIRECT_URL, SCOPE);
        mLoginResult = new LoginResult();
        mSsoHandler = new SsoHandler(activity, weiboAuth);
        mSsoHandler.authorize(new AuthListener());
    }

    public void initShare(Activity activity) {
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, APP_KEY);
        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
            mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
                @Override
                public void onCancel() {
                    //TODO
                }
            });
        }
    }

    public void shareToWeibo(Context context, Bundle data) {
        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            mWeiboShareAPI.registerApp();
            sendSingleMessage(data);
        } else {
            Toast.makeText(context, R.string.share_weibo_not_install, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSingleMessage(Bundle data) {
        WeiboMessage weiboMessage = new WeiboMessage();
        weiboMessage.mediaObject = getWebpageObj(data);
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
        mediaObject.setThumbImage((Bitmap) url.getParcelable(PARAM_BITMAP));
        return mediaObject;
    }

    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(mActivity, mAccessToken);
                updateLoginResult(values);
                updateUserInfo(values.getString("uid"));
            } else {
                mLoginListener.LoginFailed("WeiboAuthListener error:" + values.getString("code"));
            }
        }

        @Override
        public void onCancel() {
            if (mLoginListener != null) {
                mLoginListener.LoginFailed("WeiboAuthListener: onCancel");
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (mLoginListener != null) {
                mLoginListener.LoginFailed("WeiboAuthListener: WeiboException");
            }
        }
    }

    public void updateLoginResult(Bundle values) {
        // TODO Auto-generated method stub
    }

    public void logout(Context context) {
        new LogoutAPI(AccessTokenKeeper.readAccessToken(context)).logout(new RequestListener() {
            @Override
            public void onComplete(String response) {
                // TODO Auto-generated method stub
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

    private void updateUserInfo(String uid) {
        UsersAPI userapi = new UsersAPI(mAccessToken);
        userapi.show(Long.valueOf(uid), new RequestListener() {

            @Override
            public void onComplete(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    mLoginResult.setThirdPartyNickName(res.getString("name"));
                    mLoginResult.setThirdPartyID(res.getString("id"));
                    mLoginResult.setThirdPartyFaceUrl(res.getString("avatar_large"));
                    mLoginResult.setThirdPartyToken(mAccessToken.toString());
                    LoginResult tempresult = PassportService.getInstance().thirdpartyRegister(mLoginResult.getThirdPartyID(),
                            mLoginResult.getThirdPartyFaceUrl(), mLoginResult.getThirdPartyNickName(), "sina");
                    if (tempresult != null) {
                        mLoginResult.setToken(tempresult.getToken());
                        mLoginResult.setUsername(tempresult.getUsername());
                        mLoginResult.setThirdPartySource(LoginResult.FROM_SINA);
                    } else {
                        if (mLoginListener != null) {
                            mLoginListener.LoginFailed("RequestListener: PassportService failed");
                        }
                    }
                } catch (JSONException e) {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("RequestListener: JSONException");
                    }
                } catch (Exception e) {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("RequestListener: Exception");
                    }
                }
                if (mLoginListener != null) {
                    mLoginListener.LoginSuccess(mLoginResult);
                }
            }

            @Override
            public void onComplete4binary(ByteArrayOutputStream responseOS) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onIOException(IOException e) {
                if (mLoginListener != null) {
                    mLoginListener.LoginFailed("RequestListener: IOException");
                }
            }

            @Override
            public void onError(WeiboException e) {
                if (mLoginListener != null) {
                    mLoginListener.LoginFailed("RequestListener: WeiboException");
                }
            }
        });
    }
}
