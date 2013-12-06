package com.pplive.liveplatform.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PrivateDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "private.db";
    private static final int DATABASE_VERSION = 1;

    public PrivateDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY NOT NULL, password TEXT NOT NULL, status INTEGER NOT NULL DEFAULT 1, ttime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, token TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
}
