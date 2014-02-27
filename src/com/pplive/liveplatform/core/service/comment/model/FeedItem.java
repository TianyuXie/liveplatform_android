package com.pplive.liveplatform.core.service.comment.model;

import com.pplive.liveplatform.util.TimeUtil;

public class FeedItem {
    public String formatedContent;

    public String time;

    public FeedItem(String formatedContent, long timestamp) {
        super();
        this.formatedContent = formatedContent;
        this.time = TimeUtil.getTimeGap(timestamp);
    }

}
