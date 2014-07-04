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

public class SearchProgramHelper extends BaseSearchHelper<Program> {

    private static final int DEFAULT_FALL_COUNT = 16;

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
            FallList<Program> fallList = (FallList<Program>) event.getContext().get(SearchProgramTask.KEY_RESULT);
            List<Program> tempPrograms = fallList.getList();
            RefreshMode mode = (RefreshMode) event.getContext().get(SearchProgramTask.KEY_LOAD_MODE);

            mNextToken = fallList.nextToken();
            mLoadedData.addAll(tempPrograms);

            if (mLoadedData.size() < DEFAULT_FALL_COUNT && LiveStatusKeyword.LIVING == mLiveStatusKeyword) {
                mLiveStatusKeyword = LiveStatusKeyword.VOD;
                mSortKeyword = SortKeyword.VV;
                mNextToken = "";

                int count = DEFAULT_FALL_COUNT - tempPrograms.size();

                if (RefreshMode.REFRESH == mode) {
                    refresh(count);
                } else if (RefreshMode.APPEND == mode) {
                    append(count);
                }

            } else if (mLoadedData.size() >= DEFAULT_FALL_COUNT || LiveStatusKeyword.VOD == mLiveStatusKeyword) {
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

    public SearchProgramHelper(RefreshAdapter<Program> adapter) {
        super(adapter);
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
    public void refresh() {

        mLiveStatusKeyword = LiveStatusKeyword.LIVING;
        mSortKeyword = SortKeyword.ONLINE;

        mNextToken = "";

        refresh(DEFAULT_FALL_COUNT);
    }

    @Override
    void load(RefreshMode mode, int count) {

        onLoadStart();

        SearchProgramTask task = new SearchProgramTask();
        task.addTaskListener(mLoadTaskListener);
        TaskContext context = new TaskContext();

        context.set(SearchProgramTask.KEY_LOAD_MODE, mode);

        context.set(SearchProgramTask.KEY_SUBJECT_ID, mSubjectId);
        context.set(SearchProgramTask.KEY_KEYWORD, mKeyword);
        context.set(SearchProgramTask.KEY_TAG, mTag);

        context.set(SearchProgramTask.KEY_LIVE_STATUS, mLiveStatusKeyword);
        context.set(SearchProgramTask.KEY_SORT, mSortKeyword);

        context.set(SearchProgramTask.KEY_FALL_COUNT, count);
        context.set(SearchProgramTask.KEY_NEXT_TOKEN, mNextToken);

        task.execute(context);
    }

}
