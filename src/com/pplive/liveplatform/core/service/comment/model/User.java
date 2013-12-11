package com.pplive.liveplatform.core.service.comment.model;

import com.pplive.liveplatform.core.service.IUser;

public class User implements IUser {

    String userName;
    
    String nickName;
    
    String iconUrl;
    
    String gender;
    
    @Override
    public String getUsername() {
        
        return userName;
    }
    
    @Override
    public String getNickname() {
        
        return nickName;
    }
    
    @Override
    public String getIcon() {
        
        return iconUrl;
    }
}
