package com.pplive.liveplatform.core.search;

import android.content.Context;

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

public class SearchUserHelper extends BaseSearchHelper<User> {

    private String mKeyword;

    private TaskListener mLoadTaskListener = new Task.BaseTaskListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            FallList<User> fallList = (FallList<User>) event.getContext().get(Extra.KEY_RESULT);
            RefreshMode mode = (RefreshMode) event.getContext().get(Extra.KEY_LOAD_MODE);

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
    public void refresh() {
        mNextToken = "";

        super.refresh();
    }

    @Override
    void load(RefreshMode mode, int count) {
        onLoadStart();

        SearchUserTask task = new SearchUserTask();
        task.addTaskListener(mLoadTaskListener);

        TaskContext context = new TaskContext();

        context.set(Extra.KEY_LOAD_MODE, mode);

        context.set(Extra.KEY_FALL_COUNT, mFallCount);
        context.set(Extra.KEY_NEXT_TOKEN, mNextToken);
        context.set(Extra.KEY_KEYWORD, mKeyword);

        UserManager mananger = UserManager.getInstance(mContext);
        if (mananger.isLogin()) {
            context.set(Extra.KEY_USERNAME, mananger.getUsernamePlain());
            context.set(Extra.KEY_TOKEN, mananger.getToken());
        }

        task.execute(context);
    }
}
