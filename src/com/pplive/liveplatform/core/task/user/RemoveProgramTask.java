package com.pplive.liveplatform.core.task.user;

import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.ProgramService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class RemoveProgramTask extends Task {
    static final String TAG = "_RemoveProgramTask";

    private final String ID = StringUtil.newGuid();
    public final static String TYPE = "RemoveProgramTask";

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
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }
        TaskContext context = params[0];
        long pid = (Long) context.get(KEY_PID);
        String token = context.getString(KEY_TOKEN);
        boolean response = false;
        try {
            response = ProgramService.getInstance().deleteProgramById(token, pid);
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
