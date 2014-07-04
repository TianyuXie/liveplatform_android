package com.pplive.liveplatform.core.task.player;

import com.pplive.liveplatform.core.api.comment.PbarAPI;
import com.pplive.liveplatform.core.api.comment.model.FeedDetailList;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class GetFeedTask extends Task {
    static final String TAG = "_GetFeedTask";

    public final static String KEY_RESULT = "get_feed_result";

    public final static String KEY_USERNAME = "username";

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        TaskContext context = params[0];
        long pid = (Long) context.get(KEY_PID);
        String token = context.getString(KEY_TOKEN);
        FeedDetailList data = null;
        try {
            data = PbarAPI.getInstance().getFeeds(token, pid);
        } catch (Exception e) {
            return new TaskResult(TaskStatus.FAILED, "PbarService error");
        }
        if (data == null) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(KEY_RESULT, data);
        result.setContext(context);
        return result;
    }

}
