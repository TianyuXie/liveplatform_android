package com.pplive.liveplatform.dac;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.dac.stat.DacStat;
import com.pplive.liveplatform.dac.stat.PublishDacStat;
import com.pplive.liveplatform.dac.stat.StartDacStat;
import com.pplive.liveplatform.dac.stat.WatchDacStat;

public class DacSender {

    private static final String TAG = DacSender.class.getCanonicalName();

    public static void sendAppStartDac(Context context) {

        StartDacStat stat = new StartDacStat();

        stat.setIsFirstStart(SettingsProvider.getInstance(context).isFirstLaunch());

        sendDac(context, stat);
    }

    public static void sendProgramPublishDac(Context context, PublishDacStat stat) {
        sendDac(context, stat);
    }

    public static void sendProgramWatchDac(Context context, WatchDacStat stat) {
        sendDac(context, stat);
    }

    private static void sendDac(Context context, DacStat stat) {
        Log.d(TAG, "sendDac");

        Intent intent = new Intent(context, DacReportService.class);
        intent.setAction(DacReportService.ACTION_DAC_REPORT);

        intent.putExtra(DacReportService.EXTRA_DAC_STAT, stat);

        context.startService(intent);
    }

    private DacSender() {

    }
}
