package com.pplive.liveplatform.core;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.core.dac.info.UserInfo;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.service.passport.model.LoginResult;
import com.pplive.liveplatform.core.service.passport.thirdparty.TencentPassport;
import com.pplive.liveplatform.core.service.passport.thirdparty.WeiboPassport;
import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.util.EncryptUtil;
import com.pplive.liveplatform.util.StringUtil;

public class UserManager {
    final static String TAG = "_UserManager";

    private final static long TEN_DAYS = 10 * 24 * 3600 * 1000;

    private static UserManager instance;

    private String mImei;

    private String mUserPrivate;

    private String mUsernamePlain;

    private String mPasswordPlain;

    private String mToken;

    private String mNickname;

    private String mIcon;

    private Context mAppContext;

    private int mThirdPartySource;

    private boolean mTokenChecked;

    private UserManager(Context context) {
        mAppContext = context;
        mImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        mUserPrivate = SettingsProvider.getInstance(context).getUserPrivate();
        mTokenChecked = false;
        if (!TextUtils.isEmpty(mUserPrivate)) {
            mToken = SettingsProvider.getInstance(context).getToken();
            mNickname = SettingsProvider.getInstance(context).getNickname();
            mIcon = SettingsProvider.getInstance(context).getIcon();
            mThirdPartySource = SettingsProvider.getInstance(context).getThirdParty();
            String[] infos = EncryptUtil.decrypt(mUserPrivate, mImei).split(String.valueOf((char) 0x01));
            mUsernamePlain = infos[0];
            if (infos.length > 1) {
                mPasswordPlain = infos[1];
            } else {
                mPasswordPlain = "";
            }
        }
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null)
            instance = new UserManager(context.getApplicationContext());
        return instance;
    }

    public boolean isLoginSafely() {
        return isLogin() && (mTokenChecked || !shouldUpdateToken());
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(mUserPrivate) && !TextUtils.isEmpty(mToken);
    }

    public boolean isLogin(String username) {
        return isLogin() && !TextUtils.isEmpty(mUsernamePlain) && mUsernamePlain.equals(username);
    }

    public boolean shouldUpdateToken() {
        long currentTime = System.currentTimeMillis();
        long loginTime = SettingsProvider.getInstance(mAppContext).getLoginTime();
        return currentTime < loginTime || currentTime - loginTime > TEN_DAYS;
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

    public void login(String usrPlain, String pwdPlain, String token) {
        StringBuffer sb = new StringBuffer();
        sb.append(usrPlain).append((char) 0x01).append(pwdPlain);
        String userPrivate = EncryptUtil.encrypt(sb.toString(), mImei);
        SettingsProvider.getInstance(mAppContext).setUserPrivate(userPrivate, token);
        mUserPrivate = userPrivate;
        mUsernamePlain = usrPlain;
        mPasswordPlain = pwdPlain;
        mToken = token;
        mTokenChecked = true;
        UserInfo.reset(mAppContext);
    }

    public void logout() {
        if (isLogin()) {
            if (isSinaLogin()) {
                Log.d(TAG, "Sina logout");
                WeiboPassport.getInstance().logout(mAppContext);
            } else if (isTencentLogin()) {
                Log.d(TAG, "Tencent logout");
                TencentPassport.getInstance().logout(mAppContext);
            }
            SettingsProvider.getInstance(mAppContext).clearUser();
            mThirdPartySource = 0;
            mUserPrivate = null;
            mUsernamePlain = null;
            mToken = null;
            mNickname = null;
            mIcon = null;
            UserInfo.reset(mAppContext);
        }
    }

    public void setUserinfo(User userinfo) {
        if (isLogin() && userinfo != null) {
            mNickname = userinfo.getNickname();
            mIcon = userinfo.getIcon();
            SettingsProvider.getInstance(mAppContext).setUserInfo(mNickname, mIcon);
        }
    }

    public void setUserinfo(String nickname, String icon) {
        if (isLogin()) {
            mNickname = nickname;
            mIcon = icon;
            SettingsProvider.getInstance(mAppContext).setUserInfo(mNickname, mIcon);
        }
    }

    public void setThirdParty(int thirdParty) {
        if (isLogin()) {
            mThirdPartySource = thirdParty;
            SettingsProvider.getInstance(mAppContext).setThirdparty(thirdParty);
        }
    }

    public int getThirdPartySource() {
        return mThirdPartySource;
    }

    public String getNickname() {
        if (isLogin()) {
            return StringUtil.isNullOrEmpty(mNickname) ? getUsernamePlain() : mNickname.split("\\(")[0];
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

    public String getPasswordPlain() {
        if (isLogin()) {
            return mPasswordPlain;
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

    public void resetToken() {
        mToken = "";
        if (isSinaLogin()) {
            Log.d(TAG, "Sina logout");
            WeiboPassport.getInstance().logout(mAppContext);
        } else if (isTencentLogin()) {
            Log.d(TAG, "Tencent logout");
            TencentPassport.getInstance().logout(mAppContext);
        }
    }
}
