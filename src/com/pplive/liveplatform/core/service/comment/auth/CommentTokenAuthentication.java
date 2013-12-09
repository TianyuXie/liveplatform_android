package com.pplive.liveplatform.core.service.comment.auth;

import org.springframework.http.HttpAuthentication;

public class CommentTokenAuthentication extends HttpAuthentication {

    private String mToken;

    public CommentTokenAuthentication(String token) {
        mToken = token;
    }

    @Override
    public String getHeaderValue() {
        return String.format("tk=%s", mToken);
    }

}
