package com.pplive.liveplatform.core.api.live;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.api.BaseURL;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.model.FallList;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.api.live.resp.ProgramFallListResp;
import com.pplive.liveplatform.core.api.live.resp.ProgramListResp;
import com.pplive.liveplatform.core.api.live.resp.SearchWordsListResp;
import com.pplive.liveplatform.core.api.live.resp.UserFallListResp;
import com.pplive.liveplatform.core.api.live.resp.UserListResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class SearchAPI extends RESTfulAPI {

    private static final String TAG = SearchAPI.class.getSimpleName();

    private static final SearchAPI sInstance = new SearchAPI();

    private static final String TEMPLATE_SEARCH_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_CDN_HOST,
            "/search/v2/pptv/searchcommon?key={keywords}&tag={tag}&subjectid={subjectid}&sort={sort}&livestatus={livestatus}&nexttk={nexttk}&fallcount={fallcount}")
            .toString();

    private static final String TEMPLATE_SEARCH_USER = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_TEST_HOST,
            "/search/user/v2/pptv/usersearch?key={key}&fallcount={fallcount}&nexttk={nexttk}").toString();

    private static final String TEMPLATE_GET_RECOMMEND_PROGRAM = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_CDN_HOST,
            "/search/v2/c/pptv/recommend/program").toString();

    private static final String TEMPLATE_GET_RECOMMEND_KEYWORD = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_CDN_HOST,
            "/search/v2/c/pptv/recommend/searchwords").toString();

    private static final String TEMPLATE_GET_RECOMMEND_USER = new BaseURL(Protocol.HTTP, Constants.LIVEPLATFORM_API_TEST_HOST,
            "/search/v2/c/pptv/recommend/publicuser").toString();

    public static SearchAPI getInstance() {
        return sInstance;
    }

    private SearchAPI() {

    }

    public FallList<Program> searchProgram(int subjectId, SortKeyword sort, LiveStatusKeyword liveStatus, String nextToken, int fallCount)
            throws LiveHttpException {

        return searchProgram("" /* keyword */, subjectId, sort, liveStatus, nextToken, fallCount);
    }

    public FallList<Program> searchProgram(String keyword, SortKeyword sort, LiveStatusKeyword liveStatus, String nextToken, int fallCount)
            throws LiveHttpException {

        return searchProgram(keyword, -1 /* subjectId */, sort, liveStatus, nextToken, fallCount);
    }

    public FallList<Program> searchProgram(String keyword, int subjectId, SortKeyword sort, LiveStatusKeyword liveStatus, String nextToken, int fallCount)
            throws LiveHttpException {

        return searchProgram(keyword, "" /* tag */, subjectId, sort, liveStatus, nextToken, fallCount);
    }

    public FallList<Program> searchProgram(String keyword, String tag, int subjectId, SortKeyword sort, LiveStatusKeyword liveStatus, String nextToken,
            int fallCount) throws LiveHttpException {

        return searchProgram(keyword, tag, subjectId >= 0 ? String.valueOf(subjectId) : "", null != sort ? sort.toString() : "",
                null != liveStatus ? liveStatus.toString() : "", nextToken, fallCount);
    }

    private FallList<Program> searchProgram(String keyword, String tag, String subjectId, String sort, String liveStatus, String nextToken, int fallCount)
            throws LiveHttpException {
        Log.d(TAG, "keywords: " + keyword + "; tag: " + tag + "; subjectId: " + subjectId + "; sort: " + sort + "; liveStatus: " + liveStatus + "; nextToken: "
                + nextToken + "; fallCount: " + fallCount);

        ProgramFallListResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_SEARCH_PROGRAM, ProgramFallListResp.class, keyword, tag, subjectId, sort, liveStatus, nextToken,
                    fallCount);

            if (0 == resp.getError()) {
                return resp.getFallList();
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

    public FallList<User> searchUser(String keyword, String nextToken, int fallCount) throws LiveHttpException {

        UserFallListResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_SEARCH_USER, UserFallListResp.class, keyword, nextToken, fallCount);

            if (0 == resp.getError()) {
                return resp.getFallList();
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

    public java.util.List<Program> recommendProgram() throws LiveHttpException {

        ProgramListResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_GET_RECOMMEND_PROGRAM, ProgramListResp.class);

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

    public java.util.List<String> recommendKeyword() throws LiveHttpException {

        SearchWordsListResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_GET_RECOMMEND_KEYWORD, SearchWordsListResp.class);

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

    public java.util.List<User> recommendUser() throws LiveHttpException {

        UserListResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_GET_RECOMMEND_USER, UserListResp.class);

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
