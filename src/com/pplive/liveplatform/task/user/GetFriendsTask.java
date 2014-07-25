package com.pplive.liveplatform.task.user;

import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.FollowAPI;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.api.live.model.UserRelation;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

public class GetFriendsTask extends Task {

    static final String TAG = GetFriendsTask.class.getSimpleName();

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (null == params || 0 == params.length) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        TaskContext context = params[0];
        FriendType type = (FriendType) context.get(Extra.KEY_FRIEND_TYPE);
        String queryUsername = context.getString(Extra.KEY_QUERY_USERNAME);

        String nextToken = context.getString(Extra.KEY_NEXT_TOKEN);
        int fallCount = (Integer) context.get(Extra.KEY_FALL_COUNT);

        String username = context.getString(Extra.KEY_USERNAME);
        String coToken = context.getString(Extra.KEY_TOKEN);

        FallList<User> users = null;
        try {
            boolean cdn = TextUtils.isEmpty(username) || TextUtils.isEmpty(coToken);
            users = type.getFriends(cdn, queryUsername, nextToken, fallCount);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.FAILED, e.toString());
        }

        if (users == null) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        updateRelation(coToken, username, users.getList());

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

    public enum FriendType {
        FOLLOWER {

            @Override
            public FallList<User> getFriends(boolean cdn, String username, String nextToken, int fallCount) throws LiveHttpException {
                return FollowAPI.getInstance().getFollowers(cdn, username, nextToken, fallCount);
            }

        },
        FAN {

            @Override
            public FallList<User> getFriends(boolean cdn, String username, String nextToken, int fallCount) throws LiveHttpException {
                return FollowAPI.getInstance().getFans(cdn, username, nextToken, fallCount);
            }

        };

        public abstract FallList<User> getFriends(boolean cdn, String username, String nextToken, int fallCount) throws LiveHttpException;
    }
}
