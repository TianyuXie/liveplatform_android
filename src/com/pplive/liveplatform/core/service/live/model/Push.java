package com.pplive.liveplatform.core.service.live.model;

import java.util.ArrayList;

import android.text.TextUtils;

public class Push {

    long pid;

    String protocol;

    String[] addr;

    String path;

    String name;

    long now;

    public long getProgramId() {
        return pid;
    }

    public String getProtocol() {
        return protocol;
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

    public long getNowTime() {
        return now;
    }

    public java.util.List<String> getPushUrlList() {
        if (null == addr || 0 == addr.length) {
            return null;
        }

        java.util.List<String> list = new ArrayList<String>(addr.length);
        for (String address : addr) {
            String url = protocol + "://" + address + path + "/" + name;
            list.add(url);
        }

        return list;
    }

    public String getPushUrl() {
        java.util.List<String> list = getPushUrlList();

        if (null != list) {
            for (String url : list) {
                if (!TextUtils.isEmpty(url)) {
                    return url;
                }
            }
        }

        return null;
    }

    public String getAddress() {
        for (String address : addr) {
            return address;
        }

        return null;
    }
}
