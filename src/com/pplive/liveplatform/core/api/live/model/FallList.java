package com.pplive.liveplatform.core.api.live.model;

public class FallList<T> extends List<T> {

    int count;

    String nexttk;

    String previoustk;

    public int count() {
        return count;
    }

    public String nextToken() {
        return nexttk;
    }

    public String previousToken() {
        return previoustk;
    }
}
