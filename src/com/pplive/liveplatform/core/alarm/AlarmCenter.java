package com.pplive.liveplatform.core.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.pplive.liveplatform.core.service.live.model.Program;

public class AlarmCenter {
    public final static String START_PRELIVE = "com.pplive.liveplatform.START_PRELIVE";

    public final static String EXTRA_PROGRAM = "extra_program";

    public final static int PRELIVE_REQUEST = 301;

    private static AlarmCenter instance;

    private AlarmManager mAlarmManager;

    private Context mAppContext;

    public static synchronized AlarmCenter getInstance(Context context) {
        if (instance == null)
            instance = new AlarmCenter(context.getApplicationContext());
        return instance;
    }

    private AlarmCenter(Context context) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mAppContext = context.getApplicationContext();
    }

    public void addPreliveAlarm(Program program) {
        if (program.getStartTime() > System.currentTimeMillis() + 10000) {
            Intent intent = new Intent(START_PRELIVE, null);
            intent.putExtra(EXTRA_PROGRAM, program);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mAppContext, PRELIVE_REQUEST, intent, 0);
            //            mAlarmManager.set(AlarmManager.RTC_WAKEUP, program.getStartTime(), pendingIntent);
                        mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
        }
    }
}
