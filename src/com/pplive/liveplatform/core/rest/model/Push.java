package com.pplive.liveplatform.core.rest.model;

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
    
    public String[] getAddr() {
        return addr;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getName() {
        return name;
    }
}
