package com.pplive.liveplatform;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pplive.liveplatform.net.NetworkManager;
import com.pplive.liveplatform.util.PPBoxUtil;

public class LiveApplication extends Application {
    
    private static final String TAG = LiveApplication.class.getSimpleName();

    private static int sAppVersionCode = -1;
    
    private static String sAppVersionName = "unknown";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        init(getApplicationContext());
        NetworkManager.init(getApplicationContext());
        
        Log.d(TAG, "version code: " + getVersionCode() + "; name: " + getVersionName());
        
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
        
        PPBoxUtil.initPPBox(getApplicationContext());
        PPBoxUtil.startPPBox();
    }
    
    private static void init(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            
            sAppVersionCode = packageInfo.versionCode;
            sAppVersionName = packageInfo.versionName;
            
        } catch (NameNotFoundException e) {
            Log.w(TAG, "warnings: " + e.toString());
        }
    }
    
    public static int getVersionCode() {
        return sAppVersionCode;
    }
    
    public static String getVersionName() {
        return sAppVersionName;
    }
}
