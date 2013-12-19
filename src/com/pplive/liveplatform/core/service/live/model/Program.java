package com.pplive.liveplatform.core.service.live.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.util.StringUtil;

public class Program {

    private static final int SUBJECT_ID_ORIGIN = 1; // 原创

    long pid;

    String title;

    String owner;

    User user;

    LiveModeEnum mode;

    long starttime;

    LiveStatusEnum livestatus;

    int subject_id = SUBJECT_ID_ORIGIN;

    String cover_url;

    String screenshot_url;

    String coname = Constants.DEFAULT_CONAME_PPTV;

    Record record;

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

    public String getOwnerNickname() {
        return user != null ? user.getNickname() : owner;
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
        return StringUtil.isNullOrEmpty(cover_url) ? "" : cover_url;
    }

    public String getScreenshotUrl() {
        return StringUtil.isNullOrEmpty(screenshot_url) ? "" : screenshot_url;
    }

    public String getLiveToken() {
        return (null == tk || StringUtil.isNullOrEmpty(tk.livetk)) ? "" : tk.livetk;
    }

    public int getVV() {
        return record == null ? 0 : record.vv;
    }

    static class Record {

        int vv;

        int online;
    }

    static class Token {
        String livetk;
    }

}
