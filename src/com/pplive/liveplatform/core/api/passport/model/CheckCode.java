package com.pplive.liveplatform.core.api.passport.model;

public class CheckCode {
    
    String guid;
    
    String image_url;
    
    public CheckCode(String guid, String image_url) {
        this.guid = guid;
        this.image_url = image_url;
    }

    public String getGUID() {
        return guid;
    }
    
    public String getImageUrl() {
        return image_url;
    }
}
