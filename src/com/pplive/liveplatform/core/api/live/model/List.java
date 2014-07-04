package com.pplive.liveplatform.core.api.live.model;


public class List<T> {

    java.util.List<T> list;
    
    int total;
    
    public java.util.List<T> getList() {
        return list;
    }
    
    public int total() {
        return total;
    }
}
