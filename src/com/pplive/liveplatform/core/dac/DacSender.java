package com.pplive.liveplatform.core.dac;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pplive.liveplatform.core.dac.stat.DacStat;
import com.pplive.liveplatform.core.dac.stat.PublishDacStat;
import com.pplive.liveplatform.core.dac.stat.StartDacStat;
import com.pplive.liveplatform.core.dac.stat.WatchDacStat;

public class DacSender {

    private static final String TAG = DacSender.class.getCanonicalName();

    public static void sendAppStartDac(Context context, boolean firstlaunch) {

        StartDacStat stat = new StartDacStat();

        stat.setIsFirstStart(firstlaunch);

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