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

public class TencentPassport {

    private final static String mAppid = "100570681";

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
            IUiListener listener = new IUiListener() {
                @Override
                public void onComplete(JSONObject values) {
                    try {
                        mLoginResult.setThirdPartyID(values.getString("openid"));
                        mLoginResult.setThirdPartyToken(values.getString("access_token"));
                    } catch (JSONException e) {
                        if (mLoginListener != null) {
                            mLoginListener.LoginFailed("IUiListener: JSONException");
                        }
                    }
                    updateUserInfo();
                }

                @Override
                public void onError(UiError e) {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("IUiListener: UiError");
                    }
                }

                @Override
                public void onCancel() {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("IUiListener: onCancel");
                    }
                }
            };
            mTencent.login(activity, "all", listener);
        } else {
            if (mLoginListener != null) {
                mLoginListener.LoginFailed("mTencent == null or invalid");
            }
        }
    }

    public void init(Context context) {
        mTencent = Tencent.createInstance(mAppid, context.getApplicationContext());
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IRequestListener requestListener = new IRequestListener() {

                @Override
                public void onUnknowException(Exception e, Object state) {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("IRequestListener: UnknowException");
                    }
                }

                @Override
                public void onSocketTimeoutException(SocketTimeoutException e, Object state) {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("IRequestListener: SocketTimeoutException");
                    }
                }

                @Override
                public void onNetworkUnavailableException(NetworkUnavailableException e, Object state) {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("IRequestListener: NetworkUnavailableException");
                    }
                }

                @Override
                public void onMalformedURLException(MalformedURLException e, Object state) {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("IRequestListener: MalformedURLException");
                    }
                }

                @Override
                public void onJSONException(JSONException e, Object state) {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("IRequestListener: JSONException");
                    }
                }

                @Override
                public void onIOException(IOException e, Object state) {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("IRequestListener: IOException");
                    }
                }

                @Override
                public void onHttpStatusException(HttpStatusException e, Object state) {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("IRequestListener: HttpStatusException");
                    }
                }

                @Override
                public void onConnectTimeoutException(ConnectTimeoutException e, Object state) {
                    if (mLoginListener != null) {
                        mLoginListener.LoginFailed("IRequestListener: ConnectTimeoutException");
                    }
                }

                @Override
                public void onComplete(final JSONObject response, Object state) {
                    try {
                        mLoginResult.setThirdPartyNickName(response.getString("nickname"));
                        mLoginResult.setThirdPartyFaceUrl(response.getString("figureurl_qq_2"));
                        LoginResult temp = PassportService.getInstance().thirdpartyRegister(mLoginResult.getThirdPartyID(),
                                mLoginResult.getThirdPartyFaceUrl(), mLoginResult.getThirdPartyNickName(), "qq");
                        if (temp != null) {
                            mLoginResult.setToken(temp.getToken());
                            mLoginResult.setUsername(temp.getUsername());
                            mLoginResult.setThirdPartySource(LoginResult.FROM_TENCENT);
                        } else {
                            if (mLoginListener != null) {
                                mLoginListener.LoginFailed("IRequestListener: PassportService failed");
                            }
                        }
                    } catch (JSONException e) {
                        if (mLoginListener != null) {
                            mLoginListener.LoginFailed("IRequestListener: JSONException");
                        }
                    } catch (Exception e) {
                        if (mLoginListener != null) {
                            mLoginListener.LoginFailed("IRequestListener: Exception");
                        }
                    }
                    if (mLoginListener != null) {
                        mLoginListener.LoginSuccess(mLoginResult);
                    }
                }
            };
            mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null, Constants.HTTP_GET, requestListener, null);
        } else {
            if (mLoginListener != null) {
                mLoginListener.LoginFailed("mTencent == null or invalid");
            }
        }
    }

    public LoginResult getLoginResult() {
        return mLoginResult;

    }

    public void logout(Context context) {
        if (mTencent != null && mTencent.isSessionValid()) {
            mTencent.logout(context.getApplicationContext());
        }
    }

    public void doShareToQQ(final Activity activity, final Bundle params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mTencent != null) {
                    mTencent.shareToQQ(activity, params, new IUiListener() {
                        @Override
                        public void onComplete(JSONObject response) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onCancel() {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onError(UiError arg0) {
                            // TODO Auto-generated method stub
                        }
                    });
                } else {
                    //TODO
                }
            }
        }).start();
    }

}
