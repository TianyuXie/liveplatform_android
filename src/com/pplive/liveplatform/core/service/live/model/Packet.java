package com.pplive.liveplatform.core.service.live.model;

public class Packet {

    int minVersionCode;
    
    int maxVersionCode;
    
    int distVersionCode;
    
    String distVersionName;
    
    String url;
    
    int mode;
    
    String description;
    
    public int getMinVersionCode() {
        return minVersionCode;
    }
    
    public int getMaxVersionCode() {
        return maxVersionCode;
    }
    
    public String getDistVersionName() {
        return distVersionName;
    }
    
    public int getDistVersionCode() {
        return distVersionCode;
    }
    
    public String getUrl() {
        return url;
    }
    
    public int getMode() {
        return mode;
    }
    
    public String getDescription() {
        return description;
    }
}
