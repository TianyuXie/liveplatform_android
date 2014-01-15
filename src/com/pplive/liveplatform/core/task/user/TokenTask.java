package com.pplive.liveplatform.core.task.user;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.ProgramService;
import com.pplive.liveplatform.core.service.passport.PassportService;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class TokenTask extends Task {
    final static String TAG = "_TokenTask";

    public final static String KEY_THIRDPARTY = "thirdparty";
    public final static String KEY_PASSWORD = "password";
    public final static String KEY_NEED_UPDATE = "need_update";

    private final static int ERR_PWD_ERROR = 3;
    private final static int ERR_PWD_EMPTY = 8;

    private final String ID = StringUtil.newGuid();
    public final static String TYPE = "Token";

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
        TaskContext context = params[0];
        String username = context.getString(KEY_USERNAME);
        String pwd = context.getString(KEY_PASSWORD);
        String token = context.getString(KEY_TOKEN);
        boolean needUpdate = (Boolean) context.get(KEY_NEED_UPDATE, true);
        boolean isThirdParty = (Boolean) context.get(KEY_THIRDPARTY, false);
        boolean mustUpdate = false;
        try {
            // check the token
            if (!TextUtils.isEmpty(token)) {
                ProgramService.getInstance().getProgramsByOwner(token, username);
            } else {
                mustUpdate = true;
            }
        } catch (LiveHttpException e) {
            if (e.getErrorCode() == ProgramService.ERR_UNAUTHORIZED) {
                // token expired
                if (isThirdParty) {
                    // must relogin manually
                    return new TaskResult(TaskStatus.Failed, "thirdparty");
                } else {
                    mustUpdate = true;
                }
            } else {
                // will retry automatically
                return new TaskResult(TaskStatus.Cancel, "programService error");
            }
        }
        if (mustUpdate) {
            Log.d(TAG, "mustUpdate...");
            token = null;
            try {
                token = PassportService.getInstance().login(username, pwd).getToken();
            } catch (LiveHttpException e) {
                // must relogin manually
                return new TaskResult(TaskStatus.Failed, "passportService failed");
            }
            if (TextUtils.isEmpty(token)) {
                // must relogin manually
                return new TaskResult(TaskStatus.Failed, "token == null");
            } else {
                // token updated
                context.set(KEY_NEED_UPDATE, false);
            }
        } else if (needUpdate) {
            Log.d(TAG, "needUpdate...");
            if (!isThirdParty) {
                String newToken = null;
                try {
                    newToken = PassportService.getInstance().login(username, pwd).getToken();
                } catch (LiveHttpException e) {
                    Log.w(TAG, e.getErrorCode() + "|" + e.getMessage());
                    switch (e.getErrorCode()) {
                    case ERR_PWD_EMPTY:
                    case ERR_PWD_ERROR:
                        // Password is changed, must relogin manually
                        return new TaskResult(TaskStatus.Failed, "password changed");
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
                return new TaskResult(TaskStatus.Failed, "thirdparty");
            }
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        context.set(KEY_TOKEN, token);
        result.setContext(context);
        return result;
    }
}
