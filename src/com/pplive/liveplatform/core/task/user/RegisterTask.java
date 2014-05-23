package com.pplive.liveplatform.core.task.user;

import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.passport.PassportService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class RegisterTask extends Task {
    final static String TAG = "_RegisterTask";
    public final static String KEY_CHECKCODE = "checkcode";
    public final static String KEY_PASSWORD = "password";
    public final static String KEY_GUID = "guid";

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
        String username = context.getString(KEY_USERNAME);
        String password = context.getString(KEY_PASSWORD);
        String checkcode = context.getString(KEY_CHECKCODE);
        String guid = context.getString(KEY_GUID);
        boolean status = false;
        try {
            status = PassportService.getInstance().registerByUsernameSimple(username, password, "", checkcode, guid);
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
