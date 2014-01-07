package com.pplive.liveplatform.dac;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pplive.liveplatform.dac.stat.StartDacStat;

public class DacSender {
    
    private static final String TAG = DacSender.class.getCanonicalName();

    public static void sendAppStartDac(Context context) {
        Log.d(TAG, "sendDac");
        
        Intent intent = new Intent(context, DacReportService.class);
        intent.setAction(DacReportService.ACTION_DAC_REPORT);
        intent.putExtra(DacReportService.EXTRA_DAC_STAT, new StartDacStat());
        
        context.startService(intent);
    }
    
    private DacSender() {
        
    }
}
