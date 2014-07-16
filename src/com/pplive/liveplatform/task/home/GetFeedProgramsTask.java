package com.pplive.liveplatform.task.home;

import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

public class GetFeedProgramsTask extends Task {

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (null == params || 0 == params.length) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        return null;
    }

}
