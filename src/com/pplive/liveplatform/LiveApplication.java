package com.pplive.liveplatform;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.pplive.media.MeetSDK;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LimitedAgeMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pplive.liveplatform.core.dac.info.AppInfo;
import com.pplive.liveplatform.core.dac.info.DeviceInfo;
import com.pplive.liveplatform.core.dac.info.SessionInfo;
import com.pplive.liveplatform.core.dac.info.UserInfo;
import com.pplive.liveplatform.core.network.NetworkManager;
import com.pplive.liveplatform.util.DirManager;
import com.pplive.liveplatform.util.PPBoxUtil;
import com.pplive.liveplatform.util.StringManager;
import com.pplive.liveplatform.util.SysUtil;

public class LiveApplication extends Application {

    static final String TAG = LiveApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        StringManager.init(getApplicationContext());
        NetworkManager.init(getApplicationContext());

        initPaths(getApplicationContext());
        initImageLoader(getApplicationContext());

        AppInfo.init(getApplicationContext());
        DeviceInfo.init(getApplicationContext());
        UserInfo.init(getApplicationContext());
        SessionInfo.init();

        PPBoxUtil.initPPBox(getApplicationContext());
        PPBoxUtil.startPPBox();

        MeetSDK.setLogPath(DirManager.getLogCachePath() + "/upload.log", DirManager.getLogCachePath());

        //        AppCrashHandler.init();
        //        BreakpadUtil.registerBreakpad(new File(DirManager.getCrashCachePath()));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        ImageLoader.getInstance().clearMemoryCache();
    }

    private void initImageLoader(Context context) {
        MemoryCache memoryCache = new LimitedAgeMemoryCache(new LruMemoryCache(2 * 1024 * 1024), 5 * 60);
        DiskCache diskCache = new LruDiscCache(new File(DirManager.getImageCachePath()), new Md5FileNameGenerator(), 1000);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 1).threadPoolSize(4)
                .denyCacheImageMultipleSizesInMemory().tasksProcessingOrder(QueueProcessingType.LIFO).memoryCache(memoryCache).diskCache(diskCache).build();
        ImageLoader.getInstance().init(config);
    }

    private void initPaths(Context context) {
        DirManager.init(context);

        SysUtil.checkPath(DirManager.getPrivateCachePath());
        SysUtil.checkPath(DirManager.getPrivateFilesPath());
        SysUtil.checkPath(DirManager.getCachePath());
        SysUtil.checkPath(DirManager.getFilesPath());
        SysUtil.checkPath(DirManager.getAppPath());
        SysUtil.checkPath(DirManager.getLogCachePath());
        SysUtil.checkPath(DirManager.getCrashCachePath());
        SysUtil.checkPath(DirManager.getShareCachePath());
        SysUtil.checkPath(DirManager.getDownloadPath());
    }
}
