package com.pplive.liveplatform.core.service.comment.model;

public class FeedItem {
    public String formatedContent;

    public long time;

    public FeedItem(String formatedContent, long time) {
        this.formatedContent = formatedContent;
        this.time = time;
    }

}
