package com.pplive.liveplatform.core.service.live.model;

import java.util.ArrayList;

public class Push {

    long pid;
    
    String protocol;
    
    String[] addr;
    
    String path;
    
    String name;
    
    public long getId() {
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
}
