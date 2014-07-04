package com.pplive.liveplatform.core.api.live.model;

import com.google.gson.annotations.SerializedName;

public class UserFeed {

    Extend extend;

    int id;

    long create_time;

    FeedType feedtype;

    SnsType snstype;

    String feed_text;

    enum FeedType {

        @SerializedName("FollowFriend")
        FOLLOW_FRIEND;
    }

    enum SnsType {

        @SerializedName("user_msg")
        USER_MSG;
    }

    static class Extend {

        User fans;
    }
}
