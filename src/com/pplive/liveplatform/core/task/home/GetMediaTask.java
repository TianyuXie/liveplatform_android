package com.pplive.liveplatform.core.task.home;

import java.util.List;

import com.pplive.liveplatform.core.rest.model.Watch;
import com.pplive.liveplatform.core.rest.service.MediaService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class GetMediaTask extends Task {
    public final static String KEY_RESULT = "play_media_result";
    public final static String KEY_PID = "pid";
    public final static String KEY_USERNAME = "username";

    @Override
    public String getID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
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
        String username = (String) context.get(KEY_USERNAME);
        List<Watch> data = null;
        try {
            data = MediaService.getInstance().getPlayWatchList(pid, username);
        } catch (Exception e) {
            return new TaskResult(TaskStatus.Failed, "get error");
        }
        if (data == null) {
            return new TaskResult(TaskStatus.Failed, "No data");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Canceled");
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        context.set(KEY_RESULT, data);
        result.setContext(context);
        return result;
    }

}
