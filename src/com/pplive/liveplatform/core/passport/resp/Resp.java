package com.pplive.liveplatform.core.passport.resp;

public class Resp<T> {

    int errorCode;
    
    String message;
    
    T result;
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public T getResult() {
        return result;
    }
}
