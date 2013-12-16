package com.pplive.liveplatform.core.task.player;

import android.util.Log;

import com.pplive.liveplatform.core.service.comment.PbarService;
import com.pplive.liveplatform.core.service.comment.model.FeedDetailList;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class GetFeedTask extends Task {
    static final String TAG = "_GetFeedTask";

    public final static String KEY_RESULT = "get_feed_result";

    private final String ID = StringUtil.newGuid();
    private final String NAME = "GetFeed";
    
    public final static int DELAY_TIME_SHORT = 5000;
    
    public final static int DELAY_TIME_LONG = 30000;

    public final static int DEFAULT_TIMEOUT = 10000;

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
        String token = context.getString(KEY_TOKEN);
        FeedDetailList data = null;
        try {
            data = PbarService.getInstance().getFeeds(token, pid);
        } catch (Exception e) {
            return new TaskResult(TaskStatus.Failed, "get error");
        }
        if (data == null) {
            Log.d(TAG, "data == null");
            return new TaskResult(TaskStatus.Failed, "No data");
        }
        Log.d(TAG, data.getSize() + "");
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Canceled");
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        context.set(KEY_RESULT, data);
        result.setContext(context);
        return result;
    }

}
