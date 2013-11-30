package com.pplive.liveplatform;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pplive.liveplatform.util.PPBoxUtil;

public class LiveApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
        
        PPBoxUtil.initPPBox(getApplicationContext());
        PPBoxUtil.startPPBox();
    }
}
