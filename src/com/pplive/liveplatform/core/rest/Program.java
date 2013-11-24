package com.pplive.liveplatform.core.rest;


public class Program {

    String owner;

    String mode;

    long pid;

    long starttime;

    String title;

    String cover_url;

    String coname = "pptv";

    int subject_id = 1;

    public Program(String owner, String mode, long starttime) {
        this.owner = owner;
        this.mode = mode;
        this.starttime = starttime;
    }

    public long getPid() {
        return pid;
    }
    
    public long getStartTime() {
        return starttime;
    }

    @Override
    public String toString() {
        return String.format("owner: %s; starttime: %d; title: %s; cover_url: %s;", owner, starttime, title, cover_url);
    }
}
