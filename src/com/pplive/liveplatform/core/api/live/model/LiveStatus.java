package com.pplive.liveplatform.core.api.live.model;

public class LiveStatus {

    LiveStatusEnum livestatus;

    Long pid;

    Long delay; // second

    StreamStatus streamstatus;

    public LiveStatus(LiveStatusEnum livestatus) {
        this.livestatus = livestatus;
    }

    public LiveStatusEnum getStatus() {
        return livestatus;
    }

    public long getProgramId() {
        return null != pid ? pid : -1;
    }

    public long getDelayInSeconds() {
        return null != delay ? delay : 0;
    }
}
