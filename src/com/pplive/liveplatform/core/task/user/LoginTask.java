package com.pplive.liveplatform.core.task.user;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.UserAPI;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.api.passport.PassportAPI;
import com.pplive.liveplatform.core.api.passport.model.LoginResult;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringManager;
import com.pplive.liveplatform.util.StringUtil;

public class LoginTask extends Task {

    final static String TAG = LoginTask.class.getSimpleName();

    public final static String TYPE = "Login";

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        //Delay
        if (mDelay != 0) {
            if (isCancelled()) {
                return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
            }
            try {
                Log.d(TAG, "start sleep");
                Thread.sleep(mDelay);
            } catch (InterruptedException e1) {
            }
        }
        Log.d(TAG, "wake up");

        //Start PassportService
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        TaskContext context = params[0];
        String usr = context.getString(Extra.KEY_USERNAME);
        String pwd = context.getString(Extra.KEY_PASSWORD);
        String token = null;
        LoginResult loginResult = null;
        try {
            loginResult = PassportAPI.getInstance().login(usr, pwd);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.FAILED, StringUtil.safeString(e.getMessage()));
        }
        if (loginResult == null) {
            return new TaskResult(TaskStatus.FAILED, StringManager.getRes(R.string.register_token_fail));
        } else {
            usr = loginResult.getUsername();
            token = loginResult.getToken();
        }
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(usr)) {
            return new TaskResult(TaskStatus.FAILED, StringManager.getRes(R.string.register_token_fail));
        }
        Log.d(TAG, "PassportService OK");

        //Start UserService
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        User userinfo = null;
        try {
            userinfo = UserAPI.getInstance().getUserInfo(token, usr);
        } catch (Exception e) {
            Log.w(TAG, "UserService error");
        }

        //Build TaskResult
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }

        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(Extra.KEY_USERNAME, usr);
        context.set(Extra.KEY_TOKEN, token);
        if (userinfo != null) {
            Log.d(TAG, "UserService OK");
            context.set(Extra.KEY_USERINFO, userinfo);
        } else {
            Log.w(TAG, "userinfo == null");
        }
        result.setContext(context);
        return result;
    }

}
