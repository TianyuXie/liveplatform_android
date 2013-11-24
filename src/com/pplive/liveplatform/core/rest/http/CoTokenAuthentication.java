package com.pplive.liveplatform.core.rest.http;

import org.springframework.http.HttpAuthentication;

public class CoTokenAuthentication extends HttpAuthentication {

    private String mCoName;
    private String mCoTk;
    
    public CoTokenAuthentication(String coName, String coTk) {
        mCoName = coName;
        mCoTk = coTk;
    }

    @Override
    public String getHeaderValue() {
        return String.format("coname=%s;cotk=%s", mCoName, mCoTk);
    }
}
