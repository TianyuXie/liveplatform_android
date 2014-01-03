package com.pplive.liveplatform.core.task.user;

import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.UserService;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;
import com.pplive.liveplatform.util.StringUtil;

public class UpdateInfoTask extends Task {
    final static String TAG = "_UpdateNickTask";
    public final static String KEY_USERINFO = "userinfo";
    public final static String KEY_NICKNAME = "nickname";
    public final static String KEY_ICON_URL = "iconurl";

    private final String ID = StringUtil.newGuid();
    public final static String TYPE = "UpdateUser";

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
        String nickname = context.getString(KEY_NICKNAME);
        String iconurl = context.getString(KEY_ICON_URL);

        //Get current info
        User userinfo = null;
        try {
            userinfo = UserService.getInstance().getUserInfo(token, username);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.Failed, "UserService: Get current info error");
        }
        if (userinfo == null) {
            return new TaskResult(TaskStatus.Failed, "No data");
        }
        if (isCancelled()) {
            return new TaskResult(TaskStatus.Cancel, "Cancelled");
        }

        boolean changeNick = !StringUtil.isNullOrEmpty(nickname) && !nickname.equals(userinfo.getNickname());
        boolean changeIcon = !StringUtil.isNullOrEmpty(iconurl) && !iconurl.equals(userinfo.getIcon());
        if (changeNick) {
            userinfo.setIcon(iconurl);
        }
        if (changeIcon) {
            userinfo.setNickname(nickname);
        }
        if (changeIcon || changeNick) {
            boolean status = false;
            try {
                status = UserService.getInstance().updateOrCreateUser(token, userinfo);
            } catch (LiveHttpException e) {
                return new TaskResult(TaskStatus.Failed, "UserService: Update info error");
            }
            if (!status) {
                return new TaskResult(TaskStatus.Failed, "fail to update");
            }
        }
        TaskResult result = new TaskResult(TaskStatus.Finished);
        context.set(KEY_USERINFO, userinfo);
        result.setContext(context);
        return result;
    }

}
