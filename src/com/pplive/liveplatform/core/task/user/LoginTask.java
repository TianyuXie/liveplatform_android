package com.pplive.liveplatform.core.task.user;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.exception.LiveHttpException;
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

        //Delay
        if (mDelay != 0) {
            if (isCancelled()) {
                return new TaskResult(TaskStatus.Cancel, "Cancelled");
            }
            try {
                Log.d(TAG, "start sleep");
                Thread.sleep(mDelay);
            } catch (InterruptedException e1) {
            }
        }

        Log.d(TAG, "leave sleep");
        //Start PassportService
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        TaskContext context = params[0];
        String usr = context.getString(KEY_USERNAME);
        String pwd = context.getString(KEY_PASSWORD);
        String token = null;
        try {
            token = PassportService.getInstance().login(usr, pwd).getToken();
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.Failed, StringUtil.safeString(e.getMessage()));
        }
        if (TextUtils.isEmpty(token)) {
            return new TaskResult(TaskStatus.Failed, StringUtil.getRes(R.string.register_token_fail));
        }
        Log.d(TAG, "PassportService OK");

        //Start UserService
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        User userinfo = null;
        try {
            userinfo = UserService.getInstance().getUserInfo(token, usr);
        } catch (Exception e) {
            Log.w(TAG, "UserService error");
        }

        //Build TaskResult
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        context.set(KEY_TOKEN, token);
        if (userinfo != null) {
            Log.d(TAG, "UserService OK");
            context.set(KEY_USERINFO, userinfo);
            context.set(KEY_USERNAME, userinfo.getUsername());
        } else {
            Log.w(TAG, "userinfo == null");
        }
        result.setContext(context);
        return result;
    }

}
