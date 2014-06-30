package com.pplive.liveplatform.core.task.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.text.TextUtils;

import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.ProgramService;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class GetProgramTask extends Task {
    static final String TAG = GetProgramTask.class.getSimpleName();

    public final static String KEY_RESULT = "program_result";
    public final static String KEY_TYPE = "search_task_type";

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
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
            return new TaskResult(TaskStatus.FAILED, "ProgramService error");
        }

        if (data == null) {
            return new TaskResult(TaskStatus.FAILED, "No data");
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
                if (program.isDeleted() || program.isPrelive()) {
                    removePrograms.add(program);
                }
            }
        }

        data.removeAll(removePrograms);

        Collections.sort(data, new Comparator<Program>() {

            @Override
            public int compare(Program lhs, Program rhs) {

                return (int) (rhs.getStartTime() - lhs.getStartTime());
            }
        });

        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }

        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(KEY_RESULT, data);
        result.setContext(context);
        return result;
    }

}
