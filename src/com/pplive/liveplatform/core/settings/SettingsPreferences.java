package com.pplive.liveplatform.core.settings;

import com.pplive.liveplatform.Constants;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsPreferences {

    private static final String KEY_CONTENT = "content";
    private static final String KEY_PRELIVE = "prelive";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_PRIVATE = "private";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ICON = "icon";
    private static final String KEY_THIRDPARTY = "thirdparty";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_FIRST_HOME = "first_home";
    private static final String KEY_LOGIN_LOCAL_TIME = "login_local_time";

    private SharedPreferences sharedPreferences;

    private static SettingsPreferences instance;

    public static synchronized SettingsPreferences getInstance(Context context) {

        if (instance == null) {
            instance = new SettingsPreferences(context);
        }

        return instance;
    }

    private SettingsPreferences(Context context) {
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public AppPrefs getAppPrefs() {
        AppPrefs userPrefs = new AppPrefs();
        userPrefs.setContentNotify(sharedPreferences.getBoolean(KEY_CONTENT, true));
        userPrefs.setPreliveNotify(sharedPreferences.getBoolean(KEY_PRELIVE, true));
        return userPrefs;
    }

    public void setAppPrefs(AppPrefs prefs) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(KEY_CONTENT, prefs.isContentNotify());
        editor.putBoolean(KEY_PRELIVE, prefs.isPreliveNotify());
        editor.commit();
    }

    public String getIcon() {
        return sharedPreferences.getString(KEY_ICON, "");
    }

    public String getNickname() {
        return sharedPreferences.getString(KEY_NICKNAME, "");
    }

    public String getUserPrivate() {
        return sharedPreferences.getString(KEY_PRIVATE, "");
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public int getThirdParty() {
        return sharedPreferences.getInt(KEY_THIRDPARTY, -1);
    }

    public long getLoginTime() {
        return sharedPreferences.getLong(KEY_LOGIN_LOCAL_TIME, 0L);
    }

    public boolean isFirstLaunch() {
        boolean result = sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true);
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(KEY_FIRST_LAUNCH, false);
        editor.commit();
        return result;
    }

    public boolean isFirstHome() {
        boolean result = sharedPreferences.getBoolean(KEY_FIRST_HOME, true);
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(KEY_FIRST_HOME, false);
        editor.commit();
        return result;
    }

    public void clearUser() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(KEY_PRIVATE, "");
        editor.putString(KEY_TOKEN, "");
        editor.putString(KEY_NICKNAME, "");
        editor.putString(KEY_ICON, "");
        editor.putLong(KEY_LOGIN_LOCAL_TIME, 0L);
        editor.putInt(KEY_THIRDPARTY, -1);
        editor.commit();
    }

    public void setUserPrivate(String userPrivate, String token) {
        String oldToken = getToken();
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(KEY_PRIVATE, userPrivate);
        editor.putString(KEY_TOKEN, token);
        if (!oldToken.equals(token)) {
            editor.putLong(KEY_LOGIN_LOCAL_TIME, System.currentTimeMillis());
        }
        editor.commit();
    }

    public void setUserInfo(String nickname, String icon) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(KEY_NICKNAME, nickname);
        editor.putString(KEY_ICON, icon);
        editor.commit();
    }

    public void setThirdparty(int thirdparty) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putInt(KEY_THIRDPARTY, thirdparty);
        editor.commit();
    }
}