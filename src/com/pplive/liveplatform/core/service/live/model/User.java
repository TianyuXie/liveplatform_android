package com.pplive.liveplatform.core.service.live.model;

import com.google.gson.annotations.SerializedName;
import com.pplive.liveplatform.core.service.IUser;
import com.pplive.liveplatform.util.StringUtil;

public class User implements IUser {

    enum Sex {

        @SerializedName("female")
        FEMALE,

        @SerializedName("male")
        MALE,

        @SerializedName("unknown")
        UNKNOWN;
    }

    String username;

    String nickname;

    String icon;

    String coname = "pptv";

    Sex sex;

    long birthday;

    String address;

    String status;

    long insert_time;

    long last_update_time;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getNickname() {
        if (StringUtil.isNullOrEmpty(nickname)) {
            return username;
        } else {
            return nickname;
        }
    }

    @Override
    public String getIcon() {
        return icon;
    }
}
