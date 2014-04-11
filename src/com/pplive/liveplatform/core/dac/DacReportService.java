package com.pplive.liveplatform.core.dac;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.pplive.dac.logclient.DataLog;
import com.pplive.dac.logclient.DataLogSource;
import com.pplive.liveplatform.core.dac.stat.DacStat;
import com.pplive.liveplatform.core.dac.stat.PublishDacStat;
import com.pplive.liveplatform.core.dac.stat.StartDacStat;
import com.pplive.liveplatform.core.dac.stat.WatchDacStat;

public class DacReportService extends IntentService {

    public static final String ACTION_DAC_REPORT = "com.pplive.liveplatform.dac";

    public static final String EXTRA_DAC_STAT = "dac_stat";

    private static final String TAG = DacReportService.class.getSimpleName();

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

    private Executor mExecutor;

    public DacReportService() {
        super(DacReportService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        mExecutor = Executors.newFixedThreadPool(10 /* number of threads */);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        Log.d(TAG, "flags: " + flags + "; startId: " + startId);
        Log.d(TAG, "actin: " + intent.getAction());

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");

        return super.onBind(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        if (ACTION_DAC_REPORT.equals(intent.getAction())) {
            DacStat stat = (DacStat) intent.getSerializableExtra(EXTRA_DAC_STAT);

            DataLog log = new DataLog(DataLogSource.LivePlatform);

            final String url = log.getLogUrl(stat.getMetaItems(), stat.getValueItems());

            Log.d(TAG, "url: " + url);

            mExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    HttpGet get = new HttpGet(url);

                    HttpClient client = new DefaultHttpClient();

                    try {
                        client.execute(get);
                    } catch (ClientProtocolException e) {
                        Log.w(TAG, e.toString());
                    } catch (IOException e) {
                        Log.w(TAG, e.toString());
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
