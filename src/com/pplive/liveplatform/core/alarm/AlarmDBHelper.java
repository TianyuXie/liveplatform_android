package com.pplive.liveplatform.core.alarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alarm.db";
    private static final int DATABASE_VERSION = 1;

    public AlarmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS prelive (pid BIGINT PRIMARY KEY, status INTEGER NOT NULL DEFAULT 1, starttime DATETIME NOT NULL, owner TEXT NOT NULL, data TEXT NOT NULL)");
        db.execSQL("CREATE INDEX prelive_index ON prelive(pid)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
}
