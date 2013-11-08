package com.pplive.liveplatform.core.task.home;

import java.io.IOException;

import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskStatus;
import com.pplive.liveplatform.util.HttpUtil;
import com.pplive.liveplatform.util.StringUtil;

public class GetTask extends Task {

    public final static String KEY_REQUEST = "get_task_request";
    public final static String KEY_URL = "get_task_url";
    public final static String KEY_RESULT = "get_task_result";

    private final String ID = StringUtil.newGuid();
    private final String NAME = "GetTask";

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void cancel() {
        isCancel = true;
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

        TaskContext context = params[0];

        String data = null;
        try {
            data = HttpUtil.getFromUrl((String) context.get(KEY_URL), "application/javascript");
        } catch (IOException e) {
            return new TaskResult(TaskStatus.Failed, "GET Error");
        }
        if (data == null) {
            return new TaskResult(TaskStatus.Failed, "No data");
        }

        TaskResult result = new TaskResult(TaskStatus.Finished);
        context.set(KEY_RESULT, data);
        result.setContext(context);

        if (isCancel) {
            result.setStatus(TaskStatus.Cancel);
            result.setMessage("Task is canceled");
        }
        return result;
    }
}
