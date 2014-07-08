package com.pplive.liveplatform.core.search;

import android.util.Log;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.live.SearchAPI;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class SearchUserTask extends Task {

    static final String TAG = SearchUserTask.class.getSimpleName();

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        TaskContext context = params[0];
        String keyword = context.getString(Extra.KEY_KEYWORD);
        String nextToken = context.getString(Extra.KEY_NEXT_TOKEN);
        int fallCount = (Integer) context.get(Extra.KEY_FALL_COUNT);

        FallList<User> data = null;
        try {
            data = SearchAPI.getInstance().searchUser(keyword, nextToken, fallCount);
        } catch (Exception e) {
            Log.w(TAG, "");
        }

        if (null == data) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(Extra.KEY_RESULT, data);
        result.setContext(context);
        return result;
    }
}
