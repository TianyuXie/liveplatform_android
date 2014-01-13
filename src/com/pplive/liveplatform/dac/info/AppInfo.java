package com.pplive.liveplatform.dac.info;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class AppInfo {

    private static final String TAG = AppInfo.class.getSimpleName();

    private static int sAppVersionCode = -1;

    private static String sAppVersionName = "unknown";

    private static String sInstallChannel = "unknown";

    public static void init(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            sAppVersionCode = packageInfo.versionCode;
            sAppVersionName = packageInfo.versionName;

            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            sInstallChannel = applicationInfo.metaData.getString("INSTALL_CHANNEL");

        } catch (NameNotFoundException e) {
            Log.w(TAG, e.toString());
        }

        Log.d(TAG, "App Version Code: " + sAppVersionCode);
        Log.d(TAG, "App Version Name: " + sAppVersionName);
        Log.d(TAG, "App Install Channel: " + sInstallChannel);
    }

    public static int getVersionCode() {
        return sAppVersionCode;
    }

    public static String getVersionName() {
        return sAppVersionName;
    }

    public static String getChannel() {
        return "liveplatform_" + sInstallChannel;
    }

    public static String getPlatform() {
        return "android_ibo";
    }

    private AppInfo() {

    }

}
