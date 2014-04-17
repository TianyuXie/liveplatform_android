package com.pplive.liveplatform.util;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

public class SysUtil {

    public static boolean checkPath(String path) {

        if (TextUtils.isEmpty(path))
            return false;
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return file.isDirectory();
        }
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
