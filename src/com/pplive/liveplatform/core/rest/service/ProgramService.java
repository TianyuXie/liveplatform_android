package com.pplive.liveplatform.core.rest.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.rest.Protocol;
import com.pplive.liveplatform.core.rest.URL;
import com.pplive.liveplatform.core.rest.model.Program;
import com.pplive.liveplatform.core.rest.resp.ProgramListResp;
import com.pplive.liveplatform.core.rest.resp.ProgramResp;
import com.pplive.liveplatform.core.rest.resp.Resp;

public class ProgramService extends AbsService {

    private static final String TAG = ProgramService.class.getSimpleName();

    private static final String TEMPLATE_GET_PROGRAMS = new URL(Protocol.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/ft/v1/owner/{owner}/programs")
            .toString();

    private static final String TEMPLATE_CREATE_PROGRAM = new URL(Protocol.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/ft/v1/program").toString();

    private static final String TEMPLATE_UPDATE_PROGRAM = new URL(Protocol.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/ft/v1/program/{programid}/info")
            .toString();

    private static final String TEMPLATE_DELETE_PROGRAM = new URL(Protocol.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/ft/v1/program/{programid}")
            .toString();

    private static ProgramService sInstance = new ProgramService();

    public static ProgramService getInstance() {
        return sInstance;
    }

    private ProgramService() {
    }

    public List<Program> getProgramsByOwner(String owner) {

        ProgramListResp rep = mRestTemplate.getForObject(TEMPLATE_GET_PROGRAMS, ProgramListResp.class, owner);

        return rep.getList();
    }

    public Program getProgramById() {

        return null;
    }

    public Program createProgram(Program program) {
        Log.d(TAG, program.toString());
        
        mRequestHeaders.setAuthorization(mCoTokenAuthentication);
        HttpEntity<?> req = new HttpEntity<Program>(program, mRequestHeaders);

        ProgramResp resp = mRestTemplate.postForObject(TEMPLATE_CREATE_PROGRAM.toString(), req, ProgramResp.class);

        return resp.getData();
    }

    public void updateProgram(Program program) {
        Log.d(TAG, program.toString());
        
        mRequestHeaders.setAuthorization(mCoTokenAuthentication);
        HttpEntity<Program> req = new HttpEntity<Program>(program, mRequestHeaders);

        mRestTemplate.postForObject(TEMPLATE_UPDATE_PROGRAM, req, Resp.class, program.getId());
    }

    public void deleteProgramById(long pid) {
        Log.d(TAG, "pid: " + pid);
        
        mRequestHeaders.setAuthorization(mCoTokenAuthentication);
        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);
        
        mRestTemplate.exchange(TEMPLATE_DELETE_PROGRAM, HttpMethod.DELETE, req, Resp.class, pid);
    }
}
