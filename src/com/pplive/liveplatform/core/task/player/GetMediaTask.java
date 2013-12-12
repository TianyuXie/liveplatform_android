package com.pplive.liveplatform.core.task.player;

import java.util.List;

import com.pplive.liveplatform.core.service.live.MediaService;
import com.pplive.liveplatform.core.service.live.model.Watch;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class GetMediaTask extends Task {
    public final static String KEY_RESULT = "play_media_result";

    private final String ID = StringUtil.newGuid();
    private final String NAME = "GetMedia";

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
        String username = (String) context.get(KEY_USERNAME);
        String token = (String) context.get(KEY_TOKEN);
        List<Watch> data = null;
        try {
            data = MediaService.getInstance().getPlayWatchList(token, pid, username);
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
