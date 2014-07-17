package com.pplive.liveplatform.task.home;

import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.FollowAPI;
import com.pplive.liveplatform.core.api.live.SearchAPI;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.api.live.model.UserRelation;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

public class GetRecommendUsersTask extends Task {

    static final String TAG = GetRecommendUsersTask.class.getSimpleName();

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (null == params || 0 == params.length) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        TaskContext context = params[0];
        String username = context.getString(Extra.KEY_USERNAME);
        String token = context.getString(Extra.KEY_TOKEN);

        List<User> users = null;
        try {
            users = SearchAPI.getInstance().recommendUser();
        } catch (LiveHttpException e) {
            Log.w(TAG, e.toString());
            return new TaskResult(TaskStatus.FAILED, e.toString());
        }

        if (users == null) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        updateRelation(token, username, users);

        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(Extra.KEY_RESULT, users);
        result.setContext(context);
        return result;
    }

    private void updateRelation(String coToken, String username, List<User> users) {
        List<UserRelation> relations = null;
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(coToken)) {
            try {
                relations = FollowAPI.getInstance().getRelations(coToken, username, users);
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
