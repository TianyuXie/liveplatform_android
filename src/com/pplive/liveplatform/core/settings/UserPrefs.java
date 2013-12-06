package com.pplive.liveplatform.core.settings;

public class UserPrefs {
    private String mNickname;

    private boolean mPreliveNotify;

    private boolean mContentNotify;

    public boolean isPreliveNotify() {
        return mPreliveNotify;
    }

    public void setPreliveNotify(boolean notify) {
        this.mPreliveNotify = notify;
    }

    public boolean isContentNotify() {
        return mContentNotify;
    }

    public void setContentNotify(boolean notify) {
        this.mContentNotify = notify;
    }

    public void setNickname(String nickname) {
        this.mNickname = nickname;
    }

    public String getNickname() {
        return mNickname;
    }

//    public String getUserPlain(Context context) {
//        return EncryptUtil.decrypt(mUser, context);
//    }
//
//    public String getUser() {
//        return mUser;
//    }
//
//    public void setUser(String user) {
//        this.mUser = user;
//    }
//
//    public String getPassword() {
//        return mPassword;
//    }
//
//    public void setPassword(String password) {
//        this.mPassword = password;
//    }
//
//    public String getPasswordPlain(Context context) {
//        return EncryptUtil.decrypt(mPassword, context);
//    }

}