package com.pplive.liveplatform.task.program;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.live.MediaAPI;
import com.pplive.liveplatform.core.api.live.model.WatchList;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

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
        long pid = (Long) context.get(Extra.KEY_PROGRAM_ID);
        String username = (String) context.get(Extra.KEY_USERNAME);
        String token = (String) context.get(Extra.KEY_TOKEN);
        WatchList data = null;
        try {
            data = MediaAPI.getInstance().getPlayWatchListV3(token, pid, username);
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
