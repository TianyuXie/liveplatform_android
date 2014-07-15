package com.pplive.liveplatform.core.api.live.model;

import com.google.gson.annotations.SerializedName;

public class Feed {

    public enum FeedType {
        @SerializedName("FollowFriend")
        FOLLOW_FRIEND,

        @SerializedName("Upload")
        UPLOAD,

        @SerializedName("CreateProgram")
        CREATE_PROGRAM,

        @SerializedName("AuditProgram")
        AUDIT_PROGRAM;
    }

    public enum SnsType {
        @SerializedName("user")
        USER,

        @SerializedName("user_private")
        USER_PRIVATE,

        @SerializedName("follow_circle")
        FOLLOW_CIRCLE,

        @SerializedName("user_msg")
        USER_MSG,

        @SerializedName("user_fans")
        USER_FANS;
    }

    static class Extend {

        Program program;

        User fans;

    }

    Extend extend;

    long id;

    SnsType snstype;

    FeedType feedtype;

    long create_time;

    String feed_text;

    public long getCreateTime() {
        return create_time;
    }

    public FeedType getFeedType() {
        return feedtype;
    }

    public Program getProgram() {
        return null != extend ? extend.program : null;
    }

    public User getFans() {
        return null != extend ? extend.fans : null;
    }

}
