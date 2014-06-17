package com.pplive.liveplatform.core.task.user;

import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.passport.PassportService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class RegisterTask extends Task {

    final static String TAG = RegisterTask.class.getSimpleName();

    public final static String KEY_PHONE_NUMBER = "phone_number";
    public final static String KEY_PASSWORD = "password";
    public final static String KEY_CHECK_CODE = "checkcode";

    public final static String TYPE = "Register";

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        TaskContext context = params[0];

        String phoneNumber = context.getString(KEY_PHONE_NUMBER);
        String password = context.getString(KEY_PASSWORD);

        String checkCode = context.getString(KEY_CHECK_CODE);

        boolean status = false;
        try {
            status = PassportService.getInstance().registerByPhoneNumSimple(phoneNumber, password, checkCode);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.FAILED, e.getMessage());
        }
        if (!status) {
            return new TaskResult(TaskStatus.FAILED);
        }
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        result.setContext(context);
        return result;
    }

}
