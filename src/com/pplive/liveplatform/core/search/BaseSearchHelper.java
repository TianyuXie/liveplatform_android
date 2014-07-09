package com.pplive.liveplatform.core.search;

import android.content.Context;

import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.android.pulltorefresh.RefreshMode;

public abstract class BaseSearchHelper<T> {

    private static final int DEFAULT_FALL_COUNT = 16;

    protected Context mContext;

    protected int mFallCount = DEFAULT_FALL_COUNT;

    protected String mNextToken = "";

    protected RefreshAdapter<T> mAdapter;

    private LoadListener mLoadListener;

    public BaseSearchHelper(Context context, RefreshAdapter<T> adapter) {
        mContext = context;
        mAdapter = adapter;
    }

    public void refresh() {
        refresh(DEFAULT_FALL_COUNT);
    }

    public void refresh(int count) {
        load(RefreshMode.REFRESH, count);
    }

    public void append() {
        append(DEFAULT_FALL_COUNT);
    }

    public void append(int count) {
        load(RefreshMode.APPEND, count);
    }

    abstract void load(RefreshMode mode, int count);

    public void setLoadListener(LoadListener listener) {
        mLoadListener = listener;
    }

    protected void onLoadStart() {
        if (null != mLoadListener) {
            mLoadListener.onLoadStart();
        }
    }

    protected void onLoadSucceed() {
        if (null != mLoadListener) {
            mLoadListener.onLoadSucceed();
        }
    }

    protected void onLoadFailed() {
        if (null != mLoadListener) {
            mLoadListener.onLoadFailed();
        }
    }

    public interface LoadListener {

        void onLoadStart();

        void onLoadSucceed();

        void onLoadFailed();
    }
}
