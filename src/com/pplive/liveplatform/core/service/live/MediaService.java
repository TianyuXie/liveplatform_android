package com.pplive.liveplatform.core.service.live;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.auth.LiveTokenAuthentication;
import com.pplive.liveplatform.core.service.live.auth.PlayTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.Push;
import com.pplive.liveplatform.core.service.live.model.Watch;
import com.pplive.liveplatform.core.service.live.model.WatchList;
import com.pplive.liveplatform.core.service.live.resp.NetSpeedResp;
import com.pplive.liveplatform.core.service.live.resp.PushResp;
import com.pplive.liveplatform.core.service.live.resp.WatchListResp;
import com.pplive.liveplatform.core.service.live.resp.WatchListResp2;
import com.pplive.liveplatform.util.URL.Protocol;

public class MediaService extends RestService {

    private static final String TAG = MediaService.class.getSimpleName();

    private static final String TEMPLATE_GET_PUSH = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST,
            "/media/v1/pptv/program/{pid}/publish?coded=true").toString();

    private static final String TEMPLATE_GET_PLAY_V1 = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/media/v1/pptv/program/{pid}/watch")
            .toString();

    private static final String TEMPLATE_GET_PLAY_V3 = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/media/v3/pptv/program/{pid}/watch")
            .toString();

    private static final String TEMPLATE_GET_PREVIEW = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/media/v1/pptv/program/{pid}/preview")
            .toString();

    private static final String TEMPLATE_NET_SPEED = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/media/v1/pptv/netspeed").toString();

    private static final MediaService sInstance = new MediaService();

    public static final MediaService getInstance() {
        return sInstance;
    }

    private MediaService() {

    }

    public Push getPush(String coToken, long pid, String username) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; username: " + username);

        String token = TokenService.getInstance().getLiveToken(coToken, pid, username);

        return getPushByLiveToken(pid, token);
    }

    public Push getPushByLiveToken(long pid, String liveToken) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; token: " + liveToken);

        mHttpHeaders.setAuthorization(new LiveTokenAuthentication(liveToken));

        HttpEntity<?> req = new HttpEntity<String>(mHttpHeaders);

        PushResp resp = null;
        try {
            HttpEntity<PushResp> rep = mRestTemplate.exchange(TEMPLATE_GET_PUSH, HttpMethod.GET, req, PushResp.class, pid);
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

    public List<Watch> getPlayWatchListV1(String coToken, long pid, String username) throws LiveHttpException {
        Log.d(TAG, "coToken:" + coToken + "; pid: " + pid + "; username: " + username);

        String token = TokenService.getInstance().getPlayToken(coToken, pid, username);

        return getPlayWatchListByPlayTokenV1(pid, token);
    }

    public List<Watch> getPlayWatchListByPlayTokenV1(long pid, String playToken) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; token: " + playToken);

        mHttpHeaders.setAuthorization(new PlayTokenAuthentication(playToken));

        HttpEntity<?> req = new HttpEntity<String>(mHttpHeaders);

        WatchListResp resp = null;
        try {
            HttpEntity<WatchListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_PLAY_V1, HttpMethod.GET, req, WatchListResp.class, pid);
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

    public WatchList getPlayWatchListV3(String coToken, long pid, String username) throws LiveHttpException {
        Log.d(TAG, "coToken: " + coToken + "; pid; " + pid + "; username: " + username);

        String playToken = TokenService.getInstance().getPlayToken(coToken, pid, username);

        return getPlayWatchListByPlayTokenV3(pid, playToken);
    }

    public WatchList getPlayWatchListByPlayTokenV3(long pid, String playToken) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; token: " + playToken);

        mHttpHeaders.setAuthorization(new PlayTokenAuthentication(playToken));

        HttpEntity<?> req = new HttpEntity<String>(mHttpHeaders);

        WatchListResp2 resp = null;
        try {
            HttpEntity<WatchListResp2> rep = mRestTemplate.exchange(TEMPLATE_GET_PLAY_V3, HttpMethod.GET, req, WatchListResp2.class, pid);
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

    public List<Watch> getPreviewWatchList(String coToken, long pid, String username) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; username: " + username);

        String token = TokenService.getInstance().getLiveToken(coToken, pid, username);

        return getPreviewWatchListByLiveToken(pid, token);
    }

    public List<Watch> getPreviewWatchListByLiveToken(long pid, String token) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid + "; token: " + token);

        mHttpHeaders.setAuthorization(new LiveTokenAuthentication(token));

        HttpEntity<?> req = new HttpEntity<String>(mHttpHeaders);

        WatchListResp resp = null;
        try {
            HttpEntity<WatchListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_PREVIEW, HttpMethod.GET, req, WatchListResp.class, pid);
            resp = rep.getBody();

            if (null != resp) {
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

    public String getClientIPAddress() {

        String url = getNetSpeedUrl();

        if (!TextUtils.isEmpty(url)) {
            String content = testNetSpeed(url, new byte[0]);

            if (!TextUtils.isEmpty(content)) {
                String[] spilts = content.split("\n");

                return spilts[1];
            }
        }

        return null;
    }

    public float getAvgNetSpeed(final int size, final int num) {
        final byte[] data = new byte[size];

        int count = 0;
        float sum = 0;
        for (int i = 0; i < num; ++i) {

            float speed = getNetSpeed(data);
            if (speed > 0) {
                sum += speed;
                count++;
            }
        }

        if (count > 0) {
            return sum / count;
        } else {
            return -1f;
        }
    }

    public float getNetSpeed(final byte[] data) {

        String url = getNetSpeedUrl();

        if (!TextUtils.isEmpty(url)) {
            String content = testNetSpeed(url, data);

            if (!TextUtils.isEmpty(content)) {
                String[] spilts = content.split("\n");

                try {
                    return Float.valueOf(spilts[0]);
                } catch (NumberFormatException e) {
                    Log.w(TAG, e.toString());
                }
            }
        }

        return -1f;
    }

    private String testNetSpeed(String url, final byte[] data) {

        if (TextUtils.isEmpty(url) || null == data) {
            throw new IllegalArgumentException();
        }

        ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new NameValuePair() {

            @Override
            public String getValue() {
                return new String(data);
            }

            @Override
            public String getName() {
                return "data";
            }
        });

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
            HttpPost post = new HttpPost(url);
            post.setEntity(entity);

            HttpParams params = new BasicHttpParams();

            params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000 /* millisecond */);

            post.setParams(params);

            HttpClient client = new DefaultHttpClient();

            HttpResponse rep = client.execute(post);

            int status = rep.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK && null != rep.getEntity()) {

                InputStream in = rep.getEntity().getContent();
                InputStreamReader r = new InputStreamReader(in);

                char[] buff = new char[100];

                StringBuilder sb = new StringBuilder();
                for (int numOfBytes = -1; (numOfBytes = r.read(buff)) > 0;) {
                    sb.append(buff, 0, numOfBytes);
                }

                Log.d(TAG, sb.toString());

                return sb.toString();
            }

        } catch (Exception e) {

            Log.w(TAG, e.toString());
        }

        return null;
    }

    private String getNetSpeedUrl() {

        NetSpeedResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_NET_SPEED, NetSpeedResp.class);

            if (null != resp && 0 == resp.getError()) {
                return resp.getData();
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        return null;
    }

}
