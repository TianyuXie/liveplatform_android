package com.pplive.liveplatform.task.user;

import android.content.Context;

import com.pplive.android.pulltorefresh.FallListHelper;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.android.pulltorefresh.RefreshMode;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskFailedEvent;
import com.pplive.liveplatform.task.TaskSucceedEvent;
import com.pplive.liveplatform.task.user.GetFriendsTask.FriendType;

public class GetFriendsHelper extends FallListHelper<User> {

    static final String TAG = GetFriendsHelper.class.getSimpleName();

    private String mQueryUsername;

    private String mUsername;

    private String mCoToken;

    private FriendType mFriendType;

    private Task.TaskListener mTaskListener = new Task.BaseTaskListener() {

        @SuppressWarnings("unchecked")
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            TaskContext context = event.getContext();
            FallList<User> users = ((FallList<User>) context.get(Extra.KEY_RESULT));
            RefreshMode mode = (RefreshMode) context.get(Extra.KEY_REFRESH_MODE);

            mNextToken = users.nextToken();
            mode.loadData(mAdapter, users.getList());

            onLoadSucceed();
        };

        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            onLoadFailed();
        }
    };

    public GetFriendsHelper(Context context, RefreshAdapter<User> adapter) {
        super(context, adapter);
    }

    public void loadFollowers(String coToken, String username, String queryUsername) {
        loadFriends(coToken, username, queryUsername, FriendType.FOLLOWER);
    }

    public void loadFans(String coToken, String username, String queryUsername) {
        loadFriends(coToken, username, queryUsername, FriendType.FAN);
    }

    private void loadFriends(String coToken, String username, String queryUsername, FriendType type) {
        mCoToken = coToken;
        mUsername = username;
        mQueryUsername = queryUsername;
        mFriendType = type;
        refresh();
    }

    @Override
    protected void onLoad(Task task, TaskContext context) {
        task.addTaskListener(mTaskListener);

        context.set(Extra.KEY_USERNAME, mUsername);
        context.set(Extra.KEY_TOKEN, mCoToken);
        context.set(Extra.KEY_QUERY_USERNAME, mQueryUsername);
        context.set(Extra.KEY_FRIEND_TYPE, mFriendType);
    }

    @Override
    protected Task createTask() {
        return new GetFriendsTask();
    }
}
