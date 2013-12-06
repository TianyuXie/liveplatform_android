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

    private String mToken;

    private String mImei;

    private Context mContext;

    private UserManager(Context context) {
        mContext = context;
        mImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        mActiveUser = PrivateDBManager.getInstance(mContext).getActiveUser();
        if (!TextUtils.isEmpty(mActiveUser)) {
            mToken = PrivateDBManager.getInstance(mContext).getToken(mActiveUser);
            mActiveUserPlain = EncryptUtil.decrypt(mActiveUser, mImei, EncryptUtil.EXTRA1);
        }
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
            mToken = token;
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

    public String getToken() {
        if (isLogin()) {
            return mToken;
        } else {
            return "";
        }
    }

}
