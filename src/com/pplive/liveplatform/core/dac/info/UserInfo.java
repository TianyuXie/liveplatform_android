package com.pplive.liveplatform.core.dac.info;

import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.util.StringUtil;

import android.content.Context;

public class UserInfo {

    private static String sUserName = "unknown";
    
    public static void init(Context context) {
        reset(context);
    }
    
    public static void reset(Context context) {
        String username = UserManager.getInstance(context).getUsernamePlain();
        sUserName = !StringUtil.isNullOrEmpty(username) ? username : "unknown"; 
    }
    
    public static String getUserName() {
        return sUserName;
    }
    
    private UserInfo() {
        
    }
}
