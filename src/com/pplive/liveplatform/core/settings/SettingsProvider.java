package com.pplive.liveplatform.core.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsProvider {
    private static final String PREFS_NAME = "com.pplive.liveplatform_preferences";

    private static final String KEY_CONTENT = "content";
    private static final String KEY_PRELIVE = "prelive";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_PRIVATE = "private";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ICON = "icon";

    private SharedPreferences sharedPreferences;

    private static SettingsProvider instance;

    public static synchronized SettingsProvider getInstance(Context context) {
        if (instance == null)
            instance = new SettingsProvider(context);
        return instance;
    }

    private SettingsProvider(Context context) {
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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

    public void clearUser() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(KEY_PRIVATE, "");
        editor.putString(KEY_TOKEN, "");
        editor.putString(KEY_NICKNAME, "");
        editor.putString(KEY_ICON, "");
        editor.commit();
    }

    public void setUserPrivate(String userPrivate, String token) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(KEY_PRIVATE, userPrivate);
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public void setUserInfo(String nickname, String icon) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(KEY_NICKNAME, nickname);
        editor.putString(KEY_ICON, icon);
        editor.commit();
    }
}