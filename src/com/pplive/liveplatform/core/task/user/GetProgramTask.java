package com.pplive.liveplatform.core.task.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.text.TextUtils;

import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.ProgramService;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class GetProgramTask extends Task {
    static final String TAG = "_GetProgramTask";

    public final static String KEY_RESULT = "program_result";
    public final static String KEY_TYPE = "search_task_type";

    private final String ID = StringUtil.newGuid();
    public final static String TYPE = "GetProgramTask";

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
            return new TaskResult(TaskStatus.Failed, "TaskContext is null");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        TaskContext context = params[0];
        String username = context.getString(KEY_USERNAME);
        String token = context.getString(KEY_TOKEN);
        List<Program> data = null;
        try {
            if (TextUtils.isEmpty(token)) {
                data = ProgramService.getInstance().getProgramsByUser(username);
            } else {
                data = ProgramService.getInstance().getProgramsByOwner(token, username);
            }
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.Failed, "ProgramService error");
        }
        if (data == null) {
            return new TaskResult(TaskStatus.Failed, "No data");
        }
        Collection<Program> removePrograms = new ArrayList<Program>();
        if (TextUtils.isEmpty(token)) {
            // User
            for (Program program : data) {
                if (program.isDeleted() || program.isExpiredPrelive() || program.isPrelive()) {
                    removePrograms.add(program);
                }
            }
        } else {
            // Owner
            for (Program program : data) {
                if (program.isDeleted()) {
                    removePrograms.add(program);
                }
            }
        }
        data.removeAll(removePrograms);
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        context.set(KEY_RESULT, data);
        result.setContext(context);
        return result;
    }

}
