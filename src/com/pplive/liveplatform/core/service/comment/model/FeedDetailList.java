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

    public long getTopFeedId() {
        if (feedIds.length != 0) {
            return feedIds[0];
        } else {
            return -1;
        }
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

    public int getSize() {
        return feedIds.length;
    }

    public Collection<String> getFeedStrings() {
        return getFeedStrings(0xffffffff, 0xffffffff);
    }

    public Collection<String> getFeedStrings(int userColor, int contentColor) {
        Collection<String> result = new ArrayList<String>();
        for (long id : feedIds) {
            result.add(formatFeed(mapFeed.get(id), userColor, contentColor));
        }
        return result;
    }

    private String formatFeed(Feed feed, int userColor, int contentColor) {
        userColor -= 0xff000000;
        contentColor -= 0xff000000;
        String username = feed.getUserName();
        String content = feed.getContent();
        //        String time = feed.getTime();
        String time = "";
        String nickname = mapUser.get(username).getNickname();
        if (TextUtils.isEmpty(nickname)) {
            nickname = username;
        }
        return String.format("<b><font color='#%06x'>%s:&nbsp;&nbsp;<small>%s</small></font><br><font color='#%06x'>&nbsp;&nbsp;&nbsp;%s</font></b><br>",
                userColor, nickname, time, contentColor, content);
    }

}
