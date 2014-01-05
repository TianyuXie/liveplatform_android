package com.pplive.liveplatform.core.service.live.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.IUser;
import com.pplive.liveplatform.util.StringUtil;

public class User implements IUser, Serializable {

    private static final long serialVersionUID = 1L;

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

    String coname = Constants.DEFAULT_CONAME_PPTV;

    Sex sex;

    long birthday;

    String address;

    String status;

    long insert_time;

    long last_update_time;
    
    public void setIcon(String icon){
        this.icon = icon;
    }

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
