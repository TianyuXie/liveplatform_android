package com.pplive.liveplatform.update;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.pplive.liveplatform.LiveApplication;
import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.ui.SettingsActivity;
import com.pplive.liveplatform.util.HttpUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 解析版本信息文件
 * 
 * @author sugarzhang
 */
public class Update
{

    // private static final String UPDATE_URL =
    // "http://up.pplive.com/android/update_phone.txt";
    
    /** 升级地址，测试地址172.16.4.20:8090 */
    public static final String CHECK_UPDATE_URL = "http://android.config.synacast.com/check_update"; // "http://172.16.4.20:8090/check_update";

    /** 手动升级地址 */
    public static final String MANUAL_CHECK_UPDATE_URL = "http://android.config.synacast.com/manual_update";

    /** apk目录 */
    public static final File DOWNLAOD_DIR = new File(Environment.getExternalStorageDirectory(), "pptv/apk/");

    public static String DOWNLOAD_PPTV_NAME = "";

    // private static final String packet0 = "packet0";

    /** 目标版本 */
    private static final String DIST_VERSION_CODE = "distVersionCode";

    /** 最大版本 */
    private static final String MAX_VERSION_CODE = "maxVersionCode";

    /** 最小版本 */
    private static final String MIN_VERSION_CODE = "minVersionCode";

    /** 版本名称 */
    private static final String DIST_VERSION_NAME = "distVersionName";

    /** apk地址 */
    private static final String URL = "url";

    /** 描述 */
    private static final String DESCRIPTION = "description";

    /** 白名单 */
    private static final String WHITE_CHANNELS = "whiteChannels";

    /** 黑名单 */
    private static final String BLACK_CHANNELS = "blackChannels";

    /** 升级模式，1：普通升级 2：推荐升级 */
    private static final String MODEL = "model";

    /** 升级地址的字段，用于在SharedPreferences中存取 */
    public static final String PREF_UPDATE_URL = "update_url";

    /** 升级地址的字段，用于在SharedPreferences中存取 */
    public static final String PREF_UPDATE_DESCRIPTION = "update_description";

    /** 升级版本号的字段，用于在SharedPreferences中存取 */
    public static final String PREF_UPDATE_VERSION = "update_version";

    /** 软件推荐名的字段，用于在SharedPreferences中存取 */
    public static final String PREF_APP_NAME = "app_name";

    /** 软件推荐图标地址的字段，用于在SharedPreferences中存取 */
    public static final String PREF_APP_ICON = "app_icon";

    /** 软件推荐地址的字段，用于在SharedPreferences中存取 */
    public static final String PREF_APP_URL = "app_url";

    /** 软件推荐简介的字段，用于在SharedPreferences中存取 */
    public static final String PREF_APP_DESCRIPTION = "app_description";

    /** 软件推荐SID的字段，用于在SharedPreferences中存取 */
    public static final String PREF_APP_SID = "app_sid";

    /** 软件推荐包名的字段，用于在SharedPreferences中存取 */
    public static final String PREF_APP_PACKAGE = "app_package";

    /** 软件推荐 显示日期记录，用于控制一天显示n次软件推荐，用于在SharedPreferences中存取 */
    public static final String PREF_APP_DATE = "app_date";

    /** 软件推荐 显示次数记录，用于控制一天显示n次软件推荐，用于在SharedPreferences中存取 */
    public static final String PREF_APP_COUNT = "app_count";

    /** 游戏运行 显示日期记录，用于在SharedPreferences中存取 */
    public static final String PREF_GAME_DATE = "game_date";

    public static void deleteLastUpdateApk()
    {
        if (DOWNLAOD_DIR.exists())
        {
            File[] list = DOWNLAOD_DIR.listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String filename)
                {
                    if (!TextUtils.isEmpty(filename) && filename.endsWith(".apk"))
                    {
                        return true;
                    }
                    return false;
                }
            });
            for (File file : list)
            {
                file.delete();
            }
        }
    }

    /**
     * 手动更新
     */
    public static void updateManual(final Activity activity)
    {
        //Toast.makeText(activity, R.string.manual_update_start, Toast.LENGTH_SHORT).show();
        UpdateListener updateListener = new UpdateListener()
        {
            @Override
            public void onCompleted(ArrayList<UpdateInfo> updateInfos)
            {


            }
        };

        UpdateAsyncTask updateTask = new UpdateAsyncTask(updateListener);
        updateTask.execute(activity);

    }

    /**
     * 个人中心手动升级(无91弹框界面)
     * 
     * @param updateInfos
     * @param activity
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean doUpdate(ArrayList<UpdateInfo> updateInfos, final Activity activity)
    {
        if (updateInfos == null)
        {
            //Toast.makeText(activity, R.string.no_update_information, Toast.LENGTH_SHORT).show();
            return false;
        }

        final int localVersion = LiveApplication.getVersionCode();
        // final int localVersion = 99;
        String msg;

        for (UpdateInfo updateInfo : updateInfos)
        {
            msg = activity.getString(R.string.the_latest_update, LiveApplication.getVersionName());
            if (updateInfo.minVersionCode <= localVersion && localVersion <= updateInfo.maxVersionCode)
            {
                if (updateInfo.model != UpdateInfo.MODE_FORCE && updateInfo.model != UpdateInfo.MODE_RECOMMEND)
                {
                    updateInfo.model = UpdateInfo.MODE_NORMAL;
                }

                if (activity.isFinishing())
                {
                    return false;
                }

                // 弹出提示
                final UpdateInfo info = updateInfo;
                // 保存升级信息
                Editor editor = SettingsProvider.getInstance(activity).getPreferencesEditor();
                editor.putString(PREF_UPDATE_URL, info.url);
                editor.putString(PREF_UPDATE_DESCRIPTION, info.description);
                editor.putLong(PREF_UPDATE_VERSION, info.distVersionCode);
                editor.commit();

                showUpdateDialog(activity, info);
                return true;
            }
            else
            {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    /**
     * 启动pptv时是否弹出升级框
     * 
     * @param activity activity
     * @return true
     * @see [类、类#方法、类#成员]
     */
    public static boolean update(final Activity activity)
    {
        // 删除以前的更新文件
        deleteLastUpdateApk();
        final ArrayList<UpdateInfo> updateInfos = getUpdateInfos(activity);
        // ArrayList<UpdateInfo> updateInfos = getUpdateInfosByTest(activity);
        if (updateInfos == null)
        {
            return false;
        }
        else
        {
            updateWhenStartPPTV(activity, updateInfos);
        }

        return false;
    }

    /**
     * 启动升级,91智能升级sdk不能在子线程中调用升级方法，系统为2.x的手机会crash
     * 
     * @param activity
     * @see [类、类#方法、类#成员]
     */
    public static void doStartUpdate(final Activity activity, final ArrayList<UpdateInfo> updateInfos)
    {
        try
        {
            // 普通弹框升级
            updateWhenStartPPTV(activity, updateInfos);
        }
        catch (Exception e)
        {
           
        }
    }

    /**
     * 启动pptv时普通升级弹框(无91助手)
     * 
     * @param activity
     * @param updateInfos
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean updateWhenStartPPTV(final Activity activity, final ArrayList<UpdateInfo> updateInfos)
    {
        if (updateInfos == null)
        {
            return false;
        }
        for (UpdateInfo updateInfo : updateInfos)
        {
            // msg = activity.getString(R.string.the_latest_update,
            // UtilMethod.getLocalVersionName(activity));
            final int localVersion = LiveApplication.getVersionCode();
            if (updateInfo.minVersionCode <= localVersion && localVersion <= updateInfo.maxVersionCode)
            {

                if (updateInfo.model != UpdateInfo.MODE_FORCE && updateInfo.model != UpdateInfo.MODE_RECOMMEND)
                {
                    updateInfo.model = UpdateInfo.MODE_NORMAL;

                }

                // 判断更新模式，如果是普通模式，判断是否提示过
                //
                if (updateInfo.model == UpdateInfo.MODE_NORMAL)
                {
                    if (UpdatePref.getPref(activity, updateInfo.distVersionName, false))
                    {
                        return false;
                    }
                }

                if (activity.isFinishing())
                {
                    return false;
                }

                // 弹出提示
                final UpdateInfo info = updateInfo;

                // 保存升级信息
                
                Editor editor = SettingsProvider.getInstance(activity).getPreferencesEditor();
                editor.putString(PREF_UPDATE_URL, info.url);
                editor.putString(PREF_UPDATE_DESCRIPTION, info.description);
                editor.putLong(PREF_UPDATE_VERSION, info.distVersionCode);
                editor.commit();

                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        showUpdateDialog(activity, info);
                    }
                });

                return true;
            }
            else
            {

            }
        }

        return false;
    }

    /** 从服务器上取得更新信息 */
    public static ArrayList<UpdateInfo> getUpdateInfos(Context context)
    {
        // byte[] imgData = null;
        try
        {
            // HttpURLConnection conn;
            //
            // conn = (HttpURLConnection) new
            // URL(UPDATE_URL).openConnection();
            // conn.setDoInput(true);
            // conn.connect();
            // InputStream is = conn.getInputStream();
            // imgData = StreamTool.readInputStream(is);
            // conn.disconnect();

            Bundle params = new Bundle();
            params.putString("deviceid", LiveApplication.getIMEI(context)); // 用户手机imei
            params.putString("devicetype", Build.MANUFACTURER + "|" + Build.MODEL + "|" + Build.DEVICE); // 用户手机型号
            params.putString("osv", Build.VERSION.RELEASE); // 操作系统版本
            params.putString("sv", LiveApplication.getVersionName()); // 软件版本
            params.putString("platform", "android_phone"); // 平台标志,

            // urlParams = new
            // String(Base64.encode(urlParams.getBytes("GBK")), "GBK");
            String updateUrl;
            if (context instanceof SettingsActivity)
            {
                updateUrl = MANUAL_CHECK_UPDATE_URL;
            }
            else
            {
                updateUrl = CHECK_UPDATE_URL;
            }
            String result = HttpUtil.getHttpGetRequest(updateUrl + "?" + HttpUtil.generateQuery(params), "*/*");
            if (TextUtils.isEmpty(result))
            {
                return null;
            }

            // 真实用
            // JSONObject Object = new JSONObject(string2Json(new
            // String(imgData)));
            JSONObject object = new JSONObject(result);

            // 测试用
            // JSONObject object = new JSONObject(new
            // String(readFromAssetFile(
            // context, "update.txt")));

            ArrayList<UpdateInfo> list = new ArrayList<UpdateInfo>();
            Iterator<String> iterator = object.keys();
            String keyString;
            UpdateInfo updateInfo;
            JSONObject channelObject;
            while (iterator.hasNext())
            {
                keyString = iterator.next();

                updateInfo = new UpdateInfo();
                channelObject = object.getJSONObject(keyString);
                updateInfo.maxVersionCode = channelObject.getInt(MAX_VERSION_CODE);
                updateInfo.minVersionCode = channelObject.getInt(MIN_VERSION_CODE);
                updateInfo.distVersionCode = channelObject.getInt(DIST_VERSION_CODE);

                updateInfo.distVersionName = channelObject.getString(DIST_VERSION_NAME);

                updateInfo.url = channelObject.getString(URL);
                updateInfo.description = channelObject.getString(DESCRIPTION);

                updateInfo.model = channelObject.getInt(MODEL);

                try
                {
                    String string = channelObject.getString(WHITE_CHANNELS);
                    if (!TextUtils.isEmpty(string))
                    {
                        updateInfo.whiteChannels = new ArrayList<String>(Arrays.asList(string.split("|")));
                    }
                }
                catch (JSONException e)
                {
                    
                }

                try
                {
                    String string = channelObject.getString(BLACK_CHANNELS);
                    if (!TextUtils.isEmpty(string))
                    {
                        updateInfo.blackChannels = new ArrayList<String>(Arrays.asList(string.split("|")));
                    }
                }
                catch (JSONException e)
                {
                    
                }


                list.add(updateInfo);
            }

            return list;

            // } catch (IOException e) {
            // LogUtils.error(e.toString(), e);
        }
        catch (Exception e)
        {
            
        }
        //
        return null;
    }

    /**
     * 显示有新版本
     * 
     * @param activity activity
     * @param updateInfo updateInfo
     * @see [类、类#方法、类#成员]
     */
    public static void showUpdateDialog(final Activity activity, final UpdateInfo updateInfo)
    {
        View view = activity.getLayoutInflater().inflate(R.layout.update_dialog, null);
        final Dialog dailog = new Dialog(activity, R.style.dim_back_dialog);
        dailog.setContentView(view);

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();

        dailog.getWindow().getAttributes().width = (int) (dm.density * 270);
        dailog.getWindow().getAttributes().height = (int) (dm.density * 270);

        TextView content = (TextView) view.findViewById(R.id.update_info_content);
        content.setText(activity.getString(R.string.update_content, updateInfo.description));

        dailog.setCancelable(false);// 不可以取消

        View updateBtn = view.findViewById(R.id.update_btn);
        View cancelBtn = view.findViewById(R.id.update_cancel);
        updateBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                // 开始升级
                startUpdate(activity, updateInfo);

                dailog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if (activity instanceof FirstActivity)
                {
                    if (updateInfo.model == UpdateInfo.MODE_FORCE)
                    {
                        // 强制升级
                        activity.finish();
                    }
                    else
                    {
                        // 跳老界面
                        if (updateInfo.model == UpdateInfo.MODE_NORMAL)
                        {
                            // 如果是普通模式，下次不再提示
                            UpdatePref.setPref(activity, updateInfo.distVersionName, true);
                        }

                        // Intent intent = new Intent(activity,
                        // RecommendActivity.class);
                        // activity.startActivity(intent);
                        // activity.finish();
                        ((FirstActivity) activity).goToRecommed();
                    }
                }
                // else
                // {
                dailog.dismiss();
                // }
            }
        });
        dailog.show();
    }

    /***
     * 开始升级
     * 
     * @param activity
     * @param updateInfo
     * @see [类、类#方法、类#成员]
     */
    protected static void startUpdate(final Activity activity, final UpdateInfo updateInfo)
    {

        // 有更新
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            //UtilMethod.getInstance().showNoSdDialog(activity);

            return;
        }

        if (!DOWNLAOD_DIR.exists())
        {
            DOWNLAOD_DIR.mkdirs();
        }

        // 判断安装包是否已经下载到本地
        // File file = new File(DOWNLAOD_DIR
        // + getFileNameFromUrl(updateInfo.url));
        // if (file.exists()) {
        // // 直接安装
        // DownManager.openFile(activity, file);
        // } else {

        if (activity instanceof FirstActivity)
        {
            Thread download = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    activity.finish();
                    DownManager.down(activity, updateInfo.url, DOWNLAOD_DIR);
                }
            });

            download.start();
        }
        else if (activity instanceof SettingsActivity)
        {
            String fileName = updateInfo.url.substring(updateInfo.url.lastIndexOf("/") + 1,
                    updateInfo.url.lastIndexOf("."));

            String filePath = DownloadConfig.getInstance(activity).getStorageDirectory() + File.separator + fileName
                    + ".apk";
            File file = new File(filePath);
            if (file.exists())
            {
                DownManager.openFile(activity, file);
                return;
            }

        }
        else
        {

        }

        // }
    }

    /**
     * 升级文件地址,从SharedPreferences中读取
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getUpdateUrl(Context context)
    {
        return SettingsProvider.getInstance(context).getPreferences().getString(PREF_UPDATE_URL, "");
    }

    /**
     * 升级文件描述,从SharedPreferences中读取
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getUpdateDescription(Context context)
    {
        return SettingsProvider.getInstance(context).getPreferences().getString(PREF_UPDATE_DESCRIPTION, "");
    }

    /**
     * 升级文件版本,从SharedPreferences中读取
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static long getUpdateVersion(Context context)
    {
        return SettingsProvider.getInstance(context).getPreferences().getLong(PREF_UPDATE_VERSION, 0);
    }

}
