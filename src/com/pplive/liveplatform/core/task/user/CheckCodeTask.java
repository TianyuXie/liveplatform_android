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
    final static String TAG = "_CheckCodeTask";

    public final static String KEY_GUID = "guid";
    public final static String KEY_IMAGE = "image";

    private final String ID = StringUtil.newGuid();
    public final static String TYPE = "CheckCode";

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
        CheckCode code = null;
        try {
            code = PassportService.getInstance().getCheckCode();
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.Failed, "PassportService error");
        }
        if (code == null) {
            return new TaskResult(TaskStatus.Failed, "No data");
        } else if (TextUtils.isEmpty(code.getGUID()) || TextUtils.isEmpty(code.getImageUrl())) {
            return new TaskResult(TaskStatus.Failed, "Invalid data");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        TaskContext context = new TaskContext();
        context.set(KEY_GUID, code.getGUID());
        context.set(KEY_IMAGE, code.getImageUrl());
        result.setContext(context);
        return result;
    }

}
