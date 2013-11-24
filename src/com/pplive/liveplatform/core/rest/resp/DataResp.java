package com.pplive.liveplatform.core.rest.resp;

public class DataResp<T> extends Resp {
    
    T data;
    
    public T getData() {
        return data;
    }
}
