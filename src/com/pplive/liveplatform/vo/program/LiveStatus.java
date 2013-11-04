package com.pplive.liveplatform.vo.program;

public class LiveStatus {

    enum Status {
        NOT_START,
        PREVIEW,
        LIVING,
        STOPPED
    }
    
    long mPid;
    
    Status mStatus;
    
    long mDelay;
}
