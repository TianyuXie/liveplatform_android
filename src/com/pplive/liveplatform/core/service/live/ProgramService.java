package com.pplive.liveplatform.core.service.live;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.LiveStatus;
import com.pplive.liveplatform.core.service.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.model.Subject;
import com.pplive.liveplatform.core.service.live.resp.LiveStatusResp;
import com.pplive.liveplatform.core.service.live.resp.MessageResp;
import com.pplive.liveplatform.core.service.live.resp.ProgramListResp;
import com.pplive.liveplatform.core.service.live.resp.ProgramResp;
import com.pplive.liveplatform.core.service.live.resp.SubjectListResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class ProgramService extends RestService {

    private static final String TAG = ProgramService.class.getSimpleName();

    private static final String TEMPLATE_GET_PROGRAMS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST,
            "/ft/v2/owner/{owner}/programs?livestatus={livestatus}").toString();

    private static final String TEMPLATE_CDN_GET_PROGRAMS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_CDN_HOST,
            "/ft/v2/owner/{owner}/programs?livestatus={livestatus}").toString();

    private static final String TEMPLATE_GET_UNFINISHED_PROGRAMS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_CDN_HOST,
            "/ft/v1/owner/{owner}/programs/action/finished").toString();

    private static final String TEMPLATE_CREATE_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/ft/v1/program").toString();

    private static final String TEMPLATE_UPDATE_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/ft/v1/program/{programid}/info")
            .toString();

    private static final String TEMPLATE_DELETE_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/ft/v1/program/{programid}").toString();

    private static final String TEMPLATE_GET_LIVESTATUS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/ft/v1/program/{programid}/livestatus")
            .toString();

    private static final String TEMPLATE_GET_SUBJECTS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_CDN_HOST, "/bk/subject/v2/pptv/subjects")
            .toString();

    private static ProgramService sInstance = new ProgramService();

    public static ProgramService getInstance() {
        return sInstance;
    }

    private ProgramService() {
    }

    public List<Program> getProgramsByUser(String user) throws LiveHttpException {
        return getProgramsByUser(user, null);
    }

    public List<Program> getProgramsByUser(String user, LiveStatusEnum livestatus) throws LiveHttpException {
        return getPrograms("", user, livestatus, false /* isOwner */);
    }

    public List<Program> getProgramsByOwner(String coToken, String owner) throws LiveHttpException {
        return getProgramsByOwner(coToken, owner, null);
    }

    public List<Program> getProgramsByOwner(String coToken, String owner, LiveStatusEnum livestatus) throws LiveHttpException {
        return getPrograms(coToken, owner, livestatus, true /* isOwner */);
    }

    private List<Program> getPrograms(String coToken, String owner, LiveStatusEnum livestatus, boolean isOwner) throws LiveHttpException {
        return getPrograms(coToken, owner, null != livestatus ? livestatus.toString() : "", isOwner);
    }

    private List<Program> getPrograms(String coToken, String owner, String liveStatus, boolean isOwner) throws LiveHttpException {
        Log.d(TAG, "owner: " + owner + "; livestatus: " + liveStatus);

        if (isOwner) {
            UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
            mHttpHeaders.setAuthorization(coTokenAuthentication);
        }

        HttpEntity<String> req = new HttpEntity<String>(mHttpHeaders);

        ProgramListResp resp = null;
        try {

            HttpEntity<ProgramListResp> rep = mRestTemplate.exchange(isOwner ? TEMPLATE_GET_PROGRAMS : TEMPLATE_CDN_GET_PROGRAMS, HttpMethod.GET, req,
                    ProgramListResp.class, owner, liveStatus);
            resp = rep.getBody();

            if (0 == resp.getError()) {
                return resp.getList();
            }
        } catch (HttpClientErrorException e) {
            Log.w(TAG, e.toString());

            throw new LiveHttpException(e.getStatusCode().value());
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null != resp) {
            throw new LiveHttpException(resp.getError());
        } else {
            throw new LiveHttpException();
        }
    }

    public List<Program> getUnfinishedPrograms(String coToken, String owner) throws LiveHttpException {
        Log.d(TAG, "owner: " + owner);

        HttpEntity<String> req = new HttpEntity<String>(mHttpHeaders);

        ProgramListResp resp = null;
        try {
            HttpEntity<ProgramListResp> rep = mRestTemplate.exchange(TEMPLATE_GET_UNFINISHED_PROGRAMS, HttpMethod.GET, req, ProgramListResp.class, owner);
            resp = rep.getBody();

            if (1 == resp.getError()) {
                return resp.getList();
            } else if (0 == resp.getError()) {
                return null;
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
        mHttpHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<Program> req = new HttpEntity<Program>(program, mHttpHeaders);

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
            mHttpHeaders.setAuthorization(coTokenAuthentication);
            HttpEntity<Program> req = new HttpEntity<Program>(program, mHttpHeaders);

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
            mHttpHeaders.setAuthorization(coTokenAuthentication);
            HttpEntity<String> req = new HttpEntity<String>(mHttpHeaders);

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

    public List<Subject> getSubjects() throws LiveHttpException {
        Log.d(TAG, "getSubjects");

        SubjectListResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_GET_SUBJECTS, SubjectListResp.class);

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
}
