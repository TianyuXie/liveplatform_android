package com.pplive.liveplatform.core.service.live;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.service.live.resp.MessageResp;
import com.pplive.liveplatform.core.service.live.resp.UserResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class UserService extends RestService {

    private static final String TAG = UserService.class.getSimpleName();

    private static final String TEMPLATE_GET_USER_INFO = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/user/v1/pptv/{username}/info")
            .toString();

    private static final String TEMPLATE_UPDATE_USER_INFO = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/user/v1/pptv/{username}/info")
            .toString();

    private static final UserService sInstance = new UserService();

    public static final UserService getInstance() {
        return sInstance;
    }

    private UserService() {

    }

    public User getUserInfo(String coToken, String username) throws LiveHttpException {
        Log.d(TAG, "username: " + username);

        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mRequestHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<String> req = new HttpEntity<String>(mRequestHeaders);

        UserResp resp = null;
        try {
            HttpEntity<UserResp> rep = mRestTemplate.exchange(TEMPLATE_GET_USER_INFO, HttpMethod.GET, req, UserResp.class, username);
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

    public boolean updateOrCreateUser(String coToken, User user) throws LiveHttpException {
        Log.d(TAG, "username: " + user.getUsername() + "; nickname: " + user.getNickname());

        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mRequestHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<User> req = new HttpEntity<User>(user, mRequestHeaders);

        MessageResp resp = null;
        try {
            ResponseEntity<MessageResp> rep = mRestTemplate.exchange(TEMPLATE_UPDATE_USER_INFO, HttpMethod.POST, req, MessageResp.class, user.getUsername());
            resp = rep.getBody();

            if (0 == resp.getError()) {
                return true;
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null != resp) {
            throw new LiveHttpException(resp.getError(), resp.getData());
        } else {
            throw new LiveHttpException();
        }
    }
}
