package com.pplive.liveplatform.core.service.comment.resp;

public class Resp<T> {

    T data;
    
    int err;
    
    String msg;
    
    public T getData() {
        return data;
    }
    
    public int getError() {
        return err;
    }
    
    public String getMessage() {
        return msg;
    }
}
