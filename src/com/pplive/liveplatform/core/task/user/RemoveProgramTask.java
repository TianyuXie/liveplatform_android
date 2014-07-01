package com.pplive.liveplatform.core.task.user;

import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.ProgramAPI;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class RemoveProgramTask extends Task {
    static final String TAG = "_RemoveProgramTask";

    public final static String TYPE = "RemoveProgramTask";

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }
        TaskContext context = params[0];
        long pid = (Long) context.get(KEY_PID);
        String token = context.getString(KEY_TOKEN);
        boolean response = false;
        try {
            response = ProgramAPI.getInstance().deleteProgramById(token, pid);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.FAILED, "ProgramService error");
        }
        if (response == false) {
            return new TaskResult(TaskStatus.FAILED, "Fail to delete");
        }
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        result.setContext(context);
        return result;
    }
}
