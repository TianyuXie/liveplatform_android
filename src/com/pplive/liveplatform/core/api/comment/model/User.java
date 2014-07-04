package com.pplive.liveplatform.core.api.comment.model;

import com.pplive.liveplatform.core.api.IUser;

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
    public String getDisplayName() {
        
        return nickName;
    }
    
    @Override
    public String getIcon() {
        
        return iconUrl;
    }
}
