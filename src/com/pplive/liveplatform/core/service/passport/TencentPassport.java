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
import android.util.Log;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.passport.model.LoginResult;
import com.pplive.liveplatform.util.StringManager;
import com.pplive.liveplatform.util.URLEncoderUtil;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class TencentPassport {
    static final String TAG = "_TencentPassport";

    //    private final static String mAppid = "100570681";

    private final static String mAppid = "100585339";

    private static TencentPassport sInstance;

    private Tencent mTencent;

    private LoginResult mLoginResult;

    private ThirdpartyLoginListener mLoginListener;

    public static synchronized TencentPassport getInstance() {
        if (sInstance == null) {
            sInstance = new TencentPassport();
        }
        return sInstance;
    }

    private TencentPassport() {
    }

    public void setLoginListener(ThirdpartyLoginListener lst) {
        mLoginListener = lst;
    }

    public void login(Activity activity) {
        mLoginResult = new LoginResult();
        if (mTencent != null && !mTencent.isSessionValid()) {
            mTencent.login(activity, "all", loginUiListener);
        } else {
            if (mLoginListener != null) {
                mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_init));
            }
        }
    }

    public void init(Context context) {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(mAppid, context.getApplicationContext());
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null, Constants.HTTP_GET, requestListener, null);
        } else {
            if (mLoginListener != null) {
                mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_init));
            }
        }
    }

    public void logout(Context context) {
        if (mTencent != null && mTencent.isSessionValid()) {
            mTencent.logout(context.getApplicationContext());
        }
    }

    private IUiListener loginUiListener = new IUiListener() {
        @Override
        public void onComplete(JSONObject values) {
            try {
                mLoginResult.setThirdPartyID(values.getString("openid"));
                mLoginResult.setThirdPartyToken(values.getString("access_token"));
            } catch (JSONException e) {
                if (mLoginListener != null) {
                    mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_data));
                }
            }
            updateUserInfo();
        }

        @Override
        public void onError(UiError e) {
            if (mLoginListener != null) {
                mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_ui));
            }
        }

        @Override
        public void onCancel() {
            if (mLoginListener != null) {
                mLoginListener.loginCancel();
            }
        }
    };

    public void doShareToQQ(final Activity activity, final Bundle params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mTencent != null) {
                    mTencent.shareToQQ(activity, params, shareUiListener);
                } else {
                    //TODO
                }
            }
        }).start();
    }

    private IUiListener shareUiListener = new IUiListener() {

        @Override
        public void onError(UiError arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onComplete(JSONObject arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub

        }
    };

    private IRequestListener requestListener = new IRequestListener() {
        @Override
        public void onUnknowException(Exception e, Object state) {
            if (mLoginListener != null) {
                mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_unknown));
            }
        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException e, Object state) {
            if (mLoginListener != null) {
                Log.e(TAG, "IRequestListener: SocketTimeoutException");
                mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_timeout));
            }
        }

        @Override
        public void onNetworkUnavailableException(NetworkUnavailableException e, Object state) {
            if (mLoginListener != null) {
                Log.e(TAG, "IRequestListener: NetworkUnavailableException");
                mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_network));
            }
        }

        @Override
        public void onMalformedURLException(MalformedURLException e, Object state) {
            if (mLoginListener != null) {
                Log.e(TAG, "IRequestListener: MalformedURLException");
                mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_network));
            }
        }

        @Override
        public void onJSONException(JSONException e, Object state) {
            if (mLoginListener != null) {
                mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_data));
            }
        }

        @Override
        public void onIOException(IOException e, Object state) {
            if (mLoginListener != null) {
                Log.e(TAG, "IRequestListener: IOException");
                mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_network));
            }
        }

        @Override
        public void onHttpStatusException(HttpStatusException e, Object state) {
            if (mLoginListener != null) {
                Log.e(TAG, "IRequestListener: HttpStatusException");
                mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_network));
            }
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException e, Object state) {
            if (mLoginListener != null) {
                mLoginListener.loginFailed(StringManager.getRes(R.string.error_qq_timeout));
            }
        }

        @Override
        public void onComplete(final JSONObject response, Object state) {
            try {
                mLoginResult.setThirdPartyNickName(response.getString("nickname"));
                mLoginResult.setThirdPartyFaceUrl(response.getString("figureurl_qq_2"));
                LoginResult temp = PassportService.getInstance().thirdpartyRegister(mLoginResult.getThirdPartyID(), mLoginResult.getThirdPartyFaceUrl(),
                        mLoginResult.getThirdPartyNickName(), "qq");
                if (temp != null) {
                    mLoginResult.setToken(temp.getToken());
                    mLoginResult.setUsername(temp.getUsername());
                    mLoginResult.setThirdPartySource(LoginResult.FROM_TENCENT);
                } else {
                    if (mLoginListener != null) {
                        mLoginListener.loginFailed(StringManager.getRes(R.string.error_pptv_data));
                    }
                }
            } catch (JSONException e) {
                if (mLoginListener != null) {
                    mLoginListener.loginFailed(StringManager.getRes(R.string.error_pptv_format));
                }
            } catch (LiveHttpException e) {
                if (mLoginListener != null) {
                    mLoginListener.loginFailed(URLEncoderUtil.decode(e.getMessage()));
                }
            }
            if (mLoginListener != null) {
                mLoginListener.loginSuccess(mLoginResult);
            }
        }
    };

}
