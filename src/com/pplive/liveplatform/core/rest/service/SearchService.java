package com.pplive.liveplatform.core.rest.service;

import java.util.List;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.rest.Program;
import com.pplive.liveplatform.core.rest.ProgramPageListResp;
import com.pplive.liveplatform.core.rest.http.Url;

public class SearchService extends AbsService {

    @SuppressWarnings("unused")
    private static final String TAG = SearchService.class.getSimpleName();

    private static final SearchService sInstance = new SearchService();
    
    private static final Url SEARCH_PROGRAM_URL = new Url(Url.Schema.HTTP, Constants.TEST_HOST, Constants.TEST_PORT, "/search/v1/pptv/searchcommon?subjectid={subjectid}&sort={sort}&livestatus={livestatus}&nexttk={nexttk}&fallcount={fallcount}");

    public static SearchService getInstance() {
        return sInstance;
    }

    private SearchService() {

    }

    public List<Program> searchProgram(int subjectId, String sort) {
        return searchProgram(subjectId, sort, "living" /* liveStatus */, "" /* nextTk */, 10 /* fallCount */);
    }

    public List<Program> searchProgram(int subjectId, String sort, String liveStatus, String nextTk, int fallCount) {
        
        ProgramPageListResp rep = mRestTemplate.getForObject(SEARCH_PROGRAM_URL.toString(), ProgramPageListResp.class, subjectId, sort, liveStatus, nextTk, fallCount);
        
        return rep.getList();
    }
}
