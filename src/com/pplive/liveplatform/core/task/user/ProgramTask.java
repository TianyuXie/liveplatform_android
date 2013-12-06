package com.pplive.liveplatform.core.task.user;

import java.util.List;

import android.util.Log;

import com.pplive.liveplatform.core.service.live.ProgramService;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class ProgramTask extends Task {
    static final String TAG = "_ProgramTask";

    public final static String KEY_RESULT = "program_result";
    public final static String KEY_USR = "username";

    private final String ID = StringUtil.newGuid();
    private final String NAME = "Program";

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
        String username = context.getString(KEY_USR);
        Log.d(TAG, username);
        List<Program> data = null;
        try {
            data = ProgramService.getInstance().getProgramsByOwner(username);
        } catch (Exception e) {
            return new TaskResult(TaskStatus.Failed, "GET Error");
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
