package com.pplive.liveplatform.core.rest.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.rest.Program;
import com.pplive.liveplatform.core.rest.http.Url;
import com.pplive.liveplatform.core.rest.resp.ProgramListResp;
import com.pplive.liveplatform.core.rest.resp.ProgramResp;
import com.pplive.liveplatform.core.rest.resp.Resp;

public class ProgramService extends AbsService{

    @SuppressWarnings("unused")
    private static final String TAG = ProgramService.class.getSimpleName();

    private static final Url GET_PROGRAMS_URL = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/ft/v1/owner/{owner}/programs");
    
    private static final Url CREATE_PROGRAM_URL = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/ft/v1/program");
    
    private static final Url UPDATE_PROGRAM_URL = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/ft/v1/program/{programid}/info");

    private static final Url DELETE_PROGRAM_URL = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/ft/v1/program/{programid}");

    private static ProgramService sInstance = new ProgramService();

    public static ProgramService getInstance() {
        return sInstance;
    }

    private ProgramService() {
    }
    
    public List<Program> getProgramsByOwner(String owner) {

        ProgramListResp rep = mRestTemplate.getForObject(GET_PROGRAMS_URL.toString(), ProgramListResp.class, owner);

        return rep.getList();
    }

    public Program getProgramById() {

        return null;
    }

    public Program createProgram(Program program) {
        Log.d(TAG, program.toString());
        
        HttpEntity<Program> req = new HttpEntity<Program>(program, mRequestHeaders);
        
        ProgramResp resp = mRestTemplate.postForObject(CREATE_PROGRAM_URL.toString(), req, ProgramResp.class);
        
        return resp.getData();
    }

    public void updateProgram(Program program) {
        HttpEntity<Program> req = new HttpEntity<Program>(program, mRequestHeaders);
        
        mRestTemplate.postForObject(UPDATE_PROGRAM_URL.toString(), req, Resp.class, program.getId());
    }

    public void deleteProgramById(long id) {
        
        HttpEntity<?> req = new HttpEntity<String>(mRequestHeaders);
        mRestTemplate.exchange(DELETE_PROGRAM_URL.toString(), HttpMethod.DELETE, req, Resp.class, id);
    }
}
