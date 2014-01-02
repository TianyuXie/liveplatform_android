package com.pplive.liveplatform.core.service.live.model;

public class Packet {

    int minVersionCode;
    
    int maxVersionCode;
    
    String distVersionName;
    
    String url;
    
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
    
    public String getUrl() {
        return url;
    }
    
    public String getDescription() {
        return description;
    }
}
