package com.pplive.liveplatform.core.service.comment.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import android.text.TextUtils;

public class FeedDetailList {

    long[] feedIds;

    Map<String, User> mapUser;

    Map<Long, Feed> mapFeed;

    Map<Long, Integer> mapVoteUpCount;

    String nextToken;

    String previousToken;

    int availCount;

    int totalCount;

    public long[] getFeedIds() {
        return feedIds;
    }

    public Map<String, User> getUserMap() {
        return mapUser;
    }

    public Map<Long, Feed> getFeedMap() {
        return mapFeed;
    }

    public Map<Long, Integer> getVoteUpCountMap() {
        return mapVoteUpCount;
    }

    public Collection<String> getFeeds(String color) {
        Collection<String> result = new ArrayList<String>();
        for (long id : feedIds) {
            result.add(buildFeedString(mapFeed.get(id), color));
        }
        return result;
    }

    public int getSize() {
        return feedIds.length;
    }

    private String buildFeedString(Feed feed, String color) {
        String username = feed.getUserName();
        String content = feed.getContent();
        String nickname = mapUser.get(username).getNickname();
        if (TextUtils.isEmpty(nickname)) {
            nickname = username;
        }
        return String.format("<b><font color='%s'>%s: </font><font color='#919191'>%s</font></b><br>", color, nickname, content);
    }

}
