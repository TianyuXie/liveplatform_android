package com.pplive.liveplatform.dac;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.dac.stat.StartDacStat;

public class DacSender {
    
    private static final String TAG = DacSender.class.getCanonicalName();

    public static void sendAppStartDac(Context context) {
        Log.d(TAG, "sendDac");
        
        Intent intent = new Intent(context, DacReportService.class);
        intent.setAction(DacReportService.ACTION_DAC_REPORT);
        
        StartDacStat startStat = new StartDacStat();
        
        startStat.setIsFirstStart(SettingsProvider.getInstance(context).isFirstLaunch());
        
        intent.putExtra(DacReportService.EXTRA_DAC_STAT, startStat);
        
        context.startService(intent);
    }
    
    private DacSender() {
        
    }
}
