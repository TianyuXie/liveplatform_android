package com.pplive.liveplatform.update;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateInfo implements Serializable {

    private static final long serialVersionUID = -8358993893534855440L;

    /**
     * 普通升级
     */
    public static final int MODE_NORMAL = 1;

    /**
     * 推荐升级
     */
    public static final int MODE_RECOMMEND = 2;

    /**
     * 强制升级
     */
    public static final int MODE_FORCE = 3;

    /**
     * 目标版本versionCode
     */
    public int distVersionCode;

    /**
     * 最小版本
     */
    public int minVersionCode;

    /**
     * 最大版本
     */
    public int maxVersionCode;

    /**
     * 目标版本versionName
     */
    public String distVersionName;

    /**
     * 升级下载apk url
     */
    public String url;

    /**
     * 描述
     */
    public String description;

    /**
     * 白名单
     */
    public ArrayList<String> whiteChannels;

    /**
     * 黑名单
     */
    public ArrayList<String> blackChannels;

    /**
     * 升级模式
     */
    public int model;

    @Override
    public String toString() {
        return "UpdateInfo [distVersionCode=" + distVersionCode + ", minVersionCode=" + minVersionCode + ", maxVersionCode=" + maxVersionCode
                + ", distVersionName=" + distVersionName + ", url=" + url + ", description=" + description + ", whiteChannels=" + whiteChannels
                + ", blackChannels=" + blackChannels + ", model=" + model + "]";
    }

}
