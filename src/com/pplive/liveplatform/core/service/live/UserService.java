package com.pplive.liveplatform.core.service.live;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.service.live.resp.MessageResp;
import com.pplive.liveplatform.core.service.live.resp.UserResp;
import com.pplive.liveplatform.util.URL.Protocol;
import com.pplive.liveplatform.util.URLEncoderUtil;

public class UserService extends RestService {

    private static final String TAG = UserService.class.getSimpleName();

    private static final String TEMPLATE_GET_USER_INFO = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/user/v1/pptv/{username}").toString();
    
    private static final String TEMPLATE_UPDATE_USER_INFO = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/user/v1/pptv/{username}").toString();

    private static final UserService sInstance = new UserService();

    public static final UserService getInstance() {
        return sInstance;
    }

    private UserService() {

    }

    public User getUserInfo(String coToken, String username) {
        Log.d(TAG, "username: " + username);

        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mRequestHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<String> req = new HttpEntity<String>(mRequestHeaders);

        HttpEntity<UserResp> rep = mRestTemplate.exchange(TEMPLATE_GET_USER_INFO, HttpMethod.GET, req, UserResp.class, URLEncoderUtil.encode(username));

        UserResp body = rep.getBody();
        
        return body.getData();
    }
    
    public User updateOrCreateUser(String coToken, User user) {
        Log.d(TAG, "username: " + user.getUsername() + "; nickname: " + user.getNickname());
        
        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mRequestHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<User> req = new HttpEntity<User>(user, mRequestHeaders);
        
        mRestTemplate.exchange(TEMPLATE_UPDATE_USER_INFO, HttpMethod.POST, req, MessageResp.class, user.getUsername());
        
        return user;
    }
}
