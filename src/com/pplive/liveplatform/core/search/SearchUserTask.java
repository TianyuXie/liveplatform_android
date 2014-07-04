package com.pplive.liveplatform.core.search;

import com.pplive.liveplatform.core.api.live.SearchAPI;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class SearchUserTask extends Task {
    
    public final static String KEY_RESULT = "search_task_result";
    public final static String KEY_LOAD_MODE = "search_task_type";

    public final static String KEY_KEYWORD = "keyword";

    public final static String KEY_NEXT_TOKEN = "next_token";
    public final static String KEY_FALL_COUNT = "fall_count";

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        TaskContext context = params[0];
        String keyword = context.getString(KEY_KEYWORD);
        String nextToken = context.getString(KEY_NEXT_TOKEN);
        int fallCount = (Integer) context.get(KEY_FALL_COUNT);

        FallList<User> data = null;
        try {
            data = SearchAPI.getInstance().searchUser(keyword, nextToken, fallCount);
        } catch (Exception e) {

        }

        if (null == data) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(KEY_RESULT, data);
        result.setContext(context);
        return result;
    }

}
