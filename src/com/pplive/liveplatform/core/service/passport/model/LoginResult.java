package com.pplive.liveplatform.core.service.passport.model;

import com.pplive.liveplatform.util.StringUtil;

public class LoginResult {
    public final static int FROM_TENCENT = 3000;

    public final static int FROM_SINA = 3001;

    String username;
    String token;
    String nickName;
    String faceUrl;

    int thirdPartySource;
    String thirdPartyToken;
    String thirdPartyID;
    String thirdPartyNickName;
    String thirdPartyFaceUrl;

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public void setUsername(String name) {
        username = name;
    }

    public void setToken(String stoken) {
        token = stoken;
    }

    public String getThirdPartyToken() {
        return thirdPartyToken;
    }

    public String getThirdPartyID() {
        return thirdPartyID;
    }

    public String getNickName() {
        return StringUtil.isNullOrEmpty(nickName) ? thirdPartyNickName : nickName;
    }

    public String getFaceUrl() {
        return StringUtil.isNullOrEmpty(faceUrl) ? thirdPartyFaceUrl : faceUrl;
    }

    public String getThirdPartyNickName() {
        return thirdPartyNickName;
    }

    public String getThirdPartyFaceUrl() {
        return thirdPartyFaceUrl;
    }

    public void setThirdPartyToken(String token) {
        thirdPartyToken = token;
    }

    public void setThirdPartyNickName(String name) {
        thirdPartyNickName = name;
    }

    public void setThirdPartyID(String id) {
        thirdPartyID = id;
    }

    public void setThirdPartyFaceUrl(String url) {
        thirdPartyFaceUrl = url;
    }

    public int getThirdPartySource() {
        return thirdPartySource;
    }

    public void setThirdPartySource(int thirdPartySource) {
        this.thirdPartySource = thirdPartySource;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

}
