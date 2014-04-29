package com.pplive.liveplatform.core.network;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.core.service.live.MediaService;

public class WifiSpeedCheckService extends IntentService {

    public static final String ACTION_CHECK_WIFI_SPEED = "com.pplive.liveplatform.speed";

    static final String TAG = WifiSpeedCheckService.class.getSimpleName();

    public WifiSpeedCheckService() {
        super(WifiSpeedCheckService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        if (ACTION_CHECK_WIFI_SPEED.equals(intent.getAction())) {

            String oldIP = QualityPreferences.getInstance(getApplicationContext()).getIP();
            String ip = MediaService.getInstance().getClientIPAddress();
            if (!TextUtils.isEmpty(ip) && !oldIP.equals(ip)) {
                Log.d(TAG, "check Wifi Speed");
                float speed = MediaService.getInstance().getAvgNetSpeed(256 * 1024, 5);
                if (speed > 0) {
                    QualityPreferences.getInstance(getApplicationContext()).reset();
                    QualityPreferences.getInstance(getApplicationContext()).setIP(ip);
                    QualityPreferences.getInstance(getApplicationContext()).setSpeed(speed);
                }
            }
        }
    }
}
