package com.pplive.liveplatform.task.home;

import java.util.List;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.SearchAPI;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

public class GetRecommendProgramsTask extends Task {

    @Override
    protected TaskResult doInBackground(TaskContext... params) {

        List<Program> programs = null;
        try {
            programs = SearchAPI.getInstance().recommendProgram();
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.FAILED, "RecommendService error");
        }

        if (programs == null) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        TaskContext context = new TaskContext();
        context.set(Extra.KEY_RESULT, programs);
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        result.setContext(context);

        return result;
    }

}
