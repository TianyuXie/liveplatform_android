package com.pplive.liveplatform.core.task.user;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.passport.PassportService;
import com.pplive.liveplatform.core.service.passport.PassportService.CheckCodeType;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class GetCheckCodeTask extends Task {

    static final String TAG = GetCheckCodeTask.class.getSimpleName();

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }

        TaskContext context = params[0];

        String phoneNumber = context.getString(Extra.KEY_PHONE_NUMBER);
        CheckCodeType type = (CheckCodeType) context.get(Extra.KEY_CODE_TYPE);

        try {
            PassportService.getInstance().sendPhoneCheckCode(phoneNumber, type);
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
