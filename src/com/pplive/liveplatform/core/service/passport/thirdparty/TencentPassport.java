package com.pplive.liveplatform.core.service.passport.thirdparty;

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
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.UserService;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.service.passport.PassportService;
import com.pplive.liveplatform.core.service.passport.model.LoginResult;
import com.pplive.liveplatform.util.StringManager;
import com.pplive.liveplatform.util.URLUtil;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class TencentPassport {
    static final String TAG = "_TencentPassport";

    private final static String mAppid = "100585339";

    private static TencentPassport sInstance;

    private Tencent mTencent;

    private LoginResult mLoginResult;

    private ThirdpartyLoginListener mLoginListener;

    private ThirdpartyShareListener mShareListener;

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

    public void setShareListener(ThirdpartyShareListener lst) {
        mShareListener = lst;
    }

    public void login(Activity activity) {
        mLoginResult = new LoginResult();
        if (mTencent != null) {
            mTencent.login(activity, "all", loginUiListener);
        } else {
            if (mLoginListener != null) {
                mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_init));
            }
        }
    }

    public void init(Context context) {
        if (mTencent == null) {
            Log.d(TAG, "Tencent.createInstance");
            mTencent = Tencent.createInstance(mAppid, context.getApplicationContext());
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null, Constants.HTTP_GET, requestListener, null);
        } else {
            if (mLoginListener != null) {
                mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_init));
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
                    mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_data));
                }
            }
            updateUserInfo();
        }

        @Override
        public void onError(UiError e) {
            if (mLoginListener != null) {
                mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_ui));
            }
        }

        @Override
        public void onCancel() {
            if (mLoginListener != null) {
                mLoginListener.onLoginCanceled();
            }
        }
    };

    public void doShareToQQ(final Activity activity, final Bundle params) {
        if (mTencent != null) {
            mTencent.shareToQQ(activity, params, shareUiListener);
        } else {
            if (mShareListener != null) {
                mShareListener.shareFailed(StringManager.getRes(R.string.error_qq_init));
            }
        }
    }

    private IUiListener shareUiListener = new IUiListener() {

        @Override
        public void onError(UiError arg0) {
            if (mShareListener != null) {
                mShareListener.shareFailed(StringManager.getRes(R.string.error_qq_ui));
            }
        }

        @Override
        public void onComplete(JSONObject arg0) {
            if (mShareListener != null) {
                mShareListener.shareSuccess();
            }
        }

        @Override
        public void onCancel() {
            if (mShareListener != null) {
                mShareListener.shareCanceled();
            }
        }
    };

    private IRequestListener requestListener = new IRequestListener() {
        @Override
        public void onUnknowException(Exception e, Object state) {
            if (mLoginListener != null) {
                mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_unknown));
            }
        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException e, Object state) {
            if (mLoginListener != null) {
                Log.e(TAG, "IRequestListener: SocketTimeoutException");
                mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_timeout));
            }
        }

        @Override
        public void onNetworkUnavailableException(NetworkUnavailableException e, Object state) {
            if (mLoginListener != null) {
                Log.e(TAG, "IRequestListener: NetworkUnavailableException");
                mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_network));
            }
        }

        @Override
        public void onMalformedURLException(MalformedURLException e, Object state) {
            if (mLoginListener != null) {
                Log.e(TAG, "IRequestListener: MalformedURLException");
                mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_network));
            }
        }

        @Override
        public void onJSONException(JSONException e, Object state) {
            if (mLoginListener != null) {
                mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_data));
            }
        }

        @Override
        public void onIOException(IOException e, Object state) {
            if (mLoginListener != null) {
                Log.e(TAG, "IRequestListener: IOException");
                mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_network));
            }
        }

        @Override
        public void onHttpStatusException(HttpStatusException e, Object state) {
            if (mLoginListener != null) {
                Log.e(TAG, "IRequestListener: HttpStatusException");
                mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_network));
            }
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException e, Object state) {
            if (mLoginListener != null) {
                mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_qq_timeout));
            }
        }

        @Override
        public void onComplete(final JSONObject response, Object state) {
            LoginResult temp = null;
            try {
                mLoginResult.setThirdPartyNickName(response.getString("nickname"));
                mLoginResult.setThirdPartyFaceUrl(response.getString("figureurl_qq_2"));
                temp = PassportService.getInstance().thirdpartyRegister(mLoginResult.getThirdPartyID(), mLoginResult.getThirdPartyFaceUrl(),
                        mLoginResult.getThirdPartyNickName(), "qq");
            } catch (JSONException e) {
                if (mLoginListener != null) {
                    mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_pptv_format));
                }
            } catch (LiveHttpException e) {
                if (mLoginListener != null) {
                    mLoginListener.onLoginFailed(URLUtil.decode(e.getMessage()));
                }
            }
            if (temp != null) {
                String token = temp.getToken();
                String username = temp.getUsername();
                mLoginResult.setToken(token);
                mLoginResult.setUsername(username);
                mLoginResult.setThirdPartySource(LoginResult.FROM_TENCENT);
                User userinfo = null;
                try {
                    userinfo = UserService.getInstance().getUserInfo(token, username);
                } catch (LiveHttpException e) {
                }
                if (userinfo != null) {
                    mLoginResult.setFaceUrl(userinfo.getIcon());
                    mLoginResult.setNickName(userinfo.getNickname());
                }
            } else {
                if (mLoginListener != null) {
                    mLoginListener.onLoginFailed(StringManager.getRes(R.string.error_pptv_data));
                }
            }
            if (mLoginListener != null) {
                mLoginListener.onLoginSuccess(mLoginResult);
            }
        }
    };

}
