package com.pplive.liveplatform.core.search;

import java.util.ArrayList;
import java.util.List;

import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.android.pulltorefresh.RefreshMode;
import com.pplive.liveplatform.core.api.live.SearchAPI.LiveStatusKeyword;
import com.pplive.liveplatform.core.api.live.SearchAPI.SortKeyword;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.Task.TaskListener;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskSucceedEvent;
import com.pplive.liveplatform.core.task.home.SearchTask;

public class ProgramSearchHelper {

    private static final int DEFAULT_FALL_COUNT = 16;

    private String mKeyword = "";

    private String mTag = "";

    private int mSubjectId = -1;

    private LiveStatusKeyword mLiveStatusKeyword = LiveStatusKeyword.LIVING;

    private SortKeyword mSortKeyword = SortKeyword.START_TIME;

    private int mFallCount = DEFAULT_FALL_COUNT;

    private String mNextToken = "";

    private RefreshAdapter<Program> mAdapter;

    private List<Program> mLoadedPrograms = new ArrayList<Program>(mFallCount);

    private LoadListener mLoadListener;

    private TaskListener mLoadTaskListener = new Task.BaseTaskListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            FallList<Program> fallList = (FallList<Program>) event.getContext().get(SearchTask.KEY_RESULT);
            List<Program> tempPrograms = fallList.getList();
            RefreshMode mode = (RefreshMode) event.getContext().get(SearchTask.KEY_LOAD_MODE);

            mNextToken = fallList.nextToken();
            mLoadedPrograms.addAll(tempPrograms);

            if (mLoadedPrograms.size() < DEFAULT_FALL_COUNT && LiveStatusKeyword.LIVING == mLiveStatusKeyword) {
                mLiveStatusKeyword = LiveStatusKeyword.VOD;
                mSortKeyword = SortKeyword.VV;
                mNextToken = "";

                int count = DEFAULT_FALL_COUNT - tempPrograms.size();

                if (RefreshMode.REFRESH == mode) {
                    refresh(count);
                } else if (RefreshMode.APPEND == mode) {
                    append(count);
                }

            } else if (mLoadedPrograms.size() >= DEFAULT_FALL_COUNT || LiveStatusKeyword.VOD == mLiveStatusKeyword) {
                mode.loadData(mAdapter, mLoadedPrograms);
                mLoadedPrograms.clear();

                onLoadSucceed();
            }
        };

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            onLoadFailed();
        };

    };

    public ProgramSearchHelper(RefreshAdapter<Program> adapter) {
        mAdapter = adapter;
    }

    public void setLoadListener(LoadListener listener) {
        mLoadListener = listener;
    }

    public void searchBySubjectId(int subjectId) {
        mKeyword = "";

        mTag = "";

        mSubjectId = subjectId;

        refresh();
    }

    public void searchByKeyword(String keyword) {
        mKeyword = keyword;

        mTag = "";

        mSubjectId = -1;

        refresh();
    }

    public void searchByTag(String tag) {
        mKeyword = "";

        mTag = tag;

        mSubjectId = -1;

        refresh();
    }

    public void refresh() {

        mLiveStatusKeyword = LiveStatusKeyword.LIVING;
        mSortKeyword = SortKeyword.ONLINE;

        mNextToken = "";

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

    private void load(RefreshMode mode, int count) {

        onLoadStart();

        SearchTask task = new SearchTask();
        task.addTaskListener(mLoadTaskListener);
        TaskContext taskContext = new TaskContext();

        taskContext.set(SearchTask.KEY_LOAD_MODE, mode);

        taskContext.set(SearchTask.KEY_SUBJECT_ID, mSubjectId);
        taskContext.set(SearchTask.KEY_KEYWORD, mKeyword);
        taskContext.set(SearchTask.KEY_TAG, mTag);

        taskContext.set(SearchTask.KEY_LIVE_STATUS, mLiveStatusKeyword);
        taskContext.set(SearchTask.KEY_SORT, mSortKeyword);

        taskContext.set(SearchTask.KEY_FALL_COUNT, count);
        taskContext.set(SearchTask.KEY_NEXT_TOKEN, mNextToken);

        task.execute(taskContext);
    }

    private void onLoadStart() {
        if (null != mLoadListener) {
            mLoadListener.onLoadStart();
        }
    }

    private void onLoadSucceed() {
        if (null != mLoadListener) {
            mLoadListener.onLoadSucceed();
        }
    }

    private void onLoadFailed() {
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
