package com.pplive.liveplatform.core;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.util.EncryptUtil;

public class UserManager {
    private static UserManager instance;

    private String mImei;

    private String mUserPrivate;

    private String mUsernamePlain;

    private String mToken;

    private String mNickname;

    private String mIcon;

    private Context mContext;

    private UserManager(Context context) {
        mContext = context;
        mImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        mUserPrivate = SettingsProvider.getInstance(mContext).getUserPrivate();
        if (!TextUtils.isEmpty(mUserPrivate)) {
            mToken = SettingsProvider.getInstance(mContext).getToken();
            mNickname = SettingsProvider.getInstance(mContext).getNickname();
            mIcon = SettingsProvider.getInstance(mContext).getIcon();
            mUsernamePlain = EncryptUtil.decrypt(mUserPrivate, mImei).split(String.valueOf((char) 0x01))[0];
        }
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null)
            instance = new UserManager(context.getApplicationContext());
        return instance;
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(mUserPrivate);
    }

    public void login(String usrPlain, String pwdPlain, String token) {
        if (!isLogin()) {
            StringBuffer sb = new StringBuffer();
            sb.append(usrPlain).append((char) 0x01).append(pwdPlain);
            String userPrivate = EncryptUtil.encrypt(sb.toString(), mImei);
            SettingsProvider.getInstance(mContext).setUserPrivate(userPrivate, token);
            mUserPrivate = userPrivate;
            mUsernamePlain = usrPlain;
            mToken = token;
        }
    }

    public void setUserinfo(User userinfo) {
        if (isLogin()) {
            mNickname = userinfo.getNickname();
            mIcon = userinfo.getIcon();
            SettingsProvider.getInstance(mContext).setUserInfo(userinfo);
        }
    }

    public String getNickname() {
        if (isLogin() && mNickname != null) {
            return mNickname;
        } else {
            return "";
        }
    }

    public String getIcon() {
        if (isLogin() && mIcon != null) {
            return mIcon;
        } else {
            return "";
        }
    }

    public void logout() {
        if (isLogin()) {
            SettingsProvider.getInstance(mContext).clearUser();
            mUserPrivate = null;
            mUsernamePlain = null;
            mToken = null;
            mNickname = null;
            mIcon = null;
        }
    }

    public String getActiveUserPlain() {
        if (isLogin()) {
            return mUsernamePlain;
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
