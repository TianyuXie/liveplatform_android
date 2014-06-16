package com.pplive.liveplatform.core.task.home;

import com.pplive.liveplatform.core.service.live.SearchService;
import com.pplive.liveplatform.core.service.live.model.FallList;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class SearchTask extends Task {
    static final String TAG = "_SearchTask";

    public final static String KEY_RESULT = "search_task_result";
    public final static String KEY_TYPE = "search_task_type";

    public final static String KEY_SUBJECT_ID = "subjectId";
    public final static String KEY_SORT = "sort";
    public final static String KEY_LIVE_STATUS = "liveStatus";
    public final static String KEY_NEXT_TK = "nextTk";
    public final static String KEY_FALL_COUNT = "fallCount";
    public final static String KEY_KEYWORD = "key";

    private final String ID = StringUtil.newGuid();
    public final static String TYPE = "SearchTask";

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
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        TaskContext context = params[0];
        int subjectId = (Integer) context.get(KEY_SUBJECT_ID);
        SearchService.SortKeyword sort = (SearchService.SortKeyword) context.get(KEY_SORT);
        SearchService.LiveStatusKeyword liveStatus = (SearchService.LiveStatusKeyword) context.get(KEY_LIVE_STATUS);
        String nextTk = context.getString(KEY_NEXT_TK);
        int fallCount = (Integer) context.get(KEY_FALL_COUNT);
        String key = context.getString(KEY_KEYWORD);
        FallList<Program> data = null;
        try {
            data = SearchService.getInstance().searchProgram(key, subjectId, sort, liveStatus, nextTk, fallCount);
        } catch (Exception e) {
            return new TaskResult(TaskStatus.FAILED, "SearchService Error");
        }
        if (data == null) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(KEY_RESULT, data);
        result.setContext(context);
        return result;
    }
}
