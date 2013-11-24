package com.pplive.liveplatform.core.rest.http;

import org.springframework.http.HttpAuthentication;

public class PlayTkAuthentication extends HttpAuthentication {

    private String mPlayTk;
    
    public PlayTkAuthentication(String playTk) {
        mPlayTk = playTk;
    }

    @Override
    public String getHeaderValue() {
        return String.format("playtk=%s", mPlayTk);
    }
}
