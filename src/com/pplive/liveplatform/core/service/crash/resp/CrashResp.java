package com.pplive.liveplatform.core.service.crash.resp;


public class CrashResp {

    boolean result;

    boolean updump = false;

    String upurl;

    public String getUploadUrl() {
        return upurl;
    }
    
    public boolean uploadDump() {
        return updump;
    }
}
