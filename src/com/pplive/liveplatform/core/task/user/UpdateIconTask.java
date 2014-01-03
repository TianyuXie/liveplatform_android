package com.pplive.liveplatform.core.task.user;

import android.util.Log;

import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.FileUploadService;
import com.pplive.liveplatform.core.service.live.UserService;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class UpdateIconTask extends Task {
    final static String TAG = "_UpdateIconTask";
    public final static String KEY_USERINFO = "userinfo";
    public final static String KEY_ICON = "icon";

    private final String ID = StringUtil.newGuid();
    public final static String TYPE = "UpdateIcon";

    private User mUserInfo;
    private String mIconUrl;

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
        String username = context.getString(KEY_USERNAME);
        String token = context.getString(KEY_TOKEN);
        String icon = context.getString(KEY_ICON);
        GetUserInfoThread userThread = new GetUserInfoThread(username, token);
        userThread.start();
        UploadThread uploadThread = new UploadThread(username, token, icon);
        uploadThread.start();
        try {
            userThread.join();
            uploadThread.join();
        } catch (InterruptedException e) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        if (mUserInfo == null || mIconUrl == null) {
            return new TaskResult(TaskStatus.Failed, "Get or upload failed");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }
        Log.d(TAG, mIconUrl);
        mUserInfo.setIcon(mIconUrl);
        boolean status = false;
        try {
            status = UserService.getInstance().updateOrCreateUser(token, mUserInfo);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.Failed, "UserService: Update info error");
        }
        if (!status) {
            return new TaskResult(TaskStatus.Failed, "fail to update");
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        context.set(KEY_USERINFO, mUserInfo);
        result.setContext(context);
        return result;
    }

    class GetUserInfoThread extends Thread {
        private String username;
        private String token;

        public GetUserInfoThread(String username, String token) {
            this.username = username;
            this.token = token;
        }

        @Override
        public void run() {
            try {
                mUserInfo = UserService.getInstance().getUserInfo(token, username);
            } catch (LiveHttpException e) {
            }
        }
    }

    class UploadThread extends Thread {
        private String username;
        private String token;
        private String icon;

        public UploadThread(String username, String token, String icon) {
            this.username = username;
            this.token = token;
            this.icon = icon;
        }

        @Override
        public void run() {
            try {
                mIconUrl = FileUploadService.getInstance().uploadFile(token, username, icon);
            } catch (LiveHttpException e) {
            }
        }
    }

}
