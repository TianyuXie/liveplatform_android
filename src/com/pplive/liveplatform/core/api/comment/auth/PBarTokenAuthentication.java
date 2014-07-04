package com.pplive.liveplatform.core.api.comment.auth;

import org.springframework.http.HttpAuthentication;

public class PBarTokenAuthentication extends HttpAuthentication {

    private String mToken;

    public PBarTokenAuthentication(String token) {
        mToken = token;
    }

    @Override
    public String getHeaderValue() {
        return String.format("tk=%s", mToken);
    }

}
