package com.pplive.liveplatform.core.user;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.FollowAPI;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskResult;
import com.pplive.liveplatform.core.task.TaskResult.TaskStatus;

public class GetFriendsTask extends Task {

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (null == params || 0 == params.length) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        TaskContext context = params[0];
        String nextToken = context.getString(Extra.KEY_NEXT_TOKEN);
        int fallCount = (Integer) context.get(Extra.KEY_FALL_COUNT);
        String username = context.getString(Extra.KEY_USERNAME);
        FriendType type = (FriendType) context.get(Extra.KEY_FRIEND_TYPE);

        FallList<User> users = null;
        try {
            users = type.getFriends(username, nextToken, fallCount);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.FAILED, "FollowService Error");
        }

        if (users == null) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        context.set(Extra.KEY_RESULT, users);
        result.setContext(context);
        return result;
    }

    public enum FriendType {
        FOLLOWER {

            @Override
            public FallList<User> getFriends(String username, String nextToken, int fallCount) throws LiveHttpException {
                return FollowAPI.getInstance().getFollowers(username, nextToken, fallCount);
            }

        },
        FAN {

            @Override
            public FallList<User> getFriends(String username, String nextToken, int fallCount) throws LiveHttpException {
                return FollowAPI.getInstance().getFans(username, nextToken, fallCount);
            }

        };

        public abstract FallList<User> getFriends(String username, String nextToken, int fallCount) throws LiveHttpException;
    }
}
