package com.pplive.liveplatform.util;

import android.content.Context;

public class StringManager {
    private static Context mAppContext;

    public static void init(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public static String getRes(int resid) {
        if (mAppContext != null) {
            return mAppContext.getString(resid);
        } else {
            return "";
        }
    }
}
