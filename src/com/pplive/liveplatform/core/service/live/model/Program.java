package com.pplive.liveplatform.core.service.live.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.util.StringUtil;
import com.pplive.liveplatform.util.TimeUtil;

public class Program implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int SUBJECT_ID_ORIGIN = 1;

    long pid;

    String title;

    String owner;

    User user;

    LiveModeEnum mode;

    long starttime;

    LiveStatusEnum livestatus;

    int subject_id = SUBJECT_ID_ORIGIN;

    String cover_url;

    String screenshot_url;

    String coname = Constants.DEFAULT_CONAME_PPTV;

    Record record;

    String playencode;

    Token tk;

    Recommend recommend;

    public Program(String owner, String title, long starttime) {
        this(owner, LiveModeEnum.CAMERA, title, starttime);
    }

    public Program(String owner, LiveModeEnum mode, String title, long starttime) {
        this.owner = owner;
        this.mode = mode;
        this.title = title;
        this.starttime = starttime;
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

    public String getOwner() {
        return owner;
    }

    public String getOwnerNickname() {
        return user != null ? user.getNickname() : owner;
    }

    public String getOwnerIcon() {
        return user != null ? user.getIcon() : "";
    }

    public long getStartTime() {
        return starttime;
    }

    public String getStartTimeLong() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss yyyy.MM.dd", Locale.US);
        return format.format(new Date(starttime));
    }

    public String getStartTimeShort() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
        return format.format(new Date(starttime));
    }

    public LiveStatusEnum getLiveStatus() {
        return livestatus;
    }

    public boolean isComing() {
        return livestatus == LiveStatusEnum.INIT || livestatus == LiveStatusEnum.PREVIEW || livestatus == LiveStatusEnum.NOT_START;
    }

    private String getCoverPre() {
        String coverUrl = getCoverUrl();
        return TextUtils.isEmpty(coverUrl) ? getScreenshotUrl() : coverUrl;
    }

    private String getShotPre() {
        String shotUrl = getScreenshotUrl();
        return TextUtils.isEmpty(shotUrl) ? getCoverUrl() : shotUrl;
    }

    public String getRecommendCover() {
        if (null != recommend) {
            if ("cover_url".equals(recommend.page_pic)) {
                return getCoverPre();
            } else if ("screenshot_url".equals(recommend.page_pic)) {
                return getShotPre();
            }
        }
        return getCoverPre();
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

    public int getViews() {
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

    public boolean isLiving() {
        return livestatus == LiveStatusEnum.LIVING;
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

}
