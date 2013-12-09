package com.pplive.liveplatform.core.service.passport.model;

public class LoginResult {

    String username;
    
    String token;
    
    String thirdPartyToken;
    
    String thirdPartyID;
    String thirdPartyNickName;
    String thirdPartyFaceUrl;
    
    public String getUsername() {
        return username;
    }
    
    public String getToken() {
        return token;
    }
    
    public String getThirdPartyToken() {
        return thirdPartyToken;
    }
    
    public String getThirdPartyNickName()
    {
        return thirdPartyNickName;
        
    }
    
    public String getThirdPartyID()
    {
        return thirdPartyID;
        
    }

    public String getThirdPartyFaceUrl()
    {
        return thirdPartyFaceUrl;
    }
    
    public void setThirdPartyToken(String token) {
        thirdPartyToken = token;
    }
    
    public void setThirdPartyNickName(String name)
    {
        thirdPartyNickName = name;
        
    }
    
    public void setThirdPartyID(String id)
    {
        thirdPartyID = id;
        
    }

    public void setThirdPartyFaceUrl(String url)
    {
        thirdPartyFaceUrl = url;
    }
    
}
