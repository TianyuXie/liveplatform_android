package com.pplive.liveplatform.core.search;

import android.content.Context;

import com.pplive.android.pulltorefresh.FallListHelper;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.android.pulltorefresh.RefreshMode;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.Task.TaskListener;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskSucceedEvent;

public class SearchUserHelper extends FallListHelper<User> {

    private String mKeyword;

    private TaskListener mLoadTaskListener = new Task.BaseTaskListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            FallList<User> fallList = (FallList<User>) event.getContext().get(Extra.KEY_SEARCH_RESULT);
            RefreshMode mode = (RefreshMode) event.getContext().get(Extra.KEY_REFRESH_MODE);

            mNextToken = fallList.nextToken();
            mode.loadData(mAdapter, fallList.getList());

            onLoadSucceed();
        }

        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            onLoadFailed();
        }
    };

    public SearchUserHelper(Context context, RefreshAdapter<User> adapter) {
        super(context, adapter);
    }

    public void searchByKeyword(String keyword) {
        mKeyword = keyword;

        refresh();
    }

    @Override
    protected void onLoad(Task task, TaskContext context) {
        task.addTaskListener(mLoadTaskListener);
        context.set(Extra.KEY_KEYWORD, mKeyword);

        UserManager mananger = UserManager.getInstance(mContext);
        if (mananger.isLogin()) {
            context.set(Extra.KEY_USERNAME, mananger.getUsernamePlain());
            context.set(Extra.KEY_TOKEN, mananger.getToken());
        }
    }

    @Override
    protected Task createTask() {
        return new SearchUserTask();
    }
}
