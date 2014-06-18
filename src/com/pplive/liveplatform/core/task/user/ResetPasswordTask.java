package com.pplive.liveplatform.core.task.user;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.passport.PassportService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class ResetPasswordTask extends Task {

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }

        TaskContext context = params[0];

        String loginname = context.getString(Extra.KEY_LOGIN_NAME);
        String password = context.getString(Extra.KEY_PASSWORD);

        try {
            PassportService.getInstance().resetPassword(loginname, password);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.FAILED, StringUtil.safeString(e.getMessage()));
        }

        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }

        TaskResult result = new TaskResult(TaskStatus.SUCCEED);

        result.setContext(context);

        return result;
    }

}
