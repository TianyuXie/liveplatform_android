package com.pplive.liveplatform.core.api.live.resp;

public class Resp<T> {

    T data;
    
    int err;

    String kind;

    public T getData() {
        return data;
    }
    
    public int getError() {
        return err;
    }

    public String getKind() {
        return kind;
    }
}
