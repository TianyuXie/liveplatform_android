package com.pplive.liveplatform.core.api.live.model;

import com.google.gson.annotations.SerializedName;

public class Feed {

    public enum FeedType {
        @SerializedName("FollowFriend")
        FOLLOW_FRIEND("FollowFriend"),

        @SerializedName("Upload")
        UPLOAD("Upload"),

        @SerializedName("CreateProgram")
        CREATE_PROGRAM("CreateProgram"),

        @SerializedName("AuditProgram")
        AUDIT_PROGRAM("AuditProgram");

        private FeedType(String name) {
            this.name = name;
        }

        private String name;

        @Override
        public String toString() {
            return name;
        }
    }

    public enum SnsType {
        @SerializedName("user")
        USER("user"),

        @SerializedName("user_private")
        USER_PRIVATE("user_private"),

        @SerializedName("follow_circle")
        FOLLOW_CIRCLE("follow_circle"),

        @SerializedName("user_msg")
        USER_MSG("user_msg"),

        @SerializedName("user_fans")
        USER_FANS("user_fans");

        private SnsType(String name) {
            this.name = name;
        }

        private String name;

        @Override
        public String toString() {
            return name;
        }
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

    public SnsType getSnsType() {
        return snstype;
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
