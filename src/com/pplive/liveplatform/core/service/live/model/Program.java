package com.pplive.liveplatform.core.service.live.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Program {

    long pid;

    String title;

    String owner;

    LiveModeEnum mode;

    long starttime;

    LiveStatusEnum livestatus;

    int subject_id = 1;

    String cover_url;
    
    String screenshot_url;

    String coname = "pptv";
    
    Token tk;
    
    public Program(String owner, String title, long starttime) {
        this(owner, LiveModeEnum.CAMERA, title, starttime);
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

    public String getStartTimeLong() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss yyyy.MM.dd", Locale.US);
        return format.format(new Date(starttime));
    }

    public String getStartTimeShort() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
        return format.format(new Date(starttime));
    }

    public LiveStatusEnum getLiveStatus() {
        return livestatus;
    }

    public String getCoverUrl() {
        if (cover_url == null || cover_url.equals("null")){
            return "";
        }
        return cover_url;
    }
    
    public String getScreenshotUrl() {
        if (screenshot_url == null || screenshot_url.equals("null")){
            return "";
        }
        return screenshot_url;
    }
    
    public String getLiveToken() {
        if (null != tk) {
            return tk.livetk;
        }
        
        return "";
    }
    
    class Token {
        String livetk;
    }
}
