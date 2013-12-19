package com.pplive.liveplatform.core.service.live;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.live.model.FallList;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.service.live.resp.ProgramFallListResp;
import com.pplive.liveplatform.util.URL.Protocol;

public class SearchService extends RestService {
    
    public enum SortKeyword {
        @SerializedName("starttime")
        START_TIME {
            @Override
            public String toString() {
                return "starttime";
            }
        },
        
        @SerializedName("vv")
        VV {
            @Override
            public String toString() {
                return "vv";
            }
        };
    }
    
    public enum LiveStatusKeyword {
        
        @SerializedName("coming")
        COMING {
            @Override
            public String toString() {
                return "coming";
            }
        },
        
        @SerializedName("living")
        LIVING {
            @Override
            public String toString() {
                return "living";
            }
        },
        
        @SerializedName("VOD")
        VOD {
            @Override
            public String toString() {
                return "vod";
            }
        },
        
        @SerializedName("nodel")
        NO_DEL {
            @Override
            public String toString() {
                return "nodel";
            };
        };
    }

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
    
    public FallList<Program> searchProgram(int subjectId, SortKeyword sort, LiveStatusKeyword liveStatus, String nextToken, int fallCount) throws LiveHttpException {

        return searchProgram("" /* keywords */, subjectId, sort, liveStatus, nextToken, fallCount);
    }
    
    public FallList<Program> searchProgram(String keywords, int subjectId, SortKeyword sort, LiveStatusKeyword liveStatus, String nextToken, int fallCount) throws LiveHttpException {
        
        return searchProgram(keywords, subjectId, sort.toString(), liveStatus.toString(), nextToken, fallCount);
    }
    
    private FallList<Program> searchProgram(String keywords, int subjectId, String sort, String liveStatus, String nextToken, int fallCount) throws LiveHttpException {
        Log.d(TAG, "keywords: " + keywords + ";subjectId: " + subjectId + "; sort: " + sort + "; liveStatus: " + liveStatus + "; nextToken: " + nextToken
                + "; fallCount: " + fallCount);

        ProgramFallListResp resp = null;
        try {
            resp = mRestTemplate.getForObject(TEMPLATE_SEARCH_PROGRAM, ProgramFallListResp.class, keywords, subjectId, sort, liveStatus, nextToken, fallCount);

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
}
