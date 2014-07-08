package com.pplive.liveplatform.core.api.live.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.api.IUser;
import com.pplive.liveplatform.util.StringUtil;

public class User implements IUser, Serializable {

    private static final long serialVersionUID = 1L;

    public enum Sex {

        @SerializedName("female")
        FEMALE,

        @SerializedName("male")
        MALE,

        @SerializedName("unknown")
        UNKNOWN;
    }

    public enum Relation {

        SELF,

        FOLLOW,

        FAN,

        FOLLOW_FAN,

        UNKNOWN
    }

    long id = -1;

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

    Relation relation = Relation.UNKNOWN;

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getDisplayName() {
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

    @Override
    public boolean equals(Object o) {

        if (o instanceof User) {
            User that = (User) o;

            return this.id >= 0 && this.id == that.id;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (int) this.id;
    }
}
