package com.pplive.liveplatform;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pplive.liveplatform.dac.info.AppInfo;
import com.pplive.liveplatform.dac.info.DeviceInfo;
import com.pplive.liveplatform.dac.info.SessionInfo;
import com.pplive.liveplatform.net.NetworkManager;
import com.pplive.liveplatform.util.FileUtil;
import com.pplive.liveplatform.util.PPBoxUtil;
import com.pplive.liveplatform.util.StringManager;
import com.pplive.liveplatform.util.SysUtil;

public class LiveApplication extends Application {

    private static final String TAG = LiveApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d(TAG, "onCreate");

        initPaths(getApplicationContext());
        initImageLoader(getApplicationContext());
        
        AppInfo.init(getApplicationContext());
        DeviceInfo.init(getApplicationContext());
        SessionInfo.init();

        NetworkManager.init(getApplicationContext());
        StringManager.initContext(getApplicationContext());
        
        PPBoxUtil.initPPBox(getApplicationContext());
        PPBoxUtil.startPPBox();
        
    }
    
    private void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 1).threadPoolSize(4)
                .denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCacheExtraOptions(160, 120).discCacheExtraOptions(160, 120, CompressFormat.JPEG, 75, null)
                .discCache(new FileCountLimitedDiscCache(new File(SysUtil.getImageCachePath(context)), 200)).build();
        ImageLoader.getInstance().init(config);
    }

    private void initPaths(Context context) {
        FileUtil.checkPath(SysUtil.getAppPath(context));
        FileUtil.checkPath(SysUtil.getCachePath(context));
        FileUtil.checkPath(SysUtil.getFilesPath(context));
        FileUtil.checkPath(SysUtil.getPrivateCachePath(context));
        FileUtil.checkPath(SysUtil.getPrivateFilesPath(context));
        FileUtil.checkPath(SysUtil.getShareCachePath(context));
    }
}
