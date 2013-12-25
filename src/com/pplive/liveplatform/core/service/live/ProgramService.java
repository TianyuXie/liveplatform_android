package com.pplive.liveplatform.core.service.live;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.LiveStatus;
import com.pplive.liveplatform.core.service.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.resp.LiveStatusResp;
import com.pplive.liveplatform.core.service.live.resp.ProgramListResp;
import com.pplive.liveplatform.core.service.live.resp.ProgramResp;
import com.pplive.liveplatform.core.service.live.resp.MessageResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class ProgramService extends RestService {

    private static final String TAG = ProgramService.class.getSimpleName();

    private static final String TEMPLATE_GET_PROGRAMS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST,
            "/ft/v1/owner/{owner}/programs?livestatus={livestatus}").toString();

    private static final String TEMPLATE_CREATE_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/ft/v1/program").toString();

    private static final String TEMPLATE_UPDATE_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/ft/v1/program/{programid}/info")
            .toString();

    private static final String TEMPLATE_DELETE_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/ft/v1/program/{programid}").toString();

    private static final String TEMPLATE_GET_LIVESTATUS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/ft/v1/program/{programid}/livestatus")
            .toString();

    private static ProgramService sInstance = new ProgramService();

    public static ProgramService getInstance() {
        return sInstance;
    }

    private ProgramService() {
    }

    public List<Program> getProgramsByOwner(String owner) throws LiveHttpException {
        return getProgramsByOwner(owner, "");
    }

    public List<Program> getProgramsByOwner(String owner, LiveStatusEnum livestatus) throws LiveHttpException {

        return getProgramsByOwner(owner, livestatus.toString());
    }

    private List<Program> getProgramsByOwner(String owner, String liveStatus) throws LiveHttpException {
        Log.d(TAG, "owner: " + owner + "; livestatus: " + liveStatus);

        ProgramListResp resp = null;
        try {

            resp = mRestTemplate.getForObject(TEMPLATE_GET_PROGRAMS, ProgramListResp.class, owner, liveStatus);

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

    public Program createProgram(String coToken, Program program) throws LiveHttpException {
        Log.d(TAG, program.toString());

        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mRequestHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<Program> req = new HttpEntity<Program>(program, mRequestHeaders);

        ProgramResp resp = null;
        try {

            resp = mRestTemplate.postForObject(TEMPLATE_CREATE_PROGRAM, req, ProgramResp.class);

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

    public boolean updateProgram(String coToken, Program program) throws LiveHttpException {
        Log.d(TAG, program.toString());

        MessageResp resp = null;
        try {
            UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
            mRequestHeaders.setAuthorization(coTokenAuthentication);
            HttpEntity<Program> req = new HttpEntity<Program>(program, mRequestHeaders);

            resp = mRestTemplate.postForObject(TEMPLATE_UPDATE_PROGRAM, req, MessageResp.class, program.getId());

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

    public boolean deleteProgramById(String coToken, long pid) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid);

        MessageResp resp = null;
        try {
            UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
            mRequestHeaders.setAuthorization(coTokenAuthentication);
            HttpEntity<String> req = new HttpEntity<String>(mRequestHeaders);

            ResponseEntity<MessageResp> rep = mRestTemplate.exchange(TEMPLATE_DELETE_PROGRAM, HttpMethod.DELETE, req, MessageResp.class, pid);

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

    public LiveStatus getLiveStatus(long pid) throws LiveHttpException {
        Log.d(TAG, "pid: " + pid);
        
        LiveStatusResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_GET_LIVESTATUS, LiveStatusResp.class, pid);
            
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
