package com.pplive.liveplatform.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLEncoderUtil {

    public static String encode(String s) {
        
        try {
            
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            
        }
        
        return null;
    }
    
    
    private URLEncoderUtil() {
    }
}
