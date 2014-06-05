package com.pplive.liveplatform.core.task.user;

import android.text.TextUtils;

import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.passport.PassportService;
import com.pplive.liveplatform.core.service.passport.model.CheckCode;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class CheckCodeTask extends Task {
    final static String TAG = CheckCodeTask.class.getSimpleName();

    public final static String TYPE = "CheckCode";

    public static final String KEY_PHONE_NUMBER = "PhoneNumber";

    private final String ID = StringUtil.newGuid();

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
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }

        TaskContext context = params[0];

        String phoneNumber = context.getString(KEY_PHONE_NUMBER);

        try {
            PassportService.getInstance().sendPhoneCheckCode(phoneNumber);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.Failed, StringUtil.safeString(e.getMessage()));
        }

        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }

        TaskResult result = new TaskResult(TaskStatus.Finished);

        result.setContext(context);

        return result;
    }

}
