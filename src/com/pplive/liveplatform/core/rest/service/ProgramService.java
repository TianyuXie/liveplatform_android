package com.pplive.liveplatform.core.rest.service;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.rest.Program;
import com.pplive.liveplatform.core.rest.ProgramListResp;
import com.pplive.liveplatform.core.rest.ProgramResp;
import com.pplive.liveplatform.core.rest.http.CoTkAuthentication;
import com.pplive.liveplatform.core.rest.http.Url;

public class ProgramService extends AbsService{

    @SuppressWarnings("unused")
    private static final String TAG = ProgramService.class.getSimpleName();

    private static final Url GET_PROGRAMS_URL = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/ft/v1/owner/{owner}/programs");
    
    private static final Url CREATE_PROGRAM_URL = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/ft/v1/program");

    private static final Url DELETE_PROGRAM_URL = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/ft/v1/program/{programid}");

    private static ProgramService sInstance = new ProgramService();

    public static ProgramService getInstance() {
        return sInstance;
    }

    private ProgramService() {
        mRequestHeaders = new HttpHeaders();
        mRequestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        mRequestHeaders.setAuthorization(new CoTkAuthentication("pptv", Constants.TEST_COTK));
    }
    
    public List<Program> getProgramsByLiveStatus(String livestatus) {

        return null;
    }

    public List<Program> getProgramsByOwner(String owner) {

        ProgramListResp rep = mRestTemplate.getForObject(GET_PROGRAMS_URL.toString(), ProgramListResp.class, owner);

        return rep.getList();
    }

    public Program getProgramById() {

        return null;
    }

    public Program createProgram(Program program) {
        
        HttpEntity<Program> req = new HttpEntity<Program>(program, mRequestHeaders);
        
        ProgramResp resp = mRestTemplate.postForObject(CREATE_PROGRAM_URL.toString(), req, ProgramResp.class);
        
        return resp.getProgram();
    }

    public Program updateProgram(Program program) {

        return null;
    }

    public void deleteProgramById(long id) {

    }
}
