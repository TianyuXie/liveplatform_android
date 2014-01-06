package com.pplive.liveplatform.dac.info;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class AppInfo {

    private static final String TAG = AppInfo.class.getSimpleName();

    private static int sAppVersionCode = -1;

    private static String sAppVersionName = "unknown";

    private static String sChannel = "0";

    public static void init(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            sAppVersionCode = packageInfo.versionCode;
            sAppVersionName = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            Log.w(TAG, e.toString());
        }

        Log.d(TAG, "App Version Code: " + sAppVersionCode);
        Log.d(TAG, "App Version Name: " + sAppVersionName);
    }

    public static int getVersionCode() {
        return sAppVersionCode;
    }

    public static String getVersionName() {
        return sAppVersionName;
    }

    public static String getChannel() {
        return sChannel;
    }

    public static String getPlatform() {
        return "android_phone";
    }

    private AppInfo() {

    }

}
