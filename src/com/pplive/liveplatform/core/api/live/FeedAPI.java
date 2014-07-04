package com.pplive.liveplatform.core.api.live;

public class FeedAPI extends RESTfulAPI {

    static final String TAG = FeedAPI.class.getSimpleName();

    private static final FeedAPI sInstance = new FeedAPI();

    public static FeedAPI getInstance() {
        return sInstance;
    }

    private FeedAPI() {
    }
}
