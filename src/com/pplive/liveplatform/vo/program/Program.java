package com.pplive.liveplatform.vo.program;

import java.util.Locale;

public class Program {

    long pid;

    String extendal_url;

    String intro;

    String subject;

    String cover_url;

    String screenshot_url;

    long starttime;

    long endtime;

    long insert_time;

    long last_update_time;

    int subject_id;

    String coname;

    String owner;

    String title;

    String[] tags;

    // String[] watch_protocols;

    String mode;

    String livestatus;

    @Override
    public String toString() {
        return String.format(Locale.US, "%d:%s:%s", pid, title, owner);
    }

    public long getPid() {
        return pid;
    }

    public String getExtendal_url() {
        return extendal_url;
    }

    public String getIntro() {
        return intro;
    }

    public String getSubject() {
        return subject;
    }

    public String getCover_url() {
        return cover_url;
    }

    public String getScreenshot_url() {
        return screenshot_url;
    }

    public long getStarttime() {
        return starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public long getInsert_time() {
        return insert_time;
    }

    public long getLast_update_time() {
        return last_update_time;
    }

    public int getSubject_id() {
        return subject_id;
    }

    public String getConame() {
        return coname;
    }

    public String getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public String[] getTags() {
        return tags;
    }

    public String getMode() {
        return mode;
    }

    public String getLivestatus() {
        return livestatus;
    }

}
