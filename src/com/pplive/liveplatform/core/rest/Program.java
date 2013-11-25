package com.pplive.liveplatform.core.rest;

public class Program {

    long pid;

    String title;

    String owner;

    LiveModeEnum mode;

    long starttime;

    LiveStatusEnum livestatus;

    int subject_id = 1;

    String cover_url;

    String coname = "pptv";

    public Program(String owner, String title, long starttime) {
        this(owner, LiveModeEnum.UNKNOWN, title, starttime);
    }

    public Program(String owner, LiveModeEnum mode, String title, long starttime) {
        this.owner = owner;
        this.mode = mode;
        this.title = title;
        this.starttime = starttime;
    }
    

    public void setTitle(String title) {
        this.title = title;
    }
    
    public long getId() {
        return pid;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getOwner() {
        return owner;
    }

    public long getStartTime() {
        return starttime;
    }
    
    public LiveStatusEnum getLiveStatus() {
        return livestatus;
    }

    public String getCoverUrl() {
        return cover_url;
    }

    @Override
    public String toString() {
        return String.format("owner: %s; starttime: %d; title: %s; cover_url: %s;", owner, starttime, title, cover_url);
    }
}
