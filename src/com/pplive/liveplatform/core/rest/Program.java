package com.pplive.liveplatform.core.rest;

import java.util.Locale;

public class Program {

    String owner;

    String mode;

    long pid;

    long starttime;

    String title;

    String cover_url;

    String coname = "pptv";

    int subject_id = 1;

    public Program(String owner, String mode, String title, long starttime) {
        this.owner = owner;
        this.mode = mode;
        this.title = title;
        this.starttime = starttime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public long getPid() {
        return pid;
    }

    public long getStartTime() {
        return starttime;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "owner: %s; starttime: %d; title: %s; cover_url: %s;", owner, starttime, title, cover_url);
    }
}
