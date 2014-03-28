package com.pplive.liveplatform.core.update;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用于记录普通模式升级，是否提示过了
 */
public final class UpdatePref {
    /** 配置文件名 */
    public static final String FILE_NAME = "update";

    /**
     * <一句话功能简述> <功能详细描述>
     * 
     * @param context
     *            context
     * @return true
     * @see [类、类#方法、类#成员]
     */
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(FILE_NAME, 0);
    }

    /**
     * <一句话功能简述> <功能详细描述>
     * 
     * @param context
     *            context
     * @return true
     * @see [类、类#方法、类#成员]
     */
    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    /**
     * <一句话功能简述> <功能详细描述>
     * 
     * @param context
     *            context
     * @param key
     *            key
     * @param value
     *            value
     * @see [类、类#方法、类#成员]
     */
    public static void setPref(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * <一句话功能简述> <功能详细描述>
     * 
     * @param context
     *            context
     * @param key
     *            key
     * @param defaultKey
     *            defaultKey
     * @return true
     * @see [类、类#方法、类#成员]
     */
    public static boolean getPref(Context context, String key, boolean defaultKey) {
        return getPreferences(context).getBoolean(key, defaultKey);
    }
}
