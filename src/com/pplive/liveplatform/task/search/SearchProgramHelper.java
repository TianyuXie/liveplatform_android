package com.pplive.liveplatform.task.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.android.pulltorefresh.FallListHelper;
import com.pplive.android.pulltorefresh.RefreshMode;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.live.SearchAPI.LiveStatusKeyword;
import com.pplive.liveplatform.core.api.live.SearchAPI.SortKeyword;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskFailedEvent;
import com.pplive.liveplatform.task.TaskSucceedEvent;
import com.pplive.liveplatform.task.Task.TaskListener;

public class SearchProgramHelper extends FallListHelper<Program> {

    private String mKeyword = "";

    private String mTag = "";

    private int mSubjectId = -1;

    private LiveStatusKeyword mLiveStatusKeyword = LiveStatusKeyword.LIVING;

    private SortKeyword mSortKeyword = SortKeyword.START_TIME;

    private List<Program> mLoadedData = new ArrayList<Program>(mFallCount);

    private TaskListener mLoadTaskListener = new Task.BaseTaskListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            FallList<Program> fallList = (FallList<Program>) event.getContext().get(Extra.KEY_SEARCH_RESULT);
            List<Program> tempPrograms = fallList.getList();
            RefreshMode mode = (RefreshMode) event.getContext().get(Extra.KEY_REFRESH_MODE);

            mNextToken = fallList.nextToken();
            mLoadedData.addAll(tempPrograms);

            if (mLoadedData.size() < mFallCount && LiveStatusKeyword.LIVING == mLiveStatusKeyword) {
                mLiveStatusKeyword = LiveStatusKeyword.VOD;
                mSortKeyword = SortKeyword.VV;
                mNextToken = "";

                int count = mFallCount - tempPrograms.size();

                if (RefreshMode.REFRESH == mode) {
                    refresh(count);
                } else if (RefreshMode.APPEND == mode) {
                    append(count);
                }

            } else if (mLoadedData.size() >= mFallCount || LiveStatusKeyword.VOD == mLiveStatusKeyword) {
                mode.loadData(mAdapter, mLoadedData);
                mLoadedData.clear();

                onLoadSucceed();
            }
        };

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            onLoadFailed();
        };

    };

    public SearchProgramHelper(Context context, RefreshAdapter<Program> adapter) {
        super(context, adapter);
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

    public String getKeyword() {
        return mKeyword;
    }

    public String getTag() {
        return mTag;
    }

    public int getSubjectId() {
        return mSubjectId;
    }

    @Override
    protected void reset() {
        mLiveStatusKeyword = LiveStatusKeyword.LIVING;
        mSortKeyword = SortKeyword.ONLINE;
    }

    @Override
    protected void onLoad(Task task, TaskContext context) {
        task.addTaskListener(mLoadTaskListener);

        context.set(Extra.KEY_SUBJECT_ID, mSubjectId);
        context.set(Extra.KEY_KEYWORD, mKeyword);
        context.set(Extra.KEY_TAG, mTag);

        context.set(Extra.KEY_LIVE_STATUS, mLiveStatusKeyword);
        context.set(Extra.KEY_SORT, mSortKeyword);
    }

    @Override
    protected Task createTask() {
        return new SearchProgramTask();
    }

}
