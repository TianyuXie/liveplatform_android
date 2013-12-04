package com.pplive.liveplatform.core.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsProvider {
    private static final String PREFS_NAME = "com.pplive.liveplatform_preferences";

    private static final String KEY_CONTENT = "content";
    private static final String KEY_PRELIVE = "prelive";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_USER_ENCRYPT = "user_encrypt";
    private static final String KEY_PW_ENCRYPT = "password_encrypt";

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

    public UserPrefs getPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setContentNotify(sharedPreferences.getBoolean(KEY_CONTENT, true));
        userPrefs.setPreliveNotify(sharedPreferences.getBoolean(KEY_PRELIVE, true));
        userPrefs.setNickname(sharedPreferences.getString(KEY_NICKNAME, ""));
        userPrefs.setUser(sharedPreferences.getString(KEY_USER_ENCRYPT, ""));
        userPrefs.setPassword(sharedPreferences.getString(KEY_PW_ENCRYPT, ""));
        return userPrefs;
    }

    public void setPrefs(UserPrefs userPrefs) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(KEY_CONTENT, userPrefs.isContentNotify());
        editor.putBoolean(KEY_PRELIVE, userPrefs.isPreliveNotify());
        editor.putString(KEY_NICKNAME, userPrefs.getNickname());
        editor.putString(KEY_USER_ENCRYPT, userPrefs.getUser());
        editor.putString(KEY_PW_ENCRYPT, userPrefs.getPassword());
        editor.commit();
    }
}