package com.pplive.liveplatform.task.home;

import java.util.List;

import android.content.Context;

import com.pplive.android.pulltorefresh.FallListHelper;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskFailedEvent;
import com.pplive.liveplatform.task.TaskSucceedEvent;
import com.pplive.liveplatform.task.Task.TaskListener;

public class GetRecommendProgramsHelper extends FallListHelper<Program> {

    private TaskListener mTaskListener = new Task.BaseTaskListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            List<Program> programs = (List<Program>) event.getContext().get(Extra.KEY_RESULT);

            mAdapter.refreshData(programs);

            onLoadSucceed();
        }

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            onLoadFailed();
        }
    };

    public GetRecommendProgramsHelper(Context context, RefreshAdapter<Program> adapter) {
        super(context, adapter);
    }

    @Override
    protected void onLoad(Task task, TaskContext context) {
        task.addTaskListener(mTaskListener);
    }

    @Override
    protected Task createTask() {
        return new GetRecommendProgramsTask();
    }

}
