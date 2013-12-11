package com.pplive.liveplatform.core.service.comment.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

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

    public Collection<String> getFeedStrings() {
        Collection<String> result = new ArrayList<String>();
        for (long id : feedIds) {
            result.add(mapFeed.get(id).toString());
        }
        return result;
    }

    public int getSize() {
        return feedIds.length;
    }

}
