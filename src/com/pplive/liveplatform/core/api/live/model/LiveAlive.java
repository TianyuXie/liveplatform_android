package com.pplive.liveplatform.core.api.live.model;

public class LiveAlive {

    long pid;
    
    long delay; // second
    
    public long getProgramId() {
        return pid;
    }
    
    public long getDelayInSeconds() {
        return delay;
    }
}
