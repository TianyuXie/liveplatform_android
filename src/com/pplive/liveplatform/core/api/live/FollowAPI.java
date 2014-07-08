package com.pplive.liveplatform.core.api.live;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.api.BaseURL;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.api.live.model.Follow;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.api.live.model.UserRelation;
import com.pplive.liveplatform.core.api.live.resp.MessageResp;
import com.pplive.liveplatform.core.api.live.resp.UserRelationListResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class FollowAPI extends RESTfulAPI {

    static final String TAG = FollowAPI.class.getSimpleName();

    private static final String TEMPLATE_GET_RELATIONS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_TEST_HOST,
            "/ft/v1/follow/pptv/user/{username}/relations?userids={userids}").toString();

    private static final String TEMPLATE_FOLLOW = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_TEST_HOST, "/ft/v1/follow/pptv/user/{username}/follow")
            .toString();

    private static FollowAPI sInstance = new FollowAPI();

    public static final FollowAPI getInstance() {
        return sInstance;
    }

    private FollowAPI() {
    }

    public List<UserRelation> getRelations(String coToken, String username, List<User> users) throws LiveHttpException {
        if (null == users || 0 == users.size()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < users.size(); ++i) {
            User user = users.get(i);

            sb.append(String.format(i == 0 ? "%d" : ", %d", user.getId()));
        }

        return getRelations(coToken, username, sb.toString());
    }

    public List<UserRelation> getRelations(String coToken, String username, String userids) throws LiveHttpException {
        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mHttpHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<String> req = new HttpEntity<String>(mHttpHeaders);

        UserRelationListResp resp = null;
        try {
            HttpEntity<UserRelationListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_RELATIONS, HttpMethod.GET, req, UserRelationListResp.class, username,
                    userids);

            resp = rep.getBody();
            if (0 == resp.getError()) {
                return resp.getList();
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

    public boolean follow(String coToken, String username, long id) throws LiveHttpException {
        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mHttpHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<Follow> req = new HttpEntity<Follow>(new Follow(new long[] { id }, null), mHttpHeaders);

        MessageResp resp = null;
        try {
            resp = mRestTemplate.postForObject(TEMPLATE_FOLLOW, req, MessageResp.class, username);

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
