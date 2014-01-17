package com.pplive.liveplatform.util;

import android.content.Context;

public class StringManager {
    private static Context sAppContext;

    public static void init(Context context) {
        sAppContext = context.getApplicationContext();
    }

    public static String getRes(int resid) {
        if (sAppContext != null) {
            return sAppContext.getString(resid);
        } else {
            return "";
        }
    }
}
