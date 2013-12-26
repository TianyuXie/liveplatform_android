package com.pplive.liveplatform.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.text.TextUtils;

public class SysUtil {
    public final static String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    public final static String SD_APP_PATH = SD_PATH + "/ibo";

    public static String getPrivateCachePath(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    public static String getPrivateFilesPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static String getCachePath(Context context) {
        if (hasExternalStorage()) {
            return context.getExternalCacheDir().getAbsolutePath();
        } else {
            return getPrivateCachePath(context);
        }
    }

    public static String getFilesPath(Context context) {
        if (hasExternalStorage()) {
            return context.getExternalFilesDir(null).getAbsolutePath();
        } else {
            return getPrivateFilesPath(context);
        }
    }

    public static String getAppPath(Context context) {
        if (hasExternalStorage()) {
            return SD_APP_PATH;
        } else {
            return getPrivateFilesPath(context);
        }
    }

    public static String getSharePath(Context context) {
        return getCachePath(context) + "/share";
    }

    public static boolean hasExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean checkPackage(String packageName, Context context) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
