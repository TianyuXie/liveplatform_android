package com.pplive.liveplatform.core.service.comment.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public Feed(long pid, String content, Type type) {
        this.refId = "LivePlatform-pbar_" + pid;
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public String getUserName() {
        return userName;
    }

    public String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss yyyy.MM.dd", Locale.US);
        return format.format(new Date(createtime));
    }

}
