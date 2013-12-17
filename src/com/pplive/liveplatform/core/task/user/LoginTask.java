package com.pplive.liveplatform.core.task.user;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.core.service.live.UserService;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.service.passport.PassportService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class LoginTask extends Task {
    final static String TAG = "_LoginTask";
    public final static String KEY_USERINFO = "userinfo";
    public final static String KEY_PASSWORD = "password";

    private final String ID = StringUtil.newGuid();
    public final static String TYPE = "Login";

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
        String usr = context.getString(KEY_USERNAME);
        String pwd = context.getString(KEY_PASSWORD);
        String token = null;
        try {
            token = PassportService.getInstance().login(usr, pwd).getToken();
        } catch (Exception e) {
            return new TaskResult(TaskStatus.Failed, "PassportService error");
        }
        if (TextUtils.isEmpty(token)) {
            return new TaskResult(TaskStatus.Failed, "Invalid token");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        Log.d(TAG, "PassportService OK");
        User userinfo = new User();
        try {
            userinfo = UserService.getInstance().getUserInfo(token, usr);
        } catch (Exception e) {
            Log.w(TAG, "No userinfo");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        context.set(KEY_TOKEN, token);
        context.set(KEY_USERINFO, userinfo);
        result.setContext(context);
        return result;
    }

}
