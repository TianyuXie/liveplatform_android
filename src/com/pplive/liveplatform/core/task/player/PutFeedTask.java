package com.pplive.liveplatform.core.task.player;

import com.pplive.liveplatform.core.service.comment.PbarService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class PutFeedTask extends Task {
    static final String TAG = "_PutFeedTask";

    public final static String KEY_CONTENT = "content";

    public final static String KEY_FID = "fid";

    private final String ID = StringUtil.newGuid();
    public final static String TYPE = "PutFeed";

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.Failed, "TaskContext is null");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        TaskContext context = params[0];
        long pid = (Long) context.get(KEY_PID);
        String token = context.getString(KEY_TOKEN);
        String content = context.getString(KEY_CONTENT);
        TaskResult result = new TaskResult(TaskStatus.Finished);
        long feedId = -1;
        try {
            feedId = PbarService.getInstance().putFeed(token, pid, content);
        } catch (Exception e) {
            result.setStatus(TaskStatus.Failed);
            result.setMessage("PbarService error");
            result.setContext(context);
            return result;
        }
        if (feedId <= 0) {
            result.setStatus(TaskStatus.Failed);
            result.setMessage("Invalid feed id");
            result.setContext(context);
            return result;
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        context.set(KEY_FID, feedId);
        result.setContext(context);
        return result;
    }

}
