package com.pplive.liveplatform.core.service.live.model;


public class LiveStatus {

    LiveStatusEnum livestatus;
    
    long pid;
    
    long delay;  // second
    
    public LiveStatus(LiveStatusEnum livestatus) {
        this.livestatus = livestatus;
    }
    
    public LiveStatusEnum getStatus() {
        return livestatus;
    }
    
    public long getProgramId() {
        return pid;
    }
    
    public long getDelayInSeconds() {
        return delay;
    }
}
