package com.pplive.liveplatform.core.service.live;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.auth.LiveTokenAuthentication;
import com.pplive.liveplatform.core.service.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.LiveAlive;
import com.pplive.liveplatform.core.service.live.model.LiveStatus;
import com.pplive.liveplatform.core.service.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.resp.LiveAliveResp;
import com.pplive.liveplatform.core.service.live.resp.MessageResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class LiveControlService extends RestService {

    private static final String TAG = LiveControlService.class.getSimpleName();

    private static final String TEMPLATE_UPDATE_LIVE_STATUS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/c/v2/pptv/program/{pid}/livestatus")
            .toString();

    private static final String TEMPLATE_KEEP_LIVE_ALIVE = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/c/v1/pptv/program/{pid}/livealive")
            .toString();

    private static final LiveControlService sInstance = new LiveControlService();

    public static final LiveControlService getInstance() {
        return sInstance;
    }

    private LiveControlService() {
    }

    public void updateLiveStatusByCoTokenAsync(final Context context, final Program program, final LiveStatusEnum status) {

        String username = UserManager.getInstance(context).getUsernamePlain();
        String coToken = UserManager.getInstance(context).getToken();

        updateLiveStatusByCoTokenAsync(coToken, program, username, status);
    }

    private void updateLiveStatusByCoTokenAsync(final String coToken, final Program program, final String username, final LiveStatusEnum status) {

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {

                for (int i = 0; i < 10; ++i) {

                    try {

                        String liveToken = TokenService.getInstance().getLiveToken(coToken, program.getId(), username);

                        if (!TextUtils.isEmpty(liveToken) && updateLiveStatusByLiveToken(liveToken, program, status)) {
                            return true;
                        }

                        TimeUnit.MILLISECONDS.sleep(500);

                    } catch (InterruptedException e) {
                        Log.w(TAG, e.toString());
                    } catch (LiveHttpException e) {
                        Log.w(TAG, e.toString());
                    }

                }

                return false;
            }
        };

        task.execute();
    }

    public void updateLiveStatusByCoTokenAsync(final Context context, final Program program) {
        String username = UserManager.getInstance(context).getUsernamePlain();
        String coToken = UserManager.getInstance(context).getToken();

        updateLiveStatusByCoTokenAsync(coToken, program, username);
    }

    private void updateLiveStatusByCoTokenAsync(final String coToken, final Program program, final String username) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {

                for (int i = 0; i < 10; ++i) {

                    try {

                        if (updateLiveStatusByCoToken(coToken, program, username)) {
                            return true;
                        }

                        TimeUnit.MILLISECONDS.sleep(500);

                    } catch (InterruptedException e) {
                        Log.w(TAG, e.toString());
                    } catch (LiveHttpException e) {
                        Log.w(TAG, e.toString());
                    }

                }

                return false;
            }
        };

        task.execute();
    }

    public boolean updateLiveStatusByCoToken(String coToken, Program program, String username) throws LiveHttpException {
        String liveToken = TokenService.getInstance().getLiveToken(coToken, program.getId(), username);

        return updateLiveStatusByLiveToken(liveToken, program);
    }

    public boolean updateLiveStatusByLiveToken(String liveToken, Program program) throws LiveHttpException {
        LiveStatusEnum status = program.getLiveStatus();

        if (null != status.nextStatus()) {

            return updateLiveStatusByLiveToken(liveToken, program, status.nextStatus());
        }

        throw new LiveHttpException();
    }

    private boolean updateLiveStatusByLiveToken(String liveToken, Program program, LiveStatusEnum livestatus) throws LiveHttpException {
        Log.d(TAG, "pid: " + program.getId() + "; livestatus: " + livestatus);

        mHttpHeaders.setAuthorization(new LiveTokenAuthentication(liveToken));
        HttpEntity<?> req = new HttpEntity<LiveStatus>(new LiveStatus(livestatus), mHttpHeaders);

        MessageResp resp = null;
        try {

            resp = mRestTemplate.postForObject(TEMPLATE_UPDATE_LIVE_STATUS, req, MessageResp.class, program.getId());

            if (0 == resp.getError()) {
                program.setLiveStatus(livestatus);
                return true;
            } else {
                return false;
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

    public LiveAlive keepLiveAlive(String coToken, long pid) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid);

        mHttpHeaders.setAuthorization(new UserTokenAuthentication(coToken));
        HttpEntity<?> req = new HttpEntity<String>(mHttpHeaders);

        LiveAliveResp resp = null;
        try {

            HttpEntity<LiveAliveResp> rep = mRestTemplate.exchange(TEMPLATE_KEEP_LIVE_ALIVE, HttpMethod.GET, req, LiveAliveResp.class, pid);

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
}
