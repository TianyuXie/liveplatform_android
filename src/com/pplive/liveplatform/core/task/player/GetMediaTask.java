package com.pplive.liveplatform.core.task.player;

import com.pplive.liveplatform.core.service.live.MediaService;
import com.pplive.liveplatform.core.service.live.model.WatchList;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class GetMediaTask extends Task {
    static final String TAG = "_GetMediaTask";

    public final static String KEY_RESULT = "play_media_result";

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
        String username = (String) context.get(KEY_USERNAME);
        String token = (String) context.get(KEY_TOKEN);
        WatchList data = null;
        try {
            data = MediaService.getInstance().getPlayWatchListV3(token, pid, username);
        } catch (Exception e) {
            return new TaskResult(TaskStatus.FAILED, "MediaService error");
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
