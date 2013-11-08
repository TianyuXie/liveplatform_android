package com.pplive.liveplatform.util;

import android.content.Context;

public class DisplayUtil {
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getWidthPx(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
    }
}
