package com.pplive.liveplatform.util;

import android.content.Context;
import android.util.DisplayMetrics;

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
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return Math.min(dm.widthPixels, dm.heightPixels);
    }

    public static int getHeightPx(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return Math.max(dm.widthPixels, dm.heightPixels);
    }

    public static boolean isLandscape(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels > dm.heightPixels;
    }

    public static boolean isPortrait(Context context) {
        return !isLandscape(context);
    }
}
