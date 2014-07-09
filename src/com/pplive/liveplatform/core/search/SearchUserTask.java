package com.pplive.liveplatform.core.search;

import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.FollowAPI;
import com.pplive.liveplatform.core.api.live.SearchAPI;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.api.live.model.UserRelation;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class SearchUserTask extends Task {

    static final String TAG = SearchUserTask.class.getSimpleName();

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (params == null || params.length <= 0) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        TaskContext context = params[0];
        String keyword = context.getString(Extra.KEY_KEYWORD);
        String nextToken = context.getString(Extra.KEY_NEXT_TOKEN);
        int fallCount = (Integer) context.get(Extra.KEY_FALL_COUNT);

        String username = context.getString(Extra.KEY_USERNAME);
        String cotoken = context.getString(Extra.KEY_TOKEN);

        FallList<User> data = null;
        try {
            data = SearchAPI.getInstance().searchUser(keyword, nextToken, fallCount);
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null == data) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(cotoken)) {
            updateUserRelation(cotoken, username, data.getList());
        }

        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(Extra.KEY_RESULT, data);
        result.setContext(context);
        return result;
    }

    private void updateUserRelation(String cotoken, String username, List<User> users) {
        List<UserRelation> relations = null;
        try {
            relations = FollowAPI.getInstance().getRelations(cotoken, username, users);
        } catch (LiveHttpException e) {
            Log.w(TAG, e.toString());
        }

        if (null != relations && null != users) {
            for (User user : users) {
                for (UserRelation relation : relations) {
                    if (user.getId() == relation.getId()) {
                        user.setRelation(relation.getRelation());
                        break;
                    }
                }
            }
        }
    }
}
