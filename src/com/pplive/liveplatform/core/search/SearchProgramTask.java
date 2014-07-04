package com.pplive.liveplatform.core.search;

import com.pplive.liveplatform.core.api.live.SearchAPI;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class SearchProgramTask extends Task {
    static final String TAG = SearchProgramTask.class.getSimpleName();

    public final static String KEY_RESULT = "search_task_result";
    public final static String KEY_LOAD_MODE = "search_task_type";

    public final static String KEY_KEYWORD = "keyword";
    public final static String KEY_TAG = "tag";

    public final static String KEY_SUBJECT_ID = "subject_id";

    public final static String KEY_LIVE_STATUS = "live_status";
    public final static String KEY_SORT = "sort";

    public final static String KEY_NEXT_TOKEN = "next_token";
    public final static String KEY_FALL_COUNT = "fall_count";

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        TaskContext context = params[0];
        SearchAPI.SortKeyword sort = (SearchAPI.SortKeyword) context.get(KEY_SORT);
        SearchAPI.LiveStatusKeyword liveStatus = (SearchAPI.LiveStatusKeyword) context.get(KEY_LIVE_STATUS);

        String keyword = context.getString(KEY_KEYWORD);
        String tag = context.getString(KEY_TAG);

        int subjectId = (Integer) context.get(KEY_SUBJECT_ID);

        String nextTk = context.getString(KEY_NEXT_TOKEN);
        int fallCount = (Integer) context.get(KEY_FALL_COUNT);

        FallList<Program> data = null;
        try {
            data = SearchAPI.getInstance().searchProgram(keyword, tag, subjectId, sort, liveStatus, nextTk, fallCount);
        } catch (Exception e) {
            return new TaskResult(TaskStatus.FAILED, "SearchService Error");
        }
        if (data == null) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(KEY_RESULT, data);
        result.setContext(context);
        return result;
    }

}
