package com.pplive.liveplatform.core.rest.http;

import org.springframework.http.HttpAuthentication;

public class LiveTkAuthentication extends HttpAuthentication {
    
    private String mLiveTk;
    
    public LiveTkAuthentication(String liveTk) {
        mLiveTk = liveTk;
    }

    @Override
    public String getHeaderValue() {
        return String.format("livetk=%s", mLiveTk);
    }

}
