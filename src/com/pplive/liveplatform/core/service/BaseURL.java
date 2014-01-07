package com.pplive.liveplatform.core.service;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.dac.info.AppInfo;
import com.pplive.liveplatform.util.URL;

public class BaseURL extends URL {
    
    private static final long serialVersionUID = -4915855998432875190L;

    public BaseURL(Protocol protocol, String host, String path)  {
        this(protocol, host, -1, path);
    }
    
    public BaseURL(Protocol protocol, String host, int port, String path) {
        super(protocol, host, port, path);
        
        this.addParameter("_coname", Constants.DEFAULT_CONAME_PPTV);
        this.addParameter("from", Constants.PLATFORM_ANDROID_PHONE);
        this.addParameter("version", AppInfo.getVersionName());
    }
}
