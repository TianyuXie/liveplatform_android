package com.pplive.liveplatform.core.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsProvider {
    private static final String PREFS_NAME = "com.pplive.sliveplatform_preferences";

    private static final String KEY_CONTENT = "content";
    private static final String KEY_PRELIVE = "prelive";

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
        return userPrefs;
    }

    public void setPrefs(UserPrefs userPrefs) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(KEY_CONTENT, userPrefs.isContentNotify());
        editor.putBoolean(KEY_PRELIVE, userPrefs.isPreliveNotify());
        editor.commit();
    }
}