package com.pplive.liveplatform.core.task.player;

import com.pplive.liveplatform.core.service.comment.PbarService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class PutFeedTask extends Task {
    static final String TAG = "_PutFeedTask";

    public final static String KEY_RESULT = "put_feed_result";
    public final static String KEY_CONTENT = "content";

    private final String ID = StringUtil.newGuid();
    private final String NAME = "PutFeed";

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
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
            return new TaskResult(TaskStatus.Cancel, "Canceled");
        }
        TaskContext context = params[0];
        long pid = (Long) context.get(KEY_PID);
        String token = (String) context.get(KEY_TOKEN);
        String content = (String) context.get(KEY_CONTENT);
        try {
            PbarService.getInstance().putFeed(token, pid, content);
        } catch (Exception e) {
            return new TaskResult(TaskStatus.Failed, "get error");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Canceled");
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        result.setContext(context);
        return result;
    }

}
