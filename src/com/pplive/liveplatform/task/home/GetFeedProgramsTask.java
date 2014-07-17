package com.pplive.liveplatform.task.home;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.FeedAPI;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.Feed;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskResult;
import com.pplive.liveplatform.task.TaskResult.TaskStatus;

public class GetFeedProgramsTask extends Task {

    @Override
    protected TaskResult doInBackground(TaskContext... params) {
        if (null == params || 0 == params.length) {
            return new TaskResult(TaskStatus.FAILED, "TaskContext is null");
        }

        TaskContext context = params[0];
        String coToken = context.getString(Extra.KEY_TOKEN);
        String username = context.getString(Extra.KEY_USERNAME);
        String nextToken = context.getString(Extra.KEY_NEXT_TOKEN);
        int fallCount = (Integer) context.get(Extra.KEY_FALL_COUNT);

        FallList<Feed> feeds = null;
        try {
            feeds = FeedAPI.getInstance().getFollowCircleFeeds(coToken, username, nextToken, fallCount);
        } catch (LiveHttpException e) {
            return new TaskResult(TaskStatus.FAILED, e.toString());
        }

        if (null == feeds) {
            return new TaskResult(TaskStatus.FAILED, "No data");
        }

        context.set(Extra.KEY_RESULT, feeds);
        TaskResult result = new TaskResult(TaskStatus.SUCCEED);
        result.setContext(context);

        return result;
    }
}
