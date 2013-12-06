package com.pplive.liveplatform.core.service.live;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.URL.Protocol;
import com.pplive.liveplatform.core.service.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.service.live.resp.TokenResp;

public class TokenService extends RestService {

    private static final String TAG = TokenService.class.getSimpleName();

    private static final String TEMPLATE_GET_TOKEN = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST,
            "/tk/v1/{tokentype}/{programid}?user={username}&expiretime={expiretime}").toString();

    private static final TokenService sInstance = new TokenService();

    public static TokenService getInstance() {
        return sInstance;
    }

    private TokenService() {

    }

    public String getLiveToken(long pid, String username) {
        return getLiveToken(pid, username, 600 /* second */);
    }

    public String getLiveToken(long pid, String username, int expiretime) {
        return getToken(TokenType.LIVE, pid, username, expiretime);
    }

    public String getPlayToken(long pid, String username) {
        return getPlayToken(pid, username, 600 /* second */);
    }

    public String getPlayToken(long pid, String username, int expiretime) {
        return getToken(TokenType.PLAY, pid, username, expiretime);
    }

    private String getToken(TokenType type, long pid, String username, int expiretime) {
        Log.d(TAG, "TokenType: " + type + "; pid: " + pid + "; username: " + username + "; expiretime: " + expiretime);

        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(Constants.TEST_COTK);
        mRequestHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);

        ResponseEntity<TokenResp> rep = mRestTemplate.exchange(TEMPLATE_GET_TOKEN, HttpMethod.GET, req, TokenResp.class, type, pid, username, expiretime);
        TokenResp body = rep.getBody();

        return body.getData();
    }

    enum TokenType {
        LIVE {
            @Override
            public String toString() {
                return "live";
            }
        },
        PLAY {
            @Override
            public String toString() {
                return "play";
            }
        };
    }
}
