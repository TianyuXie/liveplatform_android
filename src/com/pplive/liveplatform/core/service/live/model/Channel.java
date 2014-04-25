package com.pplive.liveplatform.core.service.live.model;

import java.util.ArrayList;

public class Channel {

    int ft;

    int bwt;

    String[] addr;

    String path;

    String name;

    String[] args;

    StreamStatus streamstatus;

    long starttime;

    long endtime;

    public int getFt() {
        return ft;
    }

    public String[] getAddrs() {
        return addr;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public int getBwType() {
        return bwt;
    }

    public long getStartTime() {
        return starttime;
    }

    public long getEndTime() {
        return endtime;
    }

    public java.util.List<String> getFullPathList() {
        if (null == addr || 0 == addr.length) {
            return null;
        }

        java.util.List<String> list = new ArrayList<String>(addr.length);
        for (String address : addr) {
            String url = address + path + "/" + name;
            list.add(url);
        }

        return list;
    }

}
