package com.pplive.liveplatform.core.api.comment.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.ui.player.Emoji;

public class FeedDetailList {
    
    static final String TAG = FeedDetailList.class.getSimpleName();

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

    public List<FeedItem> getFeedItems() {
        return getFeedItems("", 0xffffffff, 0xffffffff, 0xffffffff);
    }

    public List<FeedItem> getFeedItems(String currentUser, int userColor, int contentColor, int ownerColor) {
        List<FeedItem> result = new ArrayList<FeedItem>();
        for (long id : feedIds) {
            result.add(makeItem(currentUser, mapFeed.get(id), userColor, contentColor, ownerColor));
        }
        return result;
    }

    private FeedItem makeItem(String currentUser, Feed feed, int userColor, int contentColor, int ownerColor) {
        userColor -= 0xff000000;
        contentColor -= 0xff000000;
        ownerColor -= 0xff000000;
        String username = feed.getUserName();
        String content = feed.getContent();
        String nickname = mapUser.get(username).getDisplayName();
        if (TextUtils.isEmpty(nickname)) {
            nickname = username;
        } else {
            nickname = nickname.split("\\(")[0];
        }
        if (currentUser.equals(username)) {
            contentColor = ownerColor;
        }
        
        content = convertEmojiToImageTag(content);
        
        return new FeedItem(String.format("<b><font color='#%06x'>%s:&nbsp;</font><font color='#%06x'>%s</font></b>", userColor, nickname, contentColor,
                content), feed.createTime);
    }

    private String convertEmojiToImageTag(String content) {
        StringBuilder sb = new StringBuilder(content);
        
        Log.d(TAG, sb.toString());

        Matcher matcher = Emoji.REG_EMOJI.matcher(sb);

        while (matcher.find()) {
            sb.replace(matcher.start(), matcher.end(), "<img src=\"" + matcher.group(1) + "\"/>");
            
            matcher = Emoji.REG_EMOJI.matcher(sb);
        }

        Log.d(TAG, sb.toString());

        return sb.toString();
    }
}
