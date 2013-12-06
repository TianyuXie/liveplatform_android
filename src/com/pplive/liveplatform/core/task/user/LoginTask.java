package com.pplive.liveplatform.core.task.user;

import android.text.TextUtils;

import com.pplive.liveplatform.core.service.passport.PassportService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class LoginTask extends Task {
    final static String TAG = "_LoginTask";
    public final static String KEY_TOKEN = "token";
    public final static String KEY_PWD = "password";
    public final static String KEY_USR = "username";

    private final String ID = StringUtil.newGuid();
    private final String NAME = "Login";

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
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
            return new TaskResult(TaskStatus.Cancel, "Canceled");
        }
        TaskContext context = params[0];
        String usr = context.getString(KEY_USR);
        String pwd = context.getString(KEY_PWD);
        String token = null;
        try {
            token = PassportService.getInstance().login(usr, pwd);
        } catch (Exception e) {
            return new TaskResult(TaskStatus.Failed, "GET Error");
        }
        if (TextUtils.isEmpty(token)) {
            return new TaskResult(TaskStatus.Failed, "No data");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Canceled");
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        context.set(KEY_TOKEN, token);
        result.setContext(context);
        return result;
    }

}