package com.pplive.liveplatform.core.task.player;

import android.util.Log;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.comment.PbarAPI;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class PutFeedTask extends Task {
    static final String TAG = "_PutFeedTask";

    public final static String KEY_CONTENT = "content";

    public final static String KEY_FID = "fid";

    public final static String TYPE = "PutFeed";

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        TaskContext context = params[0];
        long pid = (Long) context.get(Extra.KEY_PROGRAM_ID);
        String token = context.getString(Extra.KEY_TOKEN);
        String content = context.getString(KEY_CONTENT);
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        long feedId = -1;
        try {
            feedId = PbarAPI.getInstance().putFeed(token, pid, content);
        } catch (Exception e) {
            result.setStatus(TaskStatus.FAILED);
            result.setMessage("PbarService error");
            result.setContext(context);
            return result;
        }
        if (feedId <= 0) {
            result.setStatus(TaskStatus.FAILED);
            result.setMessage("Invalid feed id");
            result.setContext(context);
            return result;
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        Log.d(TAG, "feedId:" + feedId);
        context.set(KEY_FID, feedId);
        result.setContext(context);
        return result;
    }

}
