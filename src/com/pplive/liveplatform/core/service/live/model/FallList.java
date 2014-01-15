package com.pplive.liveplatform.core.service.live.model;

public class FallList<T> extends List<T> {

    int count;

    String nexttk;

    public int count() {
        return count;
    }

    public String nextToken() {
        return nexttk;
    }
}
