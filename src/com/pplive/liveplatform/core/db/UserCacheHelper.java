package com.pplive.liveplatform.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserCacheHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_cache.db";
    private static final int DATABASE_VERSION = 1;

    public UserCacheHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS search (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "stime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, keyword TEXT)");
        db.execSQL("CREATE INDEX search_index ON search(keyword)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
}
