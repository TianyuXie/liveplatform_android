package com.pplive.liveplatform;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.pplive.media.MeetSDK;
import android.pplive.media.util.LogUtils;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.cache.memory.impl.LimitedAgeMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pplive.liveplatform.dac.info.AppInfo;
import com.pplive.liveplatform.dac.info.DeviceInfo;
import com.pplive.liveplatform.dac.info.SessionInfo;
import com.pplive.liveplatform.dac.info.UserInfo;
import com.pplive.liveplatform.net.NetworkManager;
import com.pplive.liveplatform.util.DirManager;
import com.pplive.liveplatform.util.PPBoxUtil;
import com.pplive.liveplatform.util.StringManager;
import com.pplive.liveplatform.util.SysUtil;

public class LiveApplication extends Application {

    static final String TAG = LiveApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        DirManager.init(getApplicationContext());
        StringManager.init(getApplicationContext());
        NetworkManager.init(getApplicationContext());
        initPaths();
        initImageLoader(getApplicationContext());

        AppInfo.init(getApplicationContext());
        DeviceInfo.init(getApplicationContext());
        UserInfo.init(getApplicationContext());
        SessionInfo.init();

        PPBoxUtil.initPPBox(getApplicationContext());
        PPBoxUtil.startPPBox();
//        MeetSDK.setLogPath(DirManager.getLogCachePath() + "/upload.log", DirManager.getLogCachePath());
//        LogUtils.logDeviceInfo();
    }

    private void initImageLoader(Context context) {
        MemoryCacheAware<String, Bitmap> memoryCache = new LimitedAgeMemoryCache<String, Bitmap>(new LruMemoryCache(2 * 1024 * 1024), 5 * 60);
        DiscCacheAware discCache = new FileCountLimitedDiscCache(new File(DirManager.getImageCachePath()), new Md5FileNameGenerator(), 200);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 1).threadPoolSize(4)
                .denyCacheImageMultipleSizesInMemory().tasksProcessingOrder(QueueProcessingType.LIFO).memoryCache(memoryCache).memoryCacheExtraOptions(120, 90)
                .discCache(discCache).discCacheExtraOptions(120, 90, CompressFormat.JPEG, 75, null).build();
        ImageLoader.getInstance().init(config);
    }

    private void initPaths() {
        SysUtil.checkPath(DirManager.getPrivateCachePath());
        SysUtil.checkPath(DirManager.getPrivateFilesPath());
        SysUtil.checkPath(DirManager.getCachePath());
        SysUtil.checkPath(DirManager.getFilesPath());
        SysUtil.checkPath(DirManager.getAppPath());
        SysUtil.checkPath(DirManager.getLogCachePath());
        SysUtil.checkPath(DirManager.getShareCachePath());
        SysUtil.checkPath(DirManager.getDownloadPath());
    }
}
