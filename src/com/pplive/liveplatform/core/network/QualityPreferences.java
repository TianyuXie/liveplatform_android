package com.pplive.liveplatform.core.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.record.Quality;

public class QualityPreferences {

    public static final String KEY_IP = "ip";

    public static final String KEY_SPEED = "speed";

    public static final String KEY_QUALITY = "quality";

    private static QualityPreferences sInstance;

    public static QualityPreferences getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new QualityPreferences(context);
        }

        return sInstance;
    }

    private SharedPreferences mSharedPreferences;

    private QualityPreferences(Context context) {
        mSharedPreferences = context.getApplicationContext().getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setIP(String ip) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(KEY_IP, ip);
        edit.commit();
    }

    public String getIP() {
        return mSharedPreferences.getString(KEY_IP, "unknown");
    }

    public void setSpeed(float speed) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putFloat(KEY_SPEED, speed);
        edit.commit();
    }

    public float getSpeed() {
        return mSharedPreferences.getFloat(KEY_SPEED, -1f);
    }

    public void setQuality(Quality quality) {
        if (null != quality) {
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putInt(KEY_QUALITY, quality.getIntValue());
            edit.commit();
        }
    }

    public Quality getQuality() {
        int value = mSharedPreferences.getInt(KEY_QUALITY, -1);

        return Quality.mapIntToValue(value);
    }

    public void reset() {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.remove(KEY_IP);
        edit.remove(KEY_SPEED);
        edit.remove(KEY_QUALITY);
        edit.commit();
    }

}
