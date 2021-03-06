package com.pplive.liveplatform.core.cache;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class SearchCacheManager {

    private SQLiteDatabase mUserCache;

    private CacheHelper mUserCacheHelper;

    private static SearchCacheManager instance;

    public static synchronized SearchCacheManager getInstance(Context context) {
        if (instance == null)
            instance = new SearchCacheManager(context.getApplicationContext());
        return instance;
    }

    private SearchCacheManager(Context context) {
        mUserCacheHelper = new CacheHelper(context);
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
        if (!TextUtils.isEmpty(keyword)) {
            if (getCount(keyword) != 0) {
                updateSearchCache(keyword);
            } else {
                addSearchCache(keyword);
            }
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
            mUserCache.execSQL("DELETE FROM search", new Object[] {});
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
