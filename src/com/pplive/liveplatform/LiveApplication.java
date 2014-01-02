package com.pplive.liveplatform;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pplive.liveplatform.net.NetworkManager;
import com.pplive.liveplatform.util.FileUtil;
import com.pplive.liveplatform.util.PPBoxUtil;
import com.pplive.liveplatform.util.StringManager;
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
        initImageLoader(getApplicationContext());

        NetworkManager.init(getApplicationContext());
        StringManager.initContext(getApplicationContext());

        PPBoxUtil.initPPBox(getApplicationContext());
        PPBoxUtil.startPPBox();

        Log.d(TAG, "version code: " + getVersionCode() + "; name: " + getVersionName());
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

    private static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 1).threadPoolSize(3)
                .denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCacheExtraOptions(160, 120).discCacheExtraOptions(160, 120, CompressFormat.JPEG, 75, null)
                .discCache(new FileCountLimitedDiscCache(new File(SysUtil.getImageCachePath(context)), 150)).build();
        ImageLoader.getInstance().init(config);
    }

    private static void initPaths(Context context) {
        FileUtil.checkPath(SysUtil.getAppPath(context));
        FileUtil.checkPath(SysUtil.getCachePath(context));
        FileUtil.checkPath(SysUtil.getFilesPath(context));
        FileUtil.checkPath(SysUtil.getPrivateCachePath(context));
        FileUtil.checkPath(SysUtil.getPrivateFilesPath(context));
        FileUtil.checkPath(SysUtil.getShareCachePath(context));
    }

    public static int getVersionCode() {
        return sAppVersionCode;
    }

    public static String getVersionName() {
        return sAppVersionName;
    }
}
