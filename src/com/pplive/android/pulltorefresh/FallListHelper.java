package com.pplive.android.pulltorefresh;

import android.content.Context;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;

public abstract class FallListHelper<T> {

    private static final int DEFAULT_FALL_COUNT = 16;

    protected Context mContext;

    protected int mFallCount = DEFAULT_FALL_COUNT;

    protected String mNextToken = "";

    protected RefreshAdapter<T> mAdapter;

    private LoadListener mLoadListener;

    public FallListHelper(Context context, RefreshAdapter<T> adapter) {
        mContext = context;
        mAdapter = adapter;
    }

    public final void refresh() {
        reset();
        refresh(DEFAULT_FALL_COUNT);
    }

    protected final void refresh(int count) {
        load(RefreshMode.REFRESH, count);
    }

    public final void append() {
        append(DEFAULT_FALL_COUNT);
    }

    protected final void append(int count) {
        load(RefreshMode.APPEND, count);
    }

    protected final void load(RefreshMode mode, int count) {

        onLoadStart();

        Task task = createTask();
        TaskContext context = new TaskContext();

        context.set(Extra.KEY_NEXT_TOKEN, mNextToken);
        context.set(Extra.KEY_FALL_COUNT, count);
        context.set(Extra.KEY_REFRESH_MODE, mode);

        onLoad(task, context);

        task.execute(context);
    }

    protected abstract Task createTask();

    protected void onLoad(Task task, TaskContext context) {

    }

    protected void reset() {

    }

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
