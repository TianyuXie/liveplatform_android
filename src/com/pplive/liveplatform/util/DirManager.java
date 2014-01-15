package com.pplive.liveplatform.util;

import android.content.Context;
import android.os.Environment;

public class DirManager {
    private static Context mContext;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public final static String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    public final static String SD_APP_PATH = SD_PATH + "/ibo";

    public static String getPrivateCachePath() {
        return mContext.getCacheDir().getAbsolutePath();
    }

    public static String getPrivateFilesPath() {
        return mContext.getFilesDir().getAbsolutePath();
    }

    public static String getCachePath() {
        if (hasExternalStorage()) {
            return mContext.getExternalCacheDir().getAbsolutePath();
        } else {
            return getPrivateCachePath();
        }
    }

    public static String getFilesPath() {
        if (hasExternalStorage()) {
            return mContext.getExternalFilesDir(null).getAbsolutePath();
        } else {
            return getPrivateFilesPath();
        }
    }

    public static String getAppPath() {
        if (hasExternalStorage()) {
            return SD_APP_PATH;
        } else {
            return getPrivateFilesPath();
        }
    }

    public static String getShareCachePath() {
        return getCachePath() + "/share";
    }

    public static String getImageCachePath() {
        return getCachePath() + "/image";
    }

    public static String getDownloadPath() {
        return getAppPath() + "/download";
    }

    public static boolean hasExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}
