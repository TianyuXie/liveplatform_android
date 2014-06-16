package com.pplive.liveplatform.core.task.other;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.ImageUtil;
import com.pplive.liveplatform.util.StringUtil;

public class LoadImageTask extends Task {
    static final String TAG = "_ImageTask";

    public final static String KEY_RESULT = "image_result";

    public final static String KEY_URL = "image_url";

    public final static String KEY_TARGET = "image_target";

    private final String ID = StringUtil.newGuid();
    public final static String TYPE = "image";

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
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        TaskContext context = params[0];
        String url = context.getString(KEY_URL);
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        if (TextUtils.isEmpty(url)) {
            result.setStatus(TaskStatus.FAILED);
            result.setMessage("url is empty");
            result.setContext(context);
            return result;
        }
        Bitmap bitmap = ImageUtil.getBitmapFromUrl(url, 120.0f, 90.0f);
        if (bitmap == null) {
            result.setStatus(TaskStatus.FAILED);
            result.setMessage("No data");
            result.setContext(context);
            return result;
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        context.set(KEY_RESULT, bitmap);
        result.setContext(context);
        return result;
    }

}
