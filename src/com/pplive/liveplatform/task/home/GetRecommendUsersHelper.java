package com.pplive.liveplatform.task.home;

import java.util.List;

import android.content.Context;

import com.pplive.android.pulltorefresh.FallListHelper;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;

public class GetRecommendUsersHelper extends FallListHelper<User> {

    private String mUsername;

    private String mCoToken;

    private Task.TaskListener mTaskListener = new Task.BaseTaskListener() {
        @SuppressWarnings("unchecked")
        public void onTaskSucceed(Task sender, com.pplive.liveplatform.task.TaskSucceedEvent event) {
            List<User> data = (List<User>) event.getContext().get(Extra.KEY_RESULT);

            mAdapter.refreshData(data);

            onLoadSucceed();
        };

        public void onTaskFailed(Task sender, com.pplive.liveplatform.task.TaskFailedEvent event) {
            onLoadFailed();
        };
    };

    public GetRecommendUsersHelper(Context context, RefreshAdapter<User> adapter) {
        super(context, adapter);
    }

    public void loadRecommendUsers(String coToken, String username) {
        mUsername = username;
        mCoToken = coToken;

        refresh();
    }

    @Override
    protected void onLoad(Task task, TaskContext context) {
        super.onLoad(task, context);

        task.addTaskListener(mTaskListener);

        context.set(Extra.KEY_USERNAME, mUsername);
        context.set(Extra.KEY_TOKEN, mCoToken);
    }

    @Override
    protected Task createTask() {
        return new GetRecommendUsersTask();
    }

}
