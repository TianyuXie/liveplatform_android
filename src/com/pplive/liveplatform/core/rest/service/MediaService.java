package com.pplive.liveplatform.core.rest.service;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.rest.http.Url;

public class MediaService extends AbsService {

    private static final String TAG = MediaService.class.getSimpleName();

    private static final String TEMPLATE_GET_PUSH = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/media/v1/pptv/program/{pid}/push")
            .toString();

    private static final String TEMPLATE_GET_PLAY = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/media/v1/pptv/program/{pid}/play")
            .toString();

    private static final String TEMPLATE_GET_PREVIEW = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT,
            "/media/v1/pptv/program/{pid}/preview").toString();

    private static final MediaService sInstance = new MediaService();

    public static final MediaService getInstance() {
        return sInstance;
    }

    private MediaService() {

    }

    
    public void getPushUrl() {
        
    }
}
