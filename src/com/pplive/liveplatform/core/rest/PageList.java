package com.pplive.liveplatform.core.rest;

public class PageList<T> {

    java.util.List<T> list;
    
    int count;
    
    String nexttk;
    
    public java.util.List<T> getList() {
        return list;
    }
    
    public int count() {
       return count; 
    }
    
    public String nextToken() {
        return nexttk;
    }
}
