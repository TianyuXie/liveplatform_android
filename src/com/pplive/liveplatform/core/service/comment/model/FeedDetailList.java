package com.pplive.liveplatform.core.service.comment.model;

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
}
