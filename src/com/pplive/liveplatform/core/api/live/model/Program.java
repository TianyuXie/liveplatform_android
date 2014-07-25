package com.pplive.liveplatform.core.api.live.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.util.StringUtil;
import com.pplive.liveplatform.util.TimeUtil;

public class Program implements Serializable {

    private static final long serialVersionUID = -6974240710468812996L;

    private static final int SUBJECT_ID_ORIGIN = 1;

    Long pid;

    String title;

    String owner;

    User user;

    LiveModeEnum mode;

    Long real_starttime;

    Long real_endtime;

    Long starttime;

    Long insert_time;

    LiveStatusEnum livestatus;

    Integer subject_id;

    String cover_url;

    String screenshot_url;

    String coname;

    Record record;

    String playencode;

    Token tk;

    Recommend recommend;

    StreamStatus streamstatus;

    String[] tags;

    public Program(String owner, String title, long starttime) {
        this(owner, LiveModeEnum.CAMERA, title, starttime);
    }

    private Program(String owner, LiveModeEnum mode, String title, long starttime) {
        this.owner = owner;
        this.mode = mode;
        this.title = title;
        this.starttime = starttime;
        this.subject_id = 3;
        this.coname = Constants.DEFAULT_CONAME_PPTV;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLiveStatus(LiveStatusEnum status) {
        this.livestatus = status;
    }

    public long getId() {
        return pid;
    }

    public String getTitle() {
        return title;
    }

    public String getTags() {
        if (null != tags && tags.length > 0) {
            StringBuilder sb = new StringBuilder(tags[0]);

            for (int i = 1; i < tags.length; ++i) {
                sb.append(" ").append(tags[i]);
            }

            return sb.toString();
        }

        return "";
    }

    public int getSubjectId() {
        return subject_id;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwnerNickname() {
        return user != null ? user.getDisplayName().split("\\(")[0] : owner;
    }

    public String getOwnerIcon() {
        return user != null ? user.getIcon() : "";
    }

    public long getStartTime() {
        return starttime;
    }

    public String getStartTimeLong() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd", Locale.US);
        return format.format(new Date(starttime));
    }

    public String getStartTimeShort() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return format.format(new Date(starttime));
    }

    public LiveStatusEnum getLiveStatus() {
        return livestatus;
    }

    private String getCoverPre() {
        String coverUrl = getCoverUrl();
        if (!TextUtils.isEmpty(coverUrl)) {
            return coverUrl;
        } else {
            return getShotBySize(getScreenshotUrl(), 120);
        }
    }

    private String getShotPre() {
        String shotUrl = getScreenshotUrl();
        if (!TextUtils.isEmpty(shotUrl)) {
            return getShotBySize(shotUrl, 120);
        } else {
            return getCoverUrl();
        }
    }

    public String getRecommendCover() {
        if (null != recommend) {
            if ("cover_url".equals(recommend.page_pic)) {
                return getCoverPre();
            } else if ("screenshot_url".equals(recommend.page_pic)) {
                return getShotPre();
            }
        }
        return getShotPre();
    }

    private String getCoverUrl() {
        return StringUtil.isNullOrEmpty(cover_url) ? "" : cover_url;
    }

    public String getScreenshotUrl() {
        return StringUtil.isNullOrEmpty(screenshot_url) ? "" : screenshot_url;
    }

    private int getVV() {
        return null == record ? 0 : record.vv;
    }

    private int getOnline() {
        return null == record ? 0 : record.online;
    }

    public int getViewers() {
        if (isLiving()) {
            return getOnline();
        } else {
            return getVV();
        }
    }

    public String getShareLinkUrl() {
        return StringUtil.isNullOrEmpty(playencode) ? "" : Constants.BASE_SHARE_LINK_URL + playencode;
    }

    public String getLiveToken() {
        return (null == tk || StringUtil.isNullOrEmpty(tk.livetk)) ? "" : tk.livetk;
    }

    public boolean isPause() {
        return livestatus == LiveStatusEnum.PAUSE;
    }

    public boolean isLiving() {
        return livestatus == LiveStatusEnum.LIVING || livestatus == LiveStatusEnum.PAUSE;
    }

    public boolean isVOD() {
        return livestatus == LiveStatusEnum.STOPPED;
    }

    public boolean isDeleted() {
        return livestatus == LiveStatusEnum.DELETED || livestatus == LiveStatusEnum.SYS_DELETED;
    }

    public boolean isPrelive() {
        return livestatus == LiveStatusEnum.INIT || livestatus == LiveStatusEnum.NOT_START || livestatus == LiveStatusEnum.PREVIEW;
    }

    public boolean isExpiredPrelive() {
        return isPrelive() && System.currentTimeMillis() > starttime + TimeUtil.MS_OF_HOUR;
    }

    public boolean isOriginal() {
        return subject_id == SUBJECT_ID_ORIGIN;
    }

    class Record implements Serializable {

        private static final long serialVersionUID = 4171543027567617738L;

        int vv;

        int online;
    }

    class Token implements Serializable {

        private static final long serialVersionUID = -111694858998271543L;

        String livetk;
    }

    class Recommend implements Serializable {

        private static final long serialVersionUID = 235755470139323543L;

        String page_pic;
    }

    public long getInsertTime() {
        return insert_time;
    }

    public long getRealStartTime() {
        return real_starttime;
    }

    public long getLength() {
        return real_endtime - real_starttime;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    private String getShotBySize(String url, int size) {
        if (url.startsWith("http://live2image")) {
            int index = url.lastIndexOf("/");
            return String.format(Locale.US, "%s/sp%d%s", url.substring(0, index), size, url.substring(index));
        } else {
            return url;
        }
    }

}
