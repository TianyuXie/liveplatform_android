package com.pplive.liveplatform.core.crash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import android.os.Build;
import android.util.Log;

import com.pplive.liveplatform.core.dac.info.AppInfo;
import com.pplive.liveplatform.util.DirManager;

public class AppCrashHandler implements UncaughtExceptionHandler {

    static final String TAG = AppCrashHandler.class.getSimpleName();

    private static final String KEY_PACKAGE_NAME = "PACKAGE_NAME";

    private static final String KEY_VERSION_NAME = "VERSION_NAME";

    private static final String KEY_CHANNEL = "INSTALL_CHANNEL";

    private static final String KEY_OS_VERSION = "OS_VERSION";

    private static final String KEY_MANUFACTURER = "MANUFACTURER";

    private static final String KEY_BRAND = "BRAND";

    private static final String KEY_BOARD = "BOARD";

    private static final String KEY_STACK_TRACE = "STACK_TRACE";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);

    private Properties mCrashInfo = new Properties(); // 注意Properties key和value必须是String，否则会报错。

    public static final void init() {
        Thread.setDefaultUncaughtExceptionHandler(new AppCrashHandler());
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        initCrashInfo();

        Writer w = new StringWriter();
        PrintWriter pw = new PrintWriter(w);
        ex.printStackTrace(pw);
        mCrashInfo.put(KEY_STACK_TRACE, w.toString());

        saveCrashInfo();
    }

    private void initCrashInfo() {
        mCrashInfo.clear();

        mCrashInfo.put(KEY_PACKAGE_NAME, AppInfo.getPackageName());
        mCrashInfo.put(KEY_VERSION_NAME, AppInfo.getVersionName());
        mCrashInfo.put(KEY_CHANNEL, AppInfo.getChannel());
        mCrashInfo.put(KEY_OS_VERSION, Build.VERSION.RELEASE);
        mCrashInfo.put(KEY_MANUFACTURER, Build.MANUFACTURER);
        mCrashInfo.put(KEY_BRAND, Build.BRAND);
        mCrashInfo.put(KEY_BOARD, Build.BOARD);
    }

    private void saveCrashInfo() {

        FileOutputStream fos = null;
        try {
            File file = new File(DirManager.getCrashCachePath(), "crash_" + SIMPLE_DATE_FORMAT.format(new Date()) + ".cr");

            fos = new FileOutputStream(file);

            mCrashInfo.store(fos, null);

        } catch (FileNotFoundException e) {
            Log.w(TAG, e.toString());
        } catch (IOException e) {
            Log.w(TAG, e.toString());
        } finally {
            if (null != fos) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    Log.w(TAG, e.toString());
                } finally {
                    fos = null;
                }
            }
        }
    }

}
