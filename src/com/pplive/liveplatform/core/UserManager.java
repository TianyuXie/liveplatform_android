package com.pplive.liveplatform.core;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.pplive.liveplatform.core.db.PrivateDBManager;
import com.pplive.liveplatform.util.EncryptUtil;

public class UserManager {
    private static UserManager instance;

    private String mActiveUser;

    private String mActiveUserPlain;

    private String mImei;

    private Context mContext;

    private UserManager(Context context) {
        mContext = context;
        mImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null)
            instance = new UserManager(context.getApplicationContext());
        return instance;
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(mActiveUser);
    }

    public void login(String usrPlain, String pwdPlain, String token) {
        if (!isLogin()) {
            String usr = EncryptUtil.encrypt(usrPlain, mImei, EncryptUtil.EXTRA1);
            String pwd = EncryptUtil.encrypt(pwdPlain, mImei, EncryptUtil.EXTRA2);
            PrivateDBManager.getInstance(mContext).loginUser(usr, pwd, token);
            mActiveUser = usr;
            mActiveUserPlain = usrPlain;
        }
    }

    public void logout() {
        if (isLogin()) {
            PrivateDBManager.getInstance(mContext).logoutUser(mActiveUser);
            mActiveUser = null;
        }
    }

    public String getActiveUserPlain() {
        if (isLogin()) {
            return mActiveUserPlain;
        } else {
            return "";
        }
    }

}
