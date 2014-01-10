package com.pplive.liveplatform.core;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.service.passport.model.LoginResult;
import com.pplive.liveplatform.core.service.passport.thirdparty.TencentPassport;
import com.pplive.liveplatform.core.service.passport.thirdparty.WeiboPassport;
import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.dac.info.UserInfo;
import com.pplive.liveplatform.util.EncryptUtil;
import com.pplive.liveplatform.util.StringUtil;

public class UserManager {
    final static String TAG = "_UserManager";

    private static UserManager instance;

    private String mImei;

    private String mUserPrivate;

    private String mUsernamePlain;

    private String mToken;

    private String mNickname;

    private String mIcon;

    private Context mContext;

    private boolean mThirdPartyLogin;

    private int mThirdPartySource;

    private UserManager(Context context) {
        mContext = context;
        mImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        mUserPrivate = SettingsProvider.getInstance(context).getUserPrivate();
        if (!TextUtils.isEmpty(mUserPrivate)) {
            mToken = SettingsProvider.getInstance(context).getToken();
            mNickname = SettingsProvider.getInstance(context).getNickname();
            mIcon = SettingsProvider.getInstance(context).getIcon();
            mThirdPartySource = SettingsProvider.getInstance(context).getThirdParty();
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

    public boolean isLogin(String username) {
        return !TextUtils.isEmpty(mUserPrivate) && !TextUtils.isEmpty(mUsernamePlain) && mUsernamePlain.equals(username);
    }

    public boolean isPPTVLogin() {
        return isLogin() && !isThirdPartyLogin();
    }

    public boolean isThirdPartyLogin() {
        return isLogin() && mThirdPartySource > 0;
    }

    public boolean isSinaLogin() {
        return isThirdPartyLogin() && mThirdPartySource == LoginResult.FROM_SINA;
    }

    public boolean isTencentLogin() {
        return isThirdPartyLogin() && mThirdPartySource == LoginResult.FROM_TENCENT;
    }

    public boolean isThirdPartyLoginCurrent() {
        return isLogin() && mThirdPartyLogin && mThirdPartySource > 0;
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
            UserInfo.reset(mContext);
        }
    }

    public void logout() {
        if (isLogin()) {
            if (isSinaLogin()) {
                Log.d(TAG, "Sina logout");
                WeiboPassport.getInstance().logout(mContext);
                mThirdPartyLogin = false;
            } else if (isTencentLogin()) {
                Log.d(TAG, "Tencent logout");
                TencentPassport.getInstance().logout(mContext);
                mThirdPartyLogin = false;
            }
            SettingsProvider.getInstance(mContext).clearUser();
            mThirdPartySource = 0;
            mUserPrivate = null;
            mUsernamePlain = null;
            mToken = null;
            mNickname = null;
            mIcon = null;
            UserInfo.reset(mContext);
        }
    }

    public void setUserinfo(User userinfo) {
        if (isLogin() && userinfo != null) {
            mNickname = userinfo.getNickname();
            mIcon = userinfo.getIcon();
            SettingsProvider.getInstance(mContext).setUserInfo(mNickname, mIcon);
        }
    }

    public void setUserinfo(String nickname, String icon) {
        if (isLogin()) {
            mNickname = nickname;
            mIcon = icon;
            SettingsProvider.getInstance(mContext).setUserInfo(mNickname, mIcon);
        }
    }

    public void setThirdParty(int thirdParty) {
        if (isLogin()) {
            mThirdPartyLogin = true;
            mThirdPartySource = thirdParty;
            SettingsProvider.getInstance(mContext).setThirdparty(thirdParty);
        }
    }

    public int getThirdPartySource() {
        return mThirdPartySource;
    }

    public String getNickname() {
        if (isLogin()) {
            return StringUtil.isNullOrEmpty(mNickname) ? getUsernamePlain() : mNickname;
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

    public String getUsernamePlain() {
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
