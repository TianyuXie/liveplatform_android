package com.pplive.liveplatform.core.rest.http;

import org.springframework.http.HttpAuthentication;

public class PlayTokenAuthentication extends HttpAuthentication {

    private String mPlayTk;
    
    public PlayTokenAuthentication(String playTk) {
        mPlayTk = playTk;
    }

    @Override
    public String getHeaderValue() {
        return String.format("playtk=%s", mPlayTk);
    }
}
