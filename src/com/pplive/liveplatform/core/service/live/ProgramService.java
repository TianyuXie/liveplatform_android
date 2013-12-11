package com.pplive.liveplatform.core.service.live;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.live.auth.UserTokenAuthentication;
import com.pplive.liveplatform.core.service.live.model.LiveStatusEnum;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.resp.ProgramListResp;
import com.pplive.liveplatform.core.service.live.resp.ProgramResp;
import com.pplive.liveplatform.core.service.live.resp.Resp;
import com.pplive.liveplatform.util.URLEncoderUtil;
import com.pplive.liveplatform.util.URL.Protocol;

public class ProgramService extends RestService {

    private static final String TAG = ProgramService.class.getSimpleName();

    private static final String TEMPLATE_GET_PROGRAMS = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST,
            "/ft/v1/owner/{owner}/programs?livestatus={livestatus}").toString();

    private static final String TEMPLATE_CREATE_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/ft/v1/program").toString();

    private static final String TEMPLATE_UPDATE_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/ft/v1/program/{programid}/info").toString();

    private static final String TEMPLATE_DELETE_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST, "/ft/v1/program/{programid}").toString();

    private static ProgramService sInstance = new ProgramService();

    public static ProgramService getInstance() {
        return sInstance;
    }

    private ProgramService() {
    }

    public List<Program> getProgramsByOwner(String owner) {
        return getProgramsByOwner(owner, "");
    }

    public List<Program> getProgramsByOwner(String owner, LiveStatusEnum livestatus) {

        return getProgramsByOwner(owner, livestatus.toString());
    }

    private List<Program> getProgramsByOwner(String owner, String liveStatus) {
        Log.d(TAG, "owner: " + owner + "; livestatus: " + liveStatus);

        ProgramListResp rep = mRestTemplate.getForObject(TEMPLATE_GET_PROGRAMS, ProgramListResp.class, URLEncoderUtil.encode(owner), liveStatus);

        return rep.getList();
    }

    public Program getProgramById() {

        return null;
    }

    public Program createProgram(String coToken, Program program) {
        Log.d(TAG, program.toString());
        
        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mRequestHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<?> req = new HttpEntity<Program>(program, mRequestHeaders);

        ProgramResp resp = mRestTemplate.postForObject(TEMPLATE_CREATE_PROGRAM.toString(), req, ProgramResp.class);

        return resp.getData();
    }

    public void updateProgram(String coToken, Program program) {
        Log.d(TAG, program.toString());

        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mRequestHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<Program> req = new HttpEntity<Program>(program, mRequestHeaders);

        mRestTemplate.postForObject(TEMPLATE_UPDATE_PROGRAM, req, Resp.class, program.getId());
    }

    public void deleteProgramById(String coToken, long pid) {
        Log.d(TAG, "pid: " + pid);

        UserTokenAuthentication coTokenAuthentication = new UserTokenAuthentication(coToken);
        mRequestHeaders.setAuthorization(coTokenAuthentication);
        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);

        mRestTemplate.exchange(TEMPLATE_DELETE_PROGRAM, HttpMethod.DELETE, req, Resp.class, pid);
    }
}
