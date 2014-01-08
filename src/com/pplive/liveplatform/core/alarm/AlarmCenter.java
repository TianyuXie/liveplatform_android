package com.pplive.liveplatform.core.alarm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pplive.liveplatform.core.service.live.model.Program;

public class AlarmCenter {
    static final String TAG = "_AlarmCenter";

    public final static String START_PRELIVE = "com.pplive.liveplatform.START_PRELIVE";

    public final static String EXTRA_PROGRAM = "extra_program";

    private static AlarmCenter instance;

    private AlarmManager mAlarmManager;

    private SQLiteDatabase mAlarmDatabase;

    private AlarmDBHelper mAlarmDBHelper;

    private Context mAppContext;

    private Random mRand = new Random();

    public static synchronized AlarmCenter getInstance(Context context) {
        if (instance == null)
            instance = new AlarmCenter(context.getApplicationContext());
        return instance;
    }

    private AlarmCenter(Context context) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mAppContext = context.getApplicationContext();
        mAlarmDBHelper = new AlarmDBHelper(context);
        mAlarmDatabase = mAlarmDBHelper.getWritableDatabase();
    }

    public void addPrelive(Program program) {
        if (program.getStartTime() > System.currentTimeMillis() + 15000) {
            Intent intent = new Intent(START_PRELIVE, null);
            intent.putExtra(EXTRA_PROGRAM, program);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mAppContext, mRand.nextInt(), intent, PendingIntent.FLAG_ONE_SHOT);
            //            mAlarmManager.set(AlarmManager.RTC_WAKEUP, program.getStartTime(), pendingIntent);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
            addPreliveDB(program);
        }
    }

    public void deletePrelive(long pid) {
        mAlarmDatabase.beginTransaction();
        try {
            mAlarmDatabase.execSQL("UPDATE prelive SET status=0 WHERE pid=?", new Object[] { pid });
            mAlarmDatabase.setTransactionSuccessful();
        } finally {
            mAlarmDatabase.endTransaction();
        }
    }

    public boolean isPreliveAvailable(long pid, String username) {
        Log.d(TAG, pid + "|" + username);
        Cursor c = mAlarmDatabase.rawQuery("SELECT status FROM prelive WHERE pid=? AND owner=?", new String[] { String.valueOf(pid), username });
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("status")) == 1;
        } else {
            return false;
        }
    }

    private void addPreliveDB(Program program) {
        mAlarmDatabase.beginTransaction();
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            program.getStartTime();
            mAlarmDatabase.execSQL("INSERT INTO prelive (pid, owner, data, starttime) VALUES(?, ?, ?, datetime(?))",
                    new Object[] { program.getId(), program.getOwner(), program.toString(), df.format(new Date(program.getStartTime())) });
            mAlarmDatabase.setTransactionSuccessful();
        } finally {
            mAlarmDatabase.endTransaction();
        }
    }
}
