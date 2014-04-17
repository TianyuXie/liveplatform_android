package com.pplive.liveplatform.core.service.passport;

import java.net.URI;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.GsonHttpMessageConverterEx;
import com.pplive.liveplatform.core.service.RestTemplateFactory;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.passport.model.CheckCode;
import com.pplive.liveplatform.core.service.passport.model.LoginResult;
import com.pplive.liveplatform.core.service.passport.resp.GuidResp;
import com.pplive.liveplatform.core.service.passport.resp.LoginResultResp;
import com.pplive.liveplatform.core.service.passport.resp.MessageResp;
import com.pplive.liveplatform.util.ThreeDESUtil;
import com.pplive.liveplatform.util.ThreeDESUtil.EncryptException;
import com.pplive.liveplatform.util.URL.Protocol;
import com.pplive.liveplatform.util.URLUtil;

public class PassportService {

    private static final String TAG = PassportService.class.getSimpleName();

    private static final String TEMPLATE_PASSPORT_LOGIN = new BaseURL(Protocol.HTTPS, Constants.PASSPORT_API_HOST,
            "/v3/login/login.do?username={username}&password={password}&format=json").toString();

    private static final String TEMPLATE_PASSPORT_THIRDPARTY_LOGIN = new BaseURL(Protocol.HTTPS, Constants.PASSPORT_API_HOST,
            "/v3/register/thirdparty_simple.do?infovalue={infovalue}&apptype={apptype}&index={index}&format=json").toString();

    private static final String TEMPLATE_PASSPORT_REGISTER = new BaseURL(Protocol.HTTP, Constants.PASSPORT_API_HOST,
            "/v3/register/username.do?username={username}&password={password}&usermail={usermail}&checkcode={checkcode}&guid={guid}&format=json").toString();

    private static final String TEMPLATE_PASSPORT_GET_GUID = new BaseURL(Protocol.HTTP, Constants.PASSPORT_API_HOST, "/v3/checkcode/guid.do?format=json")
            .toString();

    private static final String TEMPLATE_PASSPORT_GET_GUID_IMAGE = new BaseURL(Protocol.HTTP, Constants.PASSPORT_API_HOST, "/v3/checkcode/image.do?guid={guid}")
            .toString();

    private static final PassportService sInstance = new PassportService();

    public static PassportService getInstance() {
        return sInstance;
    }

    private final RestTemplate mRestTemplate;
    
    private final HttpHeaders mHttpHeaders;

    private PassportService() {
        mRestTemplate = RestTemplateFactory.newInstance();
        mRestTemplate.getMessageConverters().add(new GsonHttpMessageConverterEx());
        
        mHttpHeaders = new HttpHeaders();
        mHttpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    public LoginResult login(String usr, String pwd) throws LiveHttpException {
        Log.d(TAG, "user: " + usr + "; password: " + pwd);

        HttpEntity<String> entity = new HttpEntity<String>(mHttpHeaders);

        LoginResultResp resp = null;
        try {
            HttpEntity<LoginResultResp> rep = mRestTemplate.exchange(TEMPLATE_PASSPORT_LOGIN, HttpMethod.GET, entity, LoginResultResp.class, usr, pwd);

            resp = rep.getBody();

            if (0 == resp.getErrorCode()) {
                return resp.getResult();
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null != resp) {
            throw new LiveHttpException(resp.getErrorCode(), URLUtil.decode(resp.getMessage()));
        } else {
            throw new LiveHttpException();
        }
    }

    public LoginResult thirdpartyRegister(String id, String faceUrl, String nickName, String apptype) throws LiveHttpException {

        HttpEntity<String> entity = new HttpEntity<String>(mHttpHeaders);

        String infovalue = null;
        Random random = new Random();
        int keyIndex = random.nextInt(10) + 1;
        try {
            infovalue = String.format("%s&%s&%s", URLUtil.encode(id), URLUtil.encode(faceUrl), URLUtil.encode(nickName));

            infovalue = URLUtil.encode(ThreeDESUtil.encode(infovalue, keyIndex));
        } catch (EncryptException e) {
            Log.w(TAG, e.toString());
        }

        Log.d(TAG, "infovalue: " + infovalue);

        String index = String.format(Locale.getDefault(), "%02d", keyIndex);

        UriComponents components = UriComponentsBuilder.fromUriString(TEMPLATE_PASSPORT_THIRDPARTY_LOGIN).buildAndExpand(infovalue, apptype, index);

        URI uri = URI.create(components.toString());

        LoginResultResp resp = null;
        try {
            HttpEntity<LoginResultResp> rep = mRestTemplate.exchange(uri, HttpMethod.GET, entity, LoginResultResp.class);

            resp = rep.getBody();

            if (0 == resp.getErrorCode()) {
                return resp.getResult();
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null != resp) {
            throw new LiveHttpException(resp.getErrorCode());
        } else {
            throw new LiveHttpException();
        }
    }

    public boolean register(String username, String password, String email, String checkCode, String guid) throws LiveHttpException {

        MessageResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_PASSPORT_REGISTER, MessageResp.class, username, password, email, checkCode, guid);

            if (0 == resp.getErrorCode()) {

                return true;
            }
        } catch (Exception e) {

        }

        if (null != resp) {
            throw new LiveHttpException(resp.getErrorCode(), URLUtil.decode(resp.getMessage()));
        } else {
            throw new LiveHttpException();
        }
    }

    public String getCheckCodeGUID() throws LiveHttpException {

        GuidResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_PASSPORT_GET_GUID, GuidResp.class);

            if (0 == resp.getErrorCode()) {
                return resp.getResult();
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null != resp) {
            throw new LiveHttpException(resp.getErrorCode());
        } else {
            throw new LiveHttpException();
        }
    }

    public String getCheckCodeImageUrl(String guid) {

        UriComponents components = UriComponentsBuilder.fromUriString(TEMPLATE_PASSPORT_GET_GUID_IMAGE).buildAndExpand(guid);

        return components.toString();
    }

    public CheckCode getCheckCode() throws LiveHttpException {
        String guid = getCheckCodeGUID();

        String image_url = getCheckCodeImageUrl(guid);

        return new CheckCode(guid, image_url);
    }
}
