package com.pplive.liveplatform.task.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.FollowAPI;
import com.pplive.liveplatform.core.api.live.ProgramAPI;
import com.pplive.liveplatform.core.api.live.UserAPI;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.api.live.model.UserFriendCount;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

public class GetUserDetailInfoTask extends Task {

    static final String TAG = GetUserDetailInfoTask.class.getSimpleName();

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        TaskContext context = params[0];
        String username = context.getString(Extra.KEY_USERNAME);
        String coToken = context.getString(Extra.KEY_TOKEN);
        List<Program> data = null;
        try {
            if (TextUtils.isEmpty(coToken)) {
                data = ProgramAPI.getInstance().getProgramsByUser(username);
            } else {
                data = ProgramAPI.getInstance().getProgramsByOwner(coToken, username);
            }
        } catch (LiveHttpException e) {
            Log.w(TAG, e.toString());
            return new TaskResult(TaskStatus.FAILED, "ProgramService error");
        }

        if (null == data) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        List<Program> removePrograms = new ArrayList<Program>();
        if (TextUtils.isEmpty(coToken)) {
            // User
            for (Program program : data) {
                if (program.isDeleted() || program.isExpiredPrelive() || program.isPrelive()) {
                    removePrograms.add(program);
                }
            }
        } else {
            // Owner
            for (Program program : data) {
                if (program.isDeleted() || program.isPrelive()) {
                    removePrograms.add(program);
                }
            }
        }

        data.removeAll(removePrograms);

        Collections.sort(data, new Comparator<Program>() {

            @Override
            public int compare(Program lhs, Program rhs) {

                return (int) (rhs.getStartTime() - lhs.getStartTime());
            }
        });

        context.set(Extra.KEY_USER_PROGRAMS, data);

        User user = null;
        try {
            user = UserAPI.getInstance().getUserInfo(coToken, username, TextUtils.isEmpty(coToken));
        } catch (LiveHttpException e) {
            Log.w(TAG, e.toString());
            return new TaskResult(TaskStatus.FAILED, "UserService error");
        }

        if (null == user) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        context.set(Extra.KEY_USER_INFO, user);

        UserFriendCount friendCount = null;
        try {
            boolean cdn = TextUtils.isEmpty(username) || TextUtils.isEmpty(coToken);
            friendCount = FollowAPI.getInstance().getUserFriendCount(cdn, username);
        } catch (LiveHttpException e) {
            Log.w(TAG, e.toString());
            return new TaskResult(TaskStatus.FAILED, "FollowService error");
        }

        if (null == friendCount) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        context.set(Extra.KEY_USER_FRIEND_COUNT, friendCount);

        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        result.setContext(context);
        return result;
    }

}
