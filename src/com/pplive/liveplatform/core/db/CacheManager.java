package com.pplive.liveplatform.core.db;

import java.util.ArrayList;
import java.util.List;

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

    public void clearSearchCache() {
        mUserCache.beginTransaction();
        try {
            mUserCache.execSQL("DELETE FROM search", null);
            mUserCache.setTransactionSuccessful();
        } finally {
            mUserCache.endTransaction();
        }
    }

    public void cleanSearchCache(int limit) {
        mUserCache.beginTransaction();
        try {
            mUserCache.execSQL("DELETE FROM search WHERE _id NOT IN (SELECT _id FROM search ORDER BY stime DESC LIMIT 0,?)", new Object[] { limit });
            mUserCache.setTransactionSuccessful();
        } finally {
            mUserCache.endTransaction();
        }
    }

    public List<String> getSearchCache(int limit) {
        List<String> result = new ArrayList<String>();
        Cursor c = mUserCache.rawQuery("SELECT keyword FROM search ORDER BY stime DESC LIMIT 0,?", new String[] { String.valueOf(limit) });
        while (c.moveToNext()) {
            result.add(c.getString(0));
        }
        c.close();
        return result;
    }

    public void close() {
        mUserCacheHelper.close();
    }
}
