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

import com.google.gson.Gson;
import com.pplive.liveplatform.core.api.live.model.Program;

public class AlarmCenter {
    static final String TAG = "_AlarmCenter";

    public final static String START_PRELIVE = "com.pplive.liveplatform.START_PRELIVE";

    public final static String EXTRA_PROGRAM = "extra_program";

    private static AlarmCenter instance;

    private AlarmManager mAlarmManager;

    private SQLiteDatabase mAlarmDatabase;

    private AlarmDBHelper mAlarmDBHelper;

    private Context mAppContext;

    private Random mReqGenerator;

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
        mReqGenerator = new Random();
    }

    public void syncPrelive() {
        Cursor c = mAlarmDatabase.rawQuery("SELECT data FROM prelive WHERE status=1 AND starttime > datetime('now')", new String[] {});
        Gson gson = new Gson();
        while (c.moveToNext()) {
            String data = c.getString(c.getColumnIndex("data"));
            Log.d(TAG, data);
            addPreliveAlarm(gson.fromJson(data, Program.class));
        }
        c.close();
    }

    public void addPrelive(Program program) {
        if (program.getStartTime() > System.currentTimeMillis() + 15000) {
            addPreliveAlarm(program);
            addPreliveDB(program);
        }
    }

    private void addPreliveAlarm(Program program) {
        Intent intent = new Intent(START_PRELIVE, null);
        intent.putExtra(EXTRA_PROGRAM, program);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mAppContext, mReqGenerator.nextInt(), intent, PendingIntent.FLAG_ONE_SHOT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, program.getStartTime(), pendingIntent);
    }

    private void addPreliveDB(Program program) {
        mAlarmDatabase.beginTransaction();
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            mAlarmDatabase.execSQL("INSERT INTO prelive (pid, owner, data, starttime) VALUES(?, ?, ?, datetime(?))",
                    new Object[] { program.getId(), program.getOwner(), program.toString(), df.format(new Date(program.getStartTime())) });
            mAlarmDatabase.setTransactionSuccessful();
        } finally {
            mAlarmDatabase.endTransaction();
        }
    }

    public void startPrelive(long pid) {
        mAlarmDatabase.beginTransaction();
        try {
            mAlarmDatabase.execSQL("UPDATE prelive SET status=0 WHERE pid=?", new Object[] { pid });
            mAlarmDatabase.setTransactionSuccessful();
        } finally {
            mAlarmDatabase.endTransaction();
        }
    }

    public void deletePrelive(long pid) {
        mAlarmDatabase.delete("prelive", "pid=?", new String[] { String.valueOf(pid) });
    }

    public boolean isAvailablePrelive(long pid, String username) {
        Cursor c = mAlarmDatabase.rawQuery("SELECT status FROM prelive WHERE pid=? AND owner=?", new String[] { String.valueOf(pid), username });
        boolean result = false;
        if (c.moveToFirst()) {
            result = c.getInt(c.getColumnIndex("status")) == 1;
        }
        c.close();
        return result;
    }
}
