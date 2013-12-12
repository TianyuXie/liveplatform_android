package com.pplive.liveplatform.core.service.comment.model;

import com.google.gson.annotations.SerializedName;

public class Feed {

    public enum Type {

        @SerializedName("Comment")
        COMMENT,

        @SerializedName("Share")
        SHARE,

        @SerializedName("UPLOAD")
        UPLOAD,

        @SerializedName("FollowChannel")
        FOLLOW_CHANNEL;
    }

    long id;

    String content;

    String refId;

    String userName;

    int floor;

    long createtime;

    Type type;

    public Feed(String content, long pid, Type type) {
        this.content = content;
        this.refId = "LivePlatform-pbar_" + pid;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return String.format("<b>%s: <font color='#919191'>%s</font></b><br>", userName, content);
    }

}
