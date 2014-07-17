package com.pplive.liveplatform.task.passport;

import org.springframework.http.HttpStatus;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.ProgramAPI;
import com.pplive.liveplatform.core.api.passport.PassportAPI;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

public class TokenTask extends Task {
    final static String TAG = "_TokenTask";

    public final static String KEY_THIRDPARTY = "thirdparty";
    public final static String KEY_PASSWORD = "password";
    public final static String KEY_NEED_UPDATE = "need_update";

    private final static int ERR_PWD_ERROR = 3;
    private final static int ERR_PWD_EMPTY = 8;

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }
        TaskContext context = params[0];
        String username = context.getString(Extra.KEY_USERNAME);
        String pwd = context.getString(Extra.KEY_PASSWORD);
        String token = context.getString(Extra.KEY_TOKEN);
        boolean needUpdate = (Boolean) context.get(KEY_NEED_UPDATE, true);
        boolean isThirdParty = (Boolean) context.get(KEY_THIRDPARTY, false);
        boolean mustUpdate = false;
        try {
            // check the token
            if (!TextUtils.isEmpty(token)) {
                ProgramAPI.getInstance().getProgramsByOwner(token, username);
            } else {
                mustUpdate = true;
            }
        } catch (LiveHttpException e) {
            if (HttpStatus.UNAUTHORIZED.value() == e.getErrorCode()) {
                // token expired
                if (isThirdParty) {
                    // must relogin manually
                    return new TaskResult(TaskStatus.FAILED, "thirdparty");
                } else {
                    mustUpdate = true;
                }
            } else {
                // will retry automatically
                return new TaskResult(TaskStatus.CHANCEL, "programService error");
            }
        }
        if (mustUpdate) {
            Log.d(TAG, "mustUpdate...");
            token = null;
            try {
                token = PassportAPI.getInstance().login(username, pwd).getToken();
            } catch (LiveHttpException e) {
                // must relogin manually
                return new TaskResult(TaskStatus.FAILED, "passportService failed");
            }
            if (TextUtils.isEmpty(token)) {
                // must relogin manually
                return new TaskResult(TaskStatus.FAILED, "token == null");
            } else {
                // token updated
                context.set(KEY_NEED_UPDATE, false);
            }
        } else if (needUpdate) {
            Log.d(TAG, "needUpdate...");
            if (!isThirdParty) {
                String newToken = null;
                try {
                    newToken = PassportAPI.getInstance().login(username, pwd).getToken();
                } catch (LiveHttpException e) {
                    Log.w(TAG, e.getErrorCode() + "|" + e.getMessage());
                    switch (e.getErrorCode()) {
                    case ERR_PWD_EMPTY:
                    case ERR_PWD_ERROR:
                        // Password is changed, must relogin manually
                        return new TaskResult(TaskStatus.FAILED, "password changed");
                    }
                }
                if (!TextUtils.isEmpty(newToken)) {
                    // token updated
                    token = newToken;
                    context.set(KEY_NEED_UPDATE, false);
                } else {
                    // will retry automatically
                    context.set(KEY_NEED_UPDATE, true);
                }
            } else {
                // must relogin manually
                return new TaskResult(TaskStatus.FAILED, "thirdparty");
            }
        }
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(Extra.KEY_TOKEN, token);
        result.setContext(context);
        return result;
    }
}
