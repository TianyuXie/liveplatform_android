package com.pplive.liveplatform.core.rest;

public class PageList<T> extends List<T> {

    int count;
    
    String nexttk;
    
    public int count() {
       return count; 
    }
    
    public String nextToken() {
        return nexttk;
    }
}
