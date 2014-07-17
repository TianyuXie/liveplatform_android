package com.pplive.liveplatform.task.search;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.live.SearchAPI;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

public class SearchProgramTask extends Task {
    static final String TAG = SearchProgramTask.class.getSimpleName();

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        TaskContext context = params[0];
        SearchAPI.SortKeyword sort = (SearchAPI.SortKeyword) context.get(Extra.KEY_SORT);
        SearchAPI.LiveStatusKeyword liveStatus = (SearchAPI.LiveStatusKeyword) context.get(Extra.KEY_LIVE_STATUS);

        String keyword = context.getString(Extra.KEY_KEYWORD);
        String tag = context.getString(Extra.KEY_TAG);

        int subjectId = (Integer) context.get(Extra.KEY_SUBJECT_ID);

        String nextTk = context.getString(Extra.KEY_NEXT_TOKEN);
        int fallCount = (Integer) context.get(Extra.KEY_FALL_COUNT);

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
        context.set(Extra.KEY_SEARCH_RESULT, data);
        result.setContext(context);
        return result;
    }
}
