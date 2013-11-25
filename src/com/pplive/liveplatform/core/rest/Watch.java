package com.pplive.liveplatform.core.rest;

public class Watch {

    long pid;
    
    String protocol;
    
    long delay;
    
    Channel[] channels;
    
    public long getId() {
        return pid;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public Channel[] getChannels() {
        return channels;
    }
}
