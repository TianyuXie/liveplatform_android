package com.pplive.liveplatform.core.task.user;

import android.util.Log;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.FileUploadAPI;
import com.pplive.liveplatform.core.api.live.UserAPI;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class UploadIconTask extends Task {
    final static String TAG = "_UpdateIconTask";

    public final static String KEY_USERINFO = "userinfo";

    private User mUserInfo;
    private String mIconUrl;

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        TaskContext context = params[0];
        String username = context.getString(Extra.KEY_USERNAME);
        String token = context.getString(Extra.KEY_TOKEN);
        String icon = context.getString(Extra.KEY_ICON_PATH);
        GetUserInfoThread userThread = new GetUserInfoThread(username, token);
        userThread.start();
        UploadThread uploadThread = new UploadThread(username, token, icon);
        uploadThread.start();
        try {
            userThread.join();
            uploadThread.join();
        } catch (InterruptedException e) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        if (mUserInfo == null || mIconUrl == null) {
            return new TaskResult(TaskStatus.FAILED, "Get or upload failed");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }
        Log.d(TAG, mIconUrl);
        mUserInfo.setIcon(mIconUrl);
        boolean status = false;
        try {
            status = UserAPI.getInstance().updateOrCreateUser(token, mUserInfo);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.FAILED, "UserService: Update info error");
        }
        if (!status) {
            return new TaskResult(TaskStatus.FAILED, "fail to update");
        }
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
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
                mUserInfo = UserAPI.getInstance().getUserInfo(token, username);
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
                mIconUrl = FileUploadAPI.getInstance().uploadFile(token, username, icon);
            } catch (LiveHttpException e) {
            }
        }
    }

}
