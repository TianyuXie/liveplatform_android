package com.pplive.liveplatform.core.api.live.model;

public class Follow {

    long[] add;

    long[] del;

    public Follow(long[] add, long[] del) {
        this.add = add;
        this.del = del;
    }
}
