package com.pplive.liveplatform.task.user;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.ProgramAPI;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

public class RemoveProgramTask extends Task {
    static final String TAG = "_RemoveProgramTask";

    public final static String TYPE = "RemoveProgramTask";

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }
        TaskContext context = params[0];
        long pid = (Long) context.get(Extra.KEY_PROGRAM_ID);
        String token = context.getString(Extra.KEY_TOKEN);
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
