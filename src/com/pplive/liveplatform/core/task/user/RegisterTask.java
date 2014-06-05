package com.pplive.liveplatform.core.task.user;

import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.passport.PassportService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class RegisterTask extends Task {

    final static String TAG = RegisterTask.class.getSimpleName();

    public final static String KEY_PHONE_NUMBER = "phone_number";
    public final static String KEY_PASSWORD = "password";
    public final static String KEY_CHECK_CODE = "checkcode";

    private final String ID = StringUtil.newGuid();
    public final static String TYPE = "Register";

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.Failed, "TaskContext is null");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        TaskContext context = params[0];

        String phoneNumber = context.getString(KEY_PHONE_NUMBER);
        String password = context.getString(KEY_PASSWORD);

        String checkCode = context.getString(KEY_CHECK_CODE);

        boolean status = false;
        try {
            status = PassportService.getInstance().registerByPhoneNumSimple(phoneNumber, password, checkCode);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.Failed, e.getMessage());
        }
        if (!status) {
            return new TaskResult(TaskStatus.Failed);
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        result.setContext(context);
        return result;
    }

}
