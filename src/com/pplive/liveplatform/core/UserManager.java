package com.pplive.liveplatform.core;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.util.EncryptUtil;

public class UserManager {
    private static UserManager instance;

    private String mUserinfo;

    private String mActiveUserPlain;

    private String mToken;

    private String mImei;

    private Context mContext;

    private UserManager(Context context) {
        mContext = context;
        mImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        mUserinfo = SettingsProvider.getInstance(mContext).getUserinfo();
        if (!TextUtils.isEmpty(mUserinfo)) {
            mToken = SettingsProvider.getInstance(mContext).getToken();
            mActiveUserPlain = EncryptUtil.decrypt(mUserinfo, mImei).split(String.valueOf((char) 0x01))[0];
        }
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null)
            instance = new UserManager(context.getApplicationContext());
        return instance;
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(mUserinfo);
    }

    public void login(String usrPlain, String pwdPlain, String token) {
        if (!isLogin()) {
            StringBuffer sb = new StringBuffer();
            sb.append(usrPlain).append((char) 0x01).append(pwdPlain);
            String userInfo = EncryptUtil.encrypt(sb.toString(), mImei);
            SettingsProvider.getInstance(mContext).setUser(userInfo, token);
            mUserinfo = userInfo;
            mActiveUserPlain = usrPlain;
            mToken = token;
        }
    }

    public void logout() {
        if (isLogin()) {
            SettingsProvider.getInstance(mContext).clearUser();
            mUserinfo = null;
            mActiveUserPlain = null;
            mToken = null;
        }
    }

    public String getActiveUserPlain() {
        if (isLogin()) {
            return mActiveUserPlain;
        } else {
            return "";
        }
    }

    public String getToken() {
        if (isLogin()) {
            return mToken;
        } else {
            return "";
        }
    }

}
