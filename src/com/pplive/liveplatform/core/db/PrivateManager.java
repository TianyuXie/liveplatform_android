package com.pplive.liveplatform.core.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PrivateManager {
    private SQLiteDatabase mPrivateDB;

    private PrivateHelper mPrivateHelper;
    private static PrivateManager instance;

    public static synchronized PrivateManager getInstance(Context context) {
        if (instance == null)
            instance = new PrivateManager(context.getApplicationContext());
        return instance;
    }

    private PrivateManager(Context context) {
        mPrivateHelper = new PrivateHelper(context);
        mPrivateDB = mPrivateHelper.getWritableDatabase();
    }

    private void addUser(String username, String password, String token) {
        mPrivateDB.beginTransaction();
        try {
            mPrivateDB.execSQL("UPDATE users SET status=0", new Object[] {});
            mPrivateDB.execSQL("INSERT INTO users (username, password, token) VALUES (?, ?, ?)", new Object[] { username, password, token });
            mPrivateDB.setTransactionSuccessful();
        } finally {
            mPrivateDB.endTransaction();
        }
    }

    private void updateUser(String username, String password, String token) {
        mPrivateDB.beginTransaction();
        try {
            mPrivateDB.execSQL("UPDATE users SET status=0", new Object[] {});
            mPrivateDB.execSQL("UPDATE users SET password=?, token=?, ttime=datetime(), status=1 WHERE username=?", new Object[] { password, token, username });
            mPrivateDB.setTransactionSuccessful();
        } finally {
            mPrivateDB.endTransaction();
        }
    }

    public void loginUser(String username, String password, String token) {
        if (getCount(username) != 0) {
            updateUser(username, password, token);
        } else {
            addUser(username, password, token);
        }
    }

    private int getCount(String username) {
        Cursor c = mPrivateDB.rawQuery("SELECT count(1) FROM users WHERE username=?", new String[] { username });
        int result = 0;
        if (c.moveToFirst()) {
            result = c.getInt(0);
        }
        c.close();
        return result;
    }

    public String getToken(String username) {
        Cursor c = mPrivateDB.rawQuery("SELECT token FROM users WHERE username=? AND status=1", new String[] { username });
        String result = "";
        if (c.moveToFirst()) {
            result = c.getString(0);
        }
        c.close();
        return result;
    }

    public String getActiveUser() {
        Cursor c = mPrivateDB.rawQuery("SELECT username FROM users WHERE status=1", null);
        String result = "";
        if (c.moveToFirst()) {
            result = c.getString(0);
        }
        c.close();
        return result;
    }

    public void logoutUser(String username) {
        mPrivateDB.beginTransaction();
        try {
            mPrivateDB.execSQL("UPDATE users SET status=0 where username=?", new Object[] { username });
            mPrivateDB.setTransactionSuccessful();
        } finally {
            mPrivateDB.endTransaction();
        }
    }

    public void close() {
        mPrivateHelper.close();
    }
}
