package com.pplive.liveplatform.core.service.live.model;

public class LiveAlive {

    long pid;
    
    long delay; // millisecond
    
    public long getProgramId() {
        return pid;
    }
    
    public long getDelayInMillis() {
        return delay;
    }
}
