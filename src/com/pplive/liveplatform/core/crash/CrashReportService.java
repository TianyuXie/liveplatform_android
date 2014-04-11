package com.pplive.liveplatform.core.crash;

import java.io.File;

import com.pplive.liveplatform.util.DirManager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CrashReportService extends IntentService {

    private static final String TAG = CrashReportService.class.getSimpleName();
    
    public static void reportCrash(Context context) {
        Intent intent = new Intent(context, CrashReportService.class);

        context.startService(intent);
    }

    public CrashReportService() {
        super(CrashReportService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        
        Log.d(TAG, "onHandleIntent");
        
        File root = new File(DirManager.getCrashCachePath());
        
        Log.d(TAG, root.getAbsolutePath().toString());
        
        File[] files = root.listFiles();
        for (int i = 0; null != files && i < files.length; ++i) {
            Log.d(TAG, files[i].getAbsolutePath().toString());
        }
    }

}
