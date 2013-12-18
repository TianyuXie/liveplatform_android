package com.pplive.liveplatform.core.service.live;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.live.model.FallList;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.resp.ProgramFallListResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class SearchService extends RestService {

    private static final String TAG = SearchService.class.getSimpleName();

    private static final SearchService sInstance = new SearchService();

    private static final String TEMPLATE_SEARCH_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_CDN_HOST,
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
