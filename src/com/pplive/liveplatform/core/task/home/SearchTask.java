package com.pplive.liveplatform.core.task.home;

import java.util.List;

import com.pplive.liveplatform.core.rest.model.Program;
import com.pplive.liveplatform.core.rest.service.SearchService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class SearchTask extends Task {
    public final static String KEY_RESULT = "get_task_result";
    public final static String KEY_SUBJECT_ID = "subjectId";
    public final static String KEY_SORT = "sort";
    public final static String KEY_LIVE_STATUS = "liveStatus";
    public final static String KEY_NEXT_TK = "nextTk";
    public final static String KEY_FALL_COUNT = "fallCount";

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
        int subjectId = (Integer) context.get(KEY_SUBJECT_ID);
        String sort = context.getString(KEY_SORT);
        String liveStatus = (String) context.getString(KEY_LIVE_STATUS);
        String nextTk = (String) context.getString(KEY_NEXT_TK);
        int fallCount = (Integer) context.get(KEY_FALL_COUNT);
        List<Program> data = null;
        try {
            data = SearchService.getInstance().searchProgram(subjectId, sort, liveStatus, nextTk, fallCount);
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
