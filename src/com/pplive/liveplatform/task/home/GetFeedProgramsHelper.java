package com.pplive.liveplatform.task.home;

import android.content.Context;

import com.pplive.android.pulltorefresh.FallListHelper;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.android.pulltorefresh.RefreshMode;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.Feed;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;

public class GetFeedProgramsHelper extends FallListHelper<Feed> {

    private String mCoToken;

    private String mUsername;

    private Task.TaskListener mTaskListener = new Task.BaseTaskListener() {
        @SuppressWarnings("unchecked")
        public void onTaskSucceed(Task sender, com.pplive.liveplatform.task.TaskSucceedEvent event) {
            FallList<Feed> fallList = (FallList<Feed>) event.getContext().get(Extra.KEY_RESULT);
            RefreshMode mode = (RefreshMode) event.getContext().get(Extra.KEY_REFRESH_MODE);

            mNextToken = fallList.nextToken();
            mode.loadData(mAdapter, fallList.getList());

            onLoadSucceed();
        }

        public void onTaskFailed(Task sender, com.pplive.liveplatform.task.TaskFailedEvent event) {
            onLoadFailed();
        }
    };

    public GetFeedProgramsHelper(Context context, RefreshAdapter<Feed> adapter) {
        super(context, adapter);
    }

    public void loadFeedPrograms(String coToken, String username) {
        mCoToken = coToken;
        mUsername = username;

        refresh();
    }

    @Override
    protected void onLoad(Task task, TaskContext context) {
        task.addTaskListener(mTaskListener);

        context.set(Extra.KEY_TOKEN, mCoToken);
        context.set(Extra.KEY_USERNAME, mUsername);
    }

    @Override
    protected Task createTask() {
        return new GetFeedProgramsTask();
    }

}
