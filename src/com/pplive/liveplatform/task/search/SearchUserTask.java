package com.pplive.liveplatform.task.search;

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
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

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

        updateUserRelation(cotoken, username, data.getList());

        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(Extra.KEY_SEARCH_RESULT, data);
        result.setContext(context);
        return result;
    }

    private void updateUserRelation(String cotoken, String username, List<User> users) {
        List<UserRelation> relations = null;
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(cotoken)) {
            try {
                relations = FollowAPI.getInstance().getRelations(cotoken, username, users);
            } catch (LiveHttpException e) {
                Log.w(TAG, e.toString());
            }
        }

        for (int i = 0; null != users && i < users.size(); ++i) {
            int relation = -1;
            for (int j = 0; null != relations && j < users.size(); ++j) {
                if (users.get(i).getId() == relations.get(j).getId()) {
                    relation = relations.get(j).getRelation();
                    break;
                }
            }
            users.get(i).setRelation(relation);
        }

    }
}
