package com.pplive.liveplatform.core.service.live.model;

import com.google.gson.annotations.SerializedName;

public class User {

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

    String coname;

    Sex sex;
    
    long birthday;
    
    String address;
    
    String status;
    
    long insert_time;
    
    long last_update_time;
    
    
    public String getUsername() {
        return username;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public String getIcon() {
        return icon;
    }
}
