package com.pplive.liveplatform.core.service.live;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.model.FallList;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.resp.ProgramFallListResp;
import com.pplive.liveplatform.core.service.live.resp.SearchWordsListResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class SearchService extends RestService {

    private static final String TAG = SearchService.class.getSimpleName();

    private static final SearchService sInstance = new SearchService();

    private static final String TEMPLATE_SEARCH_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_CDN_HOST,
            "/search/v2/pptv/searchcommon?key={keywords}&tag={tag}&subjectid={subjectid}&sort={sort}&livestatus={livestatus}&nexttk={nexttk}&fallcount={fallcount}")
            .toString();

    private static final String TEMPLATE_SEARCH_USER = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_TEST_HOST,
            "/search/user/v2/pptv/usersearch?key={key}&fallcount={fallcount}&nexttk={nexttk}").toString();

    private static final String TEMPLATE_GET_RECOMMEND_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_CDN_HOST,
            "/search/v2/c/pptv/recommend/program").toString();

    private static final String TEMPLATE_GET_SEARCH_WORD = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_CDN_HOST,
            "/search/v2/c/pptv/recommend/searchwords").toString();

    public static SearchService getInstance() {
        return sInstance;
    }

    private SearchService() {

    }

    public FallList<Program> searchProgram(int subjectId, SortKeyword sort, LiveStatusKeyword liveStatus, String nextToken, int fallCount)
            throws LiveHttpException {

        return searchProgram("" /* keywords */, subjectId, sort, liveStatus, nextToken, fallCount);
    }

    public FallList<Program> searchProgram(String keywords, SortKeyword sort, LiveStatusKeyword liveStatus, String nextToken, int fallCount)
            throws LiveHttpException {

        return searchProgram(keywords, -1 /* subjectId */, sort, liveStatus, nextToken, fallCount);
    }

    public FallList<Program> searchProgram(String keywords, int subjectId, SortKeyword sort, LiveStatusKeyword liveStatus, String nextToken, int fallCount)
            throws LiveHttpException {

        return searchProgram(keywords, "" /* tag */, subjectId, sort, liveStatus, nextToken, fallCount);
    }

    public FallList<Program> searchProgram(String keywords, String tag, int subjectId, SortKeyword sort, LiveStatusKeyword liveStatus, String nextToken,
            int fallCount) throws LiveHttpException {

        return searchProgram(keywords, tag, subjectId >= 0 ? String.valueOf(subjectId) : "", null != sort ? sort.toString() : "",
                null != liveStatus ? liveStatus.toString() : "", nextToken, fallCount);
    }

    private FallList<Program> searchProgram(String keywords, String tag, String subjectId, String sort, String liveStatus, String nextToken, int fallCount)
            throws LiveHttpException {
        Log.d(TAG, "keywords: " + keywords + ";tag: " + tag + ";subjectId: " + subjectId + "; sort: " + sort + "; liveStatus: " + liveStatus + "; nextToken: "
                + nextToken + "; fallCount: " + fallCount);

        ProgramFallListResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_SEARCH_PROGRAM, ProgramFallListResp.class, keywords, tag, subjectId, sort, liveStatus, nextToken,
                    fallCount);

            if (0 == resp.getError()) {
                return resp.getFallList();
            }
        } catch (Exception e) {

        }

        if (null != resp) {
            throw new LiveHttpException(resp.getError());
        } else {
            throw new LiveHttpException();
        }
    }

    public FallList<Program> getRecommandedProgram() throws LiveHttpException {

        ProgramFallListResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_GET_RECOMMEND_PROGRAM, ProgramFallListResp.class);

            if (0 == resp.getError()) {
                return resp.getFallList();
            }
        } catch (Exception e) {

        }

        if (null != resp) {
            throw new LiveHttpException(resp.getError());
        } else {
            throw new LiveHttpException();
        }
    }

    public java.util.List<String> getSearchWordsList() throws LiveHttpException {

        SearchWordsListResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_GET_SEARCH_WORD, SearchWordsListResp.class);

            if (0 == resp.getError()) {
                return resp.getList();
            }
        } catch (Exception e) {

        }

        if (null != resp) {
            throw new LiveHttpException(resp.getError());
        } else {
            throw new LiveHttpException();
        }
    }

    public enum SortKeyword {
        @SerializedName("starttime")
        START_TIME("starttime"),

        @SerializedName("vv")
        VV("vv"),

        @SerializedName("online")
        ONLINE("online");

        private String name;

        private SortKeyword(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum LiveStatusKeyword {

        @SerializedName("coming")
        COMING("coming"),

        @SerializedName("living")
        LIVING("living") {
        },

        @SerializedName("VOD")
        VOD("vod"),

        @SerializedName("nodel")
        NO_DEL("nodel");

        private String name;

        private LiveStatusKeyword(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
