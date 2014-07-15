package com.pplive.liveplatform.core.user;

import android.content.Context;

import com.pplive.android.pulltorefresh.FallListHelper;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.android.pulltorefresh.RefreshMode;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.Feed;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskSucceedEvent;

public class GetNotificatioinHelper extends FallListHelper<Feed> {

    private String mUsername;

    private String mCoToken;

    private Task.TaskListener mGetFeedsListener = new Task.BaseTaskListener() {

        @SuppressWarnings("unchecked")
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            TaskContext context = event.getContext();
            FallList<Feed> feeds = ((FallList<Feed>) context.get(Extra.KEY_RESULT));
            RefreshMode mode = (RefreshMode) context.get(Extra.KEY_REFRESH_MODE);

            mNextToken = feeds.nextToken();
            mode.loadData(mAdapter, feeds.getList());

            onLoadSucceed();
        };

        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            onLoadFailed();
        }
    };

    public GetNotificatioinHelper(Context context, RefreshAdapter<Feed> adapter) {
        super(context, adapter);
    }

    public void getFeeds(String coToken, String username) {
        mCoToken = coToken;
        mUsername = username;
        
        refresh();
    }

    @Override
    protected void onLoad(Task task, TaskContext context) {
        task.addTaskListener(mGetFeedsListener);

        context.set(Extra.KEY_USERNAME, mUsername);
        context.set(Extra.KEY_TOKEN, mCoToken);
    }

    @Override
    protected Task createTask() {
        return new GetNotificationTask();
    }

}
