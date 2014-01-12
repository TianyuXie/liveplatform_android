package com.pplive.liveplatform.core.task.user;

import android.text.TextUtils;

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
        boolean isThirdParty = (Boolean) context.get(KEY_THIRDPARTY);
        boolean retry = false;
        try {
            if (!TextUtils.isEmpty(token)) {
                ProgramService.getInstance().getProgramsByOwner(token, username);
            } else {
                retry = true;
            }
        } catch (LiveHttpException e) {
            if (e.getErrorCode() == ProgramService.ERR_UNAUTHORIZED) {
                if (isThirdParty) {
                    return new TaskResult(TaskStatus.Failed, "isThirdparty");
                } else {
                    retry = true;
                }
            } else {
                return new TaskResult(TaskStatus.Cancel, "ProgramService error");
            }
        }
        if (retry) {
            token = null;
            try {
                token = PassportService.getInstance().login(username, pwd).getToken();
            } catch (LiveHttpException e) {
                return new TaskResult(TaskStatus.Failed, "passport failed");
            }
            if (TextUtils.isEmpty(token)) {
                return new TaskResult(TaskStatus.Failed, "token == null");
            }
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        result.setContext(context);
        return result;
    }

}
