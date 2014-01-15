package com.pplive.liveplatform.update;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.UpdateService;
import com.pplive.liveplatform.core.service.live.model.Packet;
import com.pplive.liveplatform.dac.info.AppInfo;
import com.pplive.liveplatform.util.DirManager;

public class Update {

    public static final File DOWNLAOD_DIR = new File(DirManager.getDownloadPath());

    public static final String PREF_UPDATE_URL = "update_url";

    public static final String PREF_UPDATE_DESCRIPTION = "update_description";

    public static final String PREF_UPDATE_VERSION = "update_version";

    public static final String PREF_APP_NAME = "app_name";

    public static final String PREF_APP_ICON = "app_icon";

    public static final String PREF_APP_URL = "app_url";

    public static final String PREF_APP_DESCRIPTION = "app_description";

    public static final String PREF_APP_SID = "app_sid";

    public static final String PREF_APP_PACKAGE = "app_package";

    public static final String PREF_APP_DATE = "app_date";

    public static final String PREF_APP_COUNT = "app_count";

    public static final String PREF_GAME_DATE = "game_date";

    public static void deleteLastUpdateApk() {
        if (DOWNLAOD_DIR.exists()) {
            File[] list = DOWNLAOD_DIR.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (!TextUtils.isEmpty(filename) && filename.endsWith(".apk")) {
                        return true;
                    }
                    return false;
                }
            });
            for (File file : list) {
                file.delete();
            }
        }
    }

    /**
     * 手动更新
     */
    public static void updateManual(final Activity activity) {
        //Toast.makeText(activity, R.string.manual_update_start, Toast.LENGTH_SHORT).show();
        Thread updateThread = new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                ArrayList<UpdateInfo> updateInfos = getUpdateInfos(activity, true);
                doUpdate(updateInfos, activity);
            }

        };
        updateThread.start();

    }

    public static boolean doUpdate(ArrayList<UpdateInfo> updateInfos, final Activity activity) {
        if (updateInfos == null) {
            runInUiThreadToast(activity, R.string.update_no_update_information);
            return false;
        }

        final int localVersion = AppInfo.getVersionCode();
        // final int localVersion = 99;


        for (UpdateInfo updateInfo : updateInfos) {
            
            if (updateInfo.minVersionCode <= localVersion && localVersion <= updateInfo.maxVersionCode) {
                if (updateInfo.model != UpdateInfo.MODE_FORCE && updateInfo.model != UpdateInfo.MODE_RECOMMEND) {
                    updateInfo.model = UpdateInfo.MODE_NORMAL;
                }

                if (activity.isFinishing()) {
                    return false;
                }

                // 弹出提示
                final UpdateInfo info = updateInfo;
                // 保存升级信息
                Editor editor = UpdatePref.getEditor(activity);
                editor.putString(PREF_UPDATE_URL, info.url);
                editor.putString(PREF_UPDATE_DESCRIPTION, info.description);
                editor.putLong(PREF_UPDATE_VERSION, info.distVersionCode);
                editor.commit();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showUpdateDialog(activity, info);
                    }
                });
                return true;
            } else {
                String msg;
                msg = activity.getString(R.string.update_latest_update, AppInfo.getVersionName());
                runInUiThreadToast(activity, msg);
            }
        }
        return false;
    }
    
    private static void runInUiThreadToast(final Activity activity, final String msg){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private static void runInUiThreadToast(final Activity activity, final int msg){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void doUpdateAPP(final Activity context) {
        Thread updateThread = new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                update(context);
            }

        };
        updateThread.start();
    }

    /**
     * 启动pptv时是否弹出升级框
     * 
     * @param activity
     *            activity
     * @return true
     * @see [类、类#方法、类#成员]
     */
    public static boolean update(final Activity activity) {
        // 删除以前的更新文件
        deleteLastUpdateApk();
        final ArrayList<UpdateInfo> updateInfos = getUpdateInfos(activity, false);
        // ArrayList<UpdateInfo> updateInfos = getUpdateInfosByTest(activity);
        if (updateInfos == null) {
            return false;
        } else {
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
    public static void doStartUpdate(final Activity activity, final ArrayList<UpdateInfo> updateInfos) {
        try {
            // 普通弹框升级
            updateWhenStartPPTV(activity, updateInfos);
        } catch (Exception e) {

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
    public static boolean updateWhenStartPPTV(final Activity activity, final ArrayList<UpdateInfo> updateInfos) {
        if (updateInfos == null) {
            return false;
        }
        for (UpdateInfo updateInfo : updateInfos) {
            // msg = activity.getString(R.string.the_latest_update,
            // UtilMethod.getLocalVersionName(activity));
            final int localVersion = AppInfo.getVersionCode();
            if (updateInfo.minVersionCode <= localVersion && localVersion <= updateInfo.maxVersionCode) {

                if (updateInfo.model != UpdateInfo.MODE_FORCE && updateInfo.model != UpdateInfo.MODE_RECOMMEND) {
                    updateInfo.model = UpdateInfo.MODE_NORMAL;

                }

                // 判断更新模式，如果是普通模式，判断是否提示过
                //
                if (updateInfo.model == UpdateInfo.MODE_NORMAL) {
                    if (UpdatePref.getPref(activity, updateInfo.distVersionName, false)) {
                        return false;
                    }
                }

                if (activity.isFinishing()) {
                    return false;
                }

                // 弹出提示
                final UpdateInfo info = updateInfo;

                // 保存升级信息

                Editor editor = UpdatePref.getEditor(activity);
                editor.putString(PREF_UPDATE_URL, info.url);
                editor.putString(PREF_UPDATE_DESCRIPTION, info.description);
                editor.putLong(PREF_UPDATE_VERSION, info.distVersionCode);
                editor.commit();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showUpdateDialog(activity, info);
                    }
                });

                return true;
            } else {

            }
        }

        return false;
    }

    public static ArrayList<UpdateInfo> getUpdateInfos(Context context, boolean ifmanual) {
        try {
            Packet result = null;
            if(ifmanual){
                result = UpdateService.getInstance().checkManUpdate(AppInfo.getChannel(), AppInfo.getPlatform(), Build.VERSION.RELEASE,
                        AppInfo.getVersionName(), Build.MANUFACTURER + "|" + Build.MODEL + "|" + Build.DEVICE);
            }else {
                result = UpdateService.getInstance().checkUpdate(AppInfo.getChannel(), AppInfo.getPlatform(), Build.VERSION.RELEASE,
                        AppInfo.getVersionName(), Build.MANUFACTURER + "|" + Build.MODEL + "|" + Build.DEVICE);
            }

            if (result == null) {
                return null;
            }
            ArrayList<UpdateInfo> list = new ArrayList<UpdateInfo>();
            UpdateInfo updateInfo = new UpdateInfo();
            updateInfo.maxVersionCode = result.getMaxVersionCode();
            updateInfo.minVersionCode = result.getMinVersionCode();
            updateInfo.distVersionCode = result.getDistVersionCode();

            updateInfo.distVersionName = result.getDistVersionName();

            updateInfo.url = result.getUrl();
            updateInfo.description = result.getDescription();

            updateInfo.model = result.getMode();
            list.add(updateInfo);
            // 测试用
            // JSONObject object = new JSONObject(new
            // String(readFromAssetFile(
            // context, "update.txt")));
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        return null;
    }

    public static void showUpdateDialog(final Activity activity, final UpdateInfo updateInfo) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("升级");
        dialog.setMessage(activity.getString(R.string.update_content, updateInfo.description)).create();
        dialog.setPositiveButton(R.string.update_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                startUpdate(activity, updateInfo);
                dialog.dismiss();
            }

        });
        dialog.setNegativeButton(R.string.update_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (updateInfo.model == UpdateInfo.MODE_FORCE) {
                    // 强制升级
                    activity.finish();
                } else {
                    // 跳老界面
                    if (updateInfo.model == UpdateInfo.MODE_NORMAL) {
                        // 如果是普通模式，下次不再提示
                        UpdatePref.setPref(activity, updateInfo.distVersionName, true);
                    }

                }
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);// 不可以取消
        dialog.show();

    }

    public void showNoSdDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("alert");
        builder.setMessage("no sdcard");
        final AlertDialog dlg = builder.create();
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dlg.notify();
            }
        });

        builder.setIcon(Color.parseColor("#FF0000"));

        dlg.show();
    }

    protected static void startUpdate(final Activity activity, final UpdateInfo updateInfo) {

        // 有更新
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //UtilMethod.getInstance().showNoSdDialog(activity);

            return;
        }

        if (!DOWNLAOD_DIR.exists()) {
            DOWNLAOD_DIR.mkdirs();
        }

        String fileName = updateInfo.url.substring(updateInfo.url.lastIndexOf("/") + 1, updateInfo.url.lastIndexOf("."));

        String filePath = DirManager.SD_APP_PATH + File.separator + fileName + ".apk";
        File file = new File(filePath);
        if (file.exists()) {
            DownManager.openFile(activity, file);
            return;
        }

        Thread download = new Thread(new Runnable() {
            @Override
            public void run() {
                activity.finish();
                DownManager.down(activity, updateInfo.url, DOWNLAOD_DIR);
            }
        });

        download.start();

    }

    public static String getUpdateUrl(Context context) {
        return UpdatePref.getPreferences(context).getString(PREF_UPDATE_URL, "");
    }

    public static String getUpdateDescription(Context context) {
        return UpdatePref.getPreferences(context).getString(PREF_UPDATE_DESCRIPTION, "");
    }

    public static long getUpdateVersion(Context context) {
        return UpdatePref.getPreferences(context).getLong(PREF_UPDATE_VERSION, 0);
    }

}
