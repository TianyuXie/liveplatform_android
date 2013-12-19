package com.pplive.liveplatform.core.service.live;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.service.live.resp.TokenResp;
import com.pplive.liveplatform.util.URL.Protocol;

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

    public String getLiveToken(String coToken, long pid, String username) throws LiveHttpException {
        return getLiveToken(coToken, pid, username, 600 /* second */);
    }

    public String getLiveToken(String coToken, long pid, String username, int expiretime) throws LiveHttpException {
        return getToken(coToken, TokenType.LIVE, pid, username, expiretime);
    }

    public String getPlayToken(String coToken, long pid, String username) throws LiveHttpException {
        return getPlayToken(coToken, pid, username, 600 /* second */);
    }

    public String getPlayToken(String coToken, long pid, String username, int expiretime) throws LiveHttpException {
        return getToken(coToken, TokenType.PLAY, pid, username, expiretime);
    }

    private String getToken(String coToken, TokenType type, long pid, String username, int expiretime) throws LiveHttpException {
        Log.d(TAG, "TokenType: " + type + "; pid: " + pid + "; username: " + username + "; expiretime: " + expiretime);
        
        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mRequestHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);
        
        TokenResp resp = null;
        try {

            ResponseEntity<TokenResp> rep = mRestTemplate.exchange(TEMPLATE_GET_TOKEN, HttpMethod.GET, req, TokenResp.class, type, pid, username, expiretime);
            resp = rep.getBody();

            return resp.getData();
        } catch (Exception e) {

            if (null != resp) {
                throw new LiveHttpException(resp.getError());
            }
        }

        throw new LiveHttpException();
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
