package com.pplive.liveplatform.core.rest;

import android.annotation.SuppressLint;


public class Program {

    String owner;

    LiveModeEnum mode;

    long pid;

    long starttime;

    String title;
    
    LiveStatusEnum livestatus;

    String cover_url;

    String coname = "pptv";

    int subject_id = 1;
    
    public Program(long pid) {
        this.pid = pid;
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
    
    public long getStartTime() {
        return starttime;
    }
    
    public String getTitle() {
        return title;
    }
    
    public LiveStatusEnum getLiveStatus() {
        return livestatus;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("pid: %d; owner: %s; starttime: %d; title: %s; livestatus: %s; cover_url: %s;", pid, owner, starttime, title, livestatus, cover_url);
    }
}
