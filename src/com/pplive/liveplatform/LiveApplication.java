package com.pplive.liveplatform;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pplive.liveplatform.net.NetworkManager;
import com.pplive.liveplatform.util.FileUtil;
import com.pplive.liveplatform.util.PPBoxUtil;
import com.pplive.liveplatform.util.SysUtil;

public class LiveApplication extends Application {

    private static final String TAG = LiveApplication.class.getSimpleName();

    private static int sAppVersionCode = -1;

    private static String sAppVersionName = "unknown";

    @Override
    public void onCreate() {
        super.onCreate();

        initAppInfo(getApplicationContext());
        initPaths(getApplicationContext());
        NetworkManager.init(getApplicationContext());

        Log.d(TAG, "version code: " + getVersionCode() + "; name: " + getVersionName());

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).writeDebugLogs().build();
        ImageLoader.getInstance().init(config);

        PPBoxUtil.initPPBox(getApplicationContext());
        PPBoxUtil.startPPBox();
    }

    private static void initAppInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            sAppVersionCode = packageInfo.versionCode;
            sAppVersionName = packageInfo.versionName;

        } catch (NameNotFoundException e) {
            Log.w(TAG, "warnings: " + e.toString());
        }
    }

    private void initPaths(Context context) {
        FileUtil.checkPath(SysUtil.getAppPath(context));
        FileUtil.checkPath(SysUtil.getCachePath(context));
        FileUtil.checkPath(SysUtil.getFilesPath(context));
        FileUtil.checkPath(SysUtil.getPrivateCachePath(context));
        FileUtil.checkPath(SysUtil.getPrivateFilesPath(context));
        FileUtil.checkPath(SysUtil.getSharePath(context));
    }

    public static int getVersionCode() {
        return sAppVersionCode;
    }

    public static String getVersionName() {
        return sAppVersionName;
    }
}
