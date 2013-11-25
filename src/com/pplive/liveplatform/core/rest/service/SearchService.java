package com.pplive.liveplatform.core.rest.service;

import java.util.List;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.rest.http.Url;
import com.pplive.liveplatform.core.rest.model.Program;
import com.pplive.liveplatform.core.rest.resp.ProgramFallListResp;

public class SearchService extends AbsService {

    @SuppressWarnings("unused")
    private static final String TAG = SearchService.class.getSimpleName();

    private static final SearchService sInstance = new SearchService();
    
    private static final String TEMPLATE_SEARCH_PROGRAM = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/search/v1/pptv/searchcommon?subjectid={subjectid}&sort={sort}&livestatus={livestatus}&nexttk={nexttk}&fallcount={fallcount}").toString();

    public static SearchService getInstance() {
        return sInstance;
    }

    private SearchService() {

    }

    public List<Program> searchProgram(int subjectId, String sort) {
        return searchProgram(subjectId, sort, "living" /* liveStatus */, "" /* nextTk */, 10 /* fallCount */);
    }

    public List<Program> searchProgram(int subjectId, String sort, String liveStatus, String nextTk, int fallCount) {
        
        ProgramFallListResp rep = mRestTemplate.getForObject(TEMPLATE_SEARCH_PROGRAM, ProgramFallListResp.class, subjectId, sort, liveStatus, nextTk, fallCount);
        
        return rep.getList();
    }
}
