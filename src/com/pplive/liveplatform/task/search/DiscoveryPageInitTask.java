package com.pplive.liveplatform.task.search;

import java.util.List;

import android.util.Log;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.live.ProgramAPI;
import com.pplive.liveplatform.core.api.live.SearchAPI;
import com.pplive.liveplatform.core.api.live.model.Subject;
import com.pplive.liveplatform.core.api.live.model.Tag;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

public class DiscoveryPageInitTask extends Task {

    static final String TAG = DiscoveryPageInitTask.class.getSimpleName();

    @Override
    protected TaskResult doInBackground(TaskContext... params) {

        List<Subject> subjects = null;

        try {
            subjects = ProgramAPI.getInstance().getSubjects();
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        List<Tag> tags = null;
        try {
            tags = SearchAPI.getInstance().recommendTag();
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null == subjects || null == tags) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        TaskContext context = new TaskContext();
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);

        context.set(Extra.KEY_RESULT_SUBJECTS, subjects);
        context.set(Extra.KEY_RESULT_TAGS, tags);
        result.setContext(context);
        return result;
    }
}
