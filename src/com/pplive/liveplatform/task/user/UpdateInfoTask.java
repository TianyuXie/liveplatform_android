package com.pplive.liveplatform.task.user;

import android.util.Log;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.UserAPI;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringManager;
import com.pplive.liveplatform.util.StringUtil;

public class UpdateInfoTask extends Task {
    final static String TAG = "_UpdateInfoTask";

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
        String nickname = context.getString(Extra.KEY_NICKNAME);
        String iconurl = context.getString(Extra.KEY_ICON_URL);

        //Get current info
        User userinfo = null;
        try {
            userinfo = UserAPI.getInstance().getUserInfo(token, username);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.FAILED, "UserService: Get current info error");
        }
        if (userinfo == null) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }
        Log.d(TAG, "UserService OK!");
        if (isCancelled()) {
            return new TaskResult(TaskStatus.CHANCEL, "Cancelled");
        }

        boolean changeNick = !StringUtil.isNullOrEmpty(nickname) && !nickname.equals(userinfo.getDisplayName());
        boolean changeIcon = !StringUtil.isNullOrEmpty(iconurl) && !iconurl.equals(userinfo.getIcon());
        if (changeNick) {
            Log.d(TAG, "setNickname");
            userinfo.setNickname(nickname);
        }
        if (changeIcon) {
            Log.d(TAG, "setIcon");
            userinfo.setIcon(iconurl);
        }
        if (changeIcon || changeNick) {
            boolean status = false;
            try {
                status = UserAPI.getInstance().updateOrCreateUser(token, userinfo);
            } catch (LiveHttpException e) {
                String message = null;
                if (e.getMessage().equals(StringManager.getRes(R.string.error_nickname_duplicated))) {
                    message = StringManager.getRes(R.string.toast_nickname_duplicated);
                }
                return new TaskResult(TaskStatus.FAILED, message);
            }
            if (!status) {
                return new TaskResult(TaskStatus.FAILED, "fail to update");
            }
        }
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(Extra.KEY_USER_INFO, userinfo);
        result.setContext(context);
        return result;
    }

}
