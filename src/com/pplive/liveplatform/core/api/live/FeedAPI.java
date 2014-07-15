package com.pplive.liveplatform.core.api.live;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.api.BaseURL;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.Feed;
import com.pplive.liveplatform.core.api.live.resp.FeedFallListResp;
import com.pplive.liveplatform.core.api.live.resp.MessageResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class FeedAPI extends RESTfulAPI {

    static final String TAG = FeedAPI.class.getSimpleName();

    private static final String TEMPLATE_GET_FOLLOW_CICLE_FEEDS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_TEST_HOST,
            "/ft/feed/v3/pptv/user/{username}/followcicle/feeds?nexttk={nexttk}&fallcount={fallcount}").toString();

    private static final String TEMPLATE_GET_SYS_MSG = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_TEST_HOST,
            "/ft/feed/v3/pptv/user/{username}/sysmsgs?nexttk={nexttk}&fallcount={fallcount}").toString();

    private static final String TEMPLATE_DELETE_FEED = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_TEST_HOST,
            "/ft/feed/v3/pptv/user/{username}/{snstype}/{feedtype}").toString();

    private static final FeedAPI sInstance = new FeedAPI();

    public static FeedAPI getInstance() {
        return sInstance;
    }

    private FeedAPI() {
    }

    public FallList<Feed> getFollowCircleFeeds(String coToken, String username, String nextToken, int fallCount) throws LiveHttpException {
        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mHttpHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<String> req = new HttpEntity<String>(mHttpHeaders);

        FeedFallListResp resp = null;
        try {
            HttpEntity<FeedFallListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_FOLLOW_CICLE_FEEDS, HttpMethod.GET, req, FeedFallListResp.class, username,
                    nextToken, fallCount);

            resp = rep.getBody();
            if (0 == resp.getError()) {
                return resp.getData();
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null != resp) {
            throw new LiveHttpException(resp.getError());
        } else {
            throw new LiveHttpException();
        }
    }

    public FallList<Feed> getSystemMsgs(String coToken, String username, String nextToken, int fallCount) throws LiveHttpException {
        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mHttpHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<String> req = new HttpEntity<String>(mHttpHeaders);

        FeedFallListResp resp = null;
        try {
            HttpEntity<FeedFallListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_SYS_MSG, HttpMethod.GET, req, FeedFallListResp.class, username, nextToken,
                    fallCount);

            resp = rep.getBody();

            if (0 == resp.getError()) {
                return resp.getData();
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null != resp) {
            throw new LiveHttpException(resp.getError());
        } else {
            throw new LiveHttpException();
        }
    }

    public boolean deleteFeed(String coToken, String username, Feed feed) throws LiveHttpException {
        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mHttpHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<String> req = new HttpEntity<String>(mHttpHeaders);

        MessageResp resp = null;
        try {
            HttpEntity<MessageResp> rep = mRestTemplate.exchange(TEMPLATE_DELETE_FEED, HttpMethod.DELETE, req, MessageResp.class, username, feed.getSnsType(),
                    feed.getFeedType());

            resp = rep.getBody();

            if (0 == resp.getError()) {
                return true;
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null != resp) {
            throw new LiveHttpException(resp.getError());
        } else {
            throw new LiveHttpException();
        }
    }
}
