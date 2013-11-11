package com.pplive.liveplatform.core.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CacheManager {
    private SQLiteDatabase mUserCache;

    private UserCacheHelper mUserCacheHelper;

    public CacheManager(Context context) {
        mUserCacheHelper = new UserCacheHelper(context);
        mUserCache = mUserCacheHelper.getWritableDatabase();
    }

    private int getCount(String keyword) {
        Cursor c = mUserCache.rawQuery("SELECT count(1) FROM search WHERE keyword = ?", new String[] { keyword });
        int result = 0;
        if (c.moveToFirst()) {
            result = c.getInt(0);
        }
        c.close();
        return result;
    }

    public void updateCache(String keyword) {
        if (getCount(keyword) != 0) {
            updateSearchCache(keyword);
        } else {
            addSearchCache(keyword);
        }
    }

    private void addSearchCache(String keyword) {
        mUserCache.beginTransaction();
        try {
            mUserCache.execSQL("INSERT INTO search (keyword) VALUES(?)", new Object[] { keyword });
            mUserCache.setTransactionSuccessful();
        } finally {
            mUserCache.endTransaction();
        }
    }

    private void updateSearchCache(String keyword) {
        mUserCache.beginTransaction();
        try {
            mUserCache.execSQL("UPDATE search SET stime = datetime() WHERE keyword = (?)", new Object[] { keyword });
            mUserCache.setTransactionSuccessful();
        } finally {
            mUserCache.endTransaction();
        }
    }

    private void cleanSearchCache() {
        mUserCache.beginTransaction();
        try {
            mUserCache.execSQL("DELETE FROM search WHERE _id NOT IN (SELECT _id FROM search ORDER BY stime DESC LIMIT 0,30)", null);
            mUserCache.setTransactionSuccessful();
        } finally {
            mUserCache.endTransaction();
        }
    }

    public void close() {
        mUserCacheHelper.close();
    }
}
