package com.pplive.liveplatform.core.rest.service;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.rest.Protocol;
import com.pplive.liveplatform.core.rest.URL;
import com.pplive.liveplatform.core.rest.model.FallList;
import com.pplive.liveplatform.core.rest.model.Program;
import com.pplive.liveplatform.core.rest.resp.ProgramFallListResp;

public class SearchService extends AbsService {

    private static final String TAG = SearchService.class.getSimpleName();

    private static final SearchService sInstance = new SearchService();

    private static final String TEMPLATE_SEARCH_PROGRAM = new URL(Protocol.HTTP, Constants.LIVEPLATFORM_API_HOST,
            "/search/v1/pptv/searchcommon?key={keywords}&subjectid={subjectid}&sort={sort}&livestatus={livestatus}&nexttk={nexttk}&fallcount={fallcount}")
            .toString();

    public static SearchService getInstance() {
        return sInstance;
    }

    private SearchService() {

    }

    public FallList<Program> searchProgram(int subjectId, String sort, String liveStatus, String nextToken, int fallCount) {

        return searchProgram("" /* keywords */, subjectId, sort, liveStatus, nextToken, fallCount);
    }

    public FallList<Program> searchProgram(String keywords, int subjectId, String sort, String liveStatus, String nextToken, int fallCount) {
        Log.d(TAG, "keywords: " + keywords + ";subjectId: " + subjectId + "; sort: " + sort + "; liveStatus: " + liveStatus + "; nextToken: " + nextToken
                + "; fallCount: " + fallCount);

        ProgramFallListResp rep = mRestTemplate.getForObject(TEMPLATE_SEARCH_PROGRAM, ProgramFallListResp.class, keywords, subjectId, sort, liveStatus,
                nextToken, fallCount);

        return rep.getFallList();
    }
}
