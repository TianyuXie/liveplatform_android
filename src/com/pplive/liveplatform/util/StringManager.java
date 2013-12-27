package com.pplive.liveplatform.util;

import android.content.Context;

public class StringManager {
    private static Context mContext;

    public static void initContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public static String getRes(int resid) {
        if (mContext != null) {
            return mContext.getString(resid);
        } else {
            return "";
        }
    }
}
