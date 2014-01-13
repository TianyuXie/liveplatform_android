package com.pplive.liveplatform;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pplive.liveplatform.dac.info.AppInfo;
import com.pplive.liveplatform.dac.info.DeviceInfo;
import com.pplive.liveplatform.dac.info.SessionInfo;
import com.pplive.liveplatform.dac.info.UserInfo;
import com.pplive.liveplatform.net.NetworkManager;
import com.pplive.liveplatform.util.DirManager;
import com.pplive.liveplatform.util.FileUtil;
import com.pplive.liveplatform.util.PPBoxUtil;
import com.pplive.liveplatform.util.StringManager;

public class LiveApplication extends Application {

    static final String TAG = LiveApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        DirManager.init(getApplicationContext());
        StringManager.init(getApplicationContext());
        initPaths();
        initImageLoader(getApplicationContext());

        AppInfo.init(getApplicationContext());
        DeviceInfo.init(getApplicationContext());
        UserInfo.init(getApplicationContext());
        SessionInfo.init();

        NetworkManager.init(getApplicationContext());

        PPBoxUtil.initPPBox(getApplicationContext());
        PPBoxUtil.startPPBox();
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 1).threadPoolSize(4)
                .denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCacheExtraOptions(160, 120).discCacheExtraOptions(160, 120, CompressFormat.JPEG, 75, null)
                .discCache(new FileCountLimitedDiscCache(new File(DirManager.getImageCachePath()), 200)).build();
        ImageLoader.getInstance().init(config);
    }

    private void initPaths() {
        FileUtil.checkPath(DirManager.getPrivateCachePath());
        FileUtil.checkPath(DirManager.getPrivateFilesPath());
        FileUtil.checkPath(DirManager.getCachePath());
        FileUtil.checkPath(DirManager.getFilesPath());
        FileUtil.checkPath(DirManager.getAppPath());
        FileUtil.checkPath(DirManager.getShareCachePath());
        FileUtil.checkPath(DirManager.getDownloadPath());
    }
}
