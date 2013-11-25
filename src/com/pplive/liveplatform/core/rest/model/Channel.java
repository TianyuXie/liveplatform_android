package com.pplive.liveplatform.core.rest.model;

import java.util.ArrayList;

public class Channel {

    int ft;
    
    int bwt;
    
    String[] addr;
    
    String path;
    
    String name;
    
    String[] args;
    
    public int getFt() {
        return ft;
    }
    
    public int getBwt() {
        return bwt;
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
