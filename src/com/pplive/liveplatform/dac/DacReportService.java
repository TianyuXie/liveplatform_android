package com.pplive.liveplatform.dac;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.pplive.dac.logclient.DataLog;
import com.pplive.dac.logclient.DataLogSource;
import com.pplive.liveplatform.dac.stat.DacStat;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DacReportService extends IntentService {

    public static final String ACTION_DAC_REPORT = "com.pplive.liveplatform.dac";

    public static final String EXTRA_DAC_STAT = "dac_stat";

    private static final String TAG = DacReportService.class.getSimpleName();

    public DacReportService() {
        super(DacReportService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");
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
            
            Thread t = new Thread() {
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
            };
            
            t.start();
        }
    }
}
