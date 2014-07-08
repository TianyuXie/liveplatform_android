package com.pplive.liveplatform.core.task.player;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.ProgramAPI;
import com.pplive.liveplatform.core.api.live.model.LiveStatus;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class LiveStatusTask extends Task {
    static final String TAG = "_LiveStatusTask";

    public final static String KEY_RESULT = "live_status_result";

    public final static String TYPE = "LiveStatus";

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
        LiveStatus data = null;
        try {
            data = ProgramAPI.getInstance().getLiveStatus(pid);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.FAILED, "ProgramService error");
        }
        if (data == null) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(KEY_RESULT, data);
        result.setContext(context);
        return result;
    }

}
