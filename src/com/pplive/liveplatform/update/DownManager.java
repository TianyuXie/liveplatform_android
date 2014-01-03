package com.pplive.liveplatform.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class DownManager
{
    /**
     * len
     */
    public static int len = 0;

    /**
     * cLen
     */
    public static int cLen = 0;

    /**
     * beginUpload
     */
    public static boolean beginUpload = false;

    /**
     * error
     */
    public static boolean error;

    private static int connectTimeout = 30 * 1000;

    private static int readTimeout = 30 * 1000;

    // private static Context mContext;

    /**
     * 下载 url-apk路径，dir-本地路径如/sdcard/
     * 
     * @param context Context
     * @param url url
     * @param dir dir
     * @see [类、类#方法、类#成员]
     */
    public static void down(final Context context, final String url, final File dir)
    {
        // mContext = context;
        // 获取文件
        // 进度条提示文件下载中
        if (!beginUpload)
        {
            getFile(context, url, dir);
        }
    }

    private static void getFile(Context context, final String strPath, final File dir)
    {

        // Runnable r = new Runnable() {
        // public void run() {
        try
        {
            beginUpload = true;
            error = false;

            final Intent intent = new Intent(context, UpdateProgressActivity.class);
            context.startActivity(intent);

            synchronized (DownManager.class)
            {
                getDataSource(context, strPath, dir);
            }
            beginUpload = false;
        }
        catch (Exception e)
        {
            beginUpload = false;
            error = true;
        }
        len = 0;
        cLen = 0;
        // }
        // };
        // new Thread(r).start();

    }

    /**
     * <一句话功能简述> <功能详细描述>
     * 
     * @param strPath
     * @param dir
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    private static void getDataSource(Context context, final String strPath, final File dir) throws Exception
    {

        // if (!URLUtil.isNetworkUrl(strPath))
        // {
        // LogUtils.error("----strURL_APK ERROR----" + strPath);
        // }
        // else
        // {
        URL myURL = new URL(strPath);
        URLConnection conn = myURL.openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        conn.connect();
        InputStream is = conn.getInputStream();
        if (is == null)
        {
            throw new RuntimeException("stream is null");
        }

        // 不存在dir时创建目录
        if (!dir.isDirectory())
        {
            dir.mkdir();
        }

        String fileEx = strPath.substring(strPath.lastIndexOf(".") + 1, strPath.length()).toLowerCase();
        String fileNa = strPath.substring(strPath.lastIndexOf("/") + 1, strPath.lastIndexOf("."));
        File myTempFile = new File(dir, fileNa + "." + fileEx);
        // String currentTempFilePath = myTempFile.getAbsolutePath();
        FileOutputStream fos = new FileOutputStream(myTempFile);
        len = conn.getContentLength();
        cLen = 0;
        byte buf[] = new byte[128];
        do
        {
            int numread = is.read(buf);
            if (numread <= 0)
            {
                break;
            }
            fos.write(buf, 0, numread);
            cLen += numread;
            // Log.e("----------update-----------",cLen+"/"+len);
        }
        while (true);
        try
        {
            is.close();
        }
        catch (Exception e)
        {
            
        }

        try
        {
            fos.close();
        }
        catch (Exception e)
        {
            
        }

        // 关闭滚动框
        // Message msg2 = new Message();
        // msg2.what = ShowProgressBar.STOP;
        // ShowProgressBar.sendMessage(msg2);

        // 安装apk
        if ("apk".equals(fileEx))
        {
            if (context instanceof Activity)
            {
                Activity activity = (Activity) context;

                if (!activity.isFinishing())
                {
                    activity.finish();
                }
            }

            openFile(context, myTempFile);
        }
        // }
    }

    /**
     * openFile
     * 
     * @param context Context
     * @param file File
     * @see [类、类#方法、类#成员]
     */
    public static void openFile(final Context context, final File file)
    {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        context.startActivity(intent);
    }

    private static String getMIMEType(File f)
    {
        String type = "";
        String fName = f.getName();
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg")
                || end.equals("wav"))
        {
            type = "audio";
        }
        else if (end.equals("3gp") || end.equals("mp4"))
        {
            type = "video";
        }
        else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp"))
        {
            type = "image";
        }
        else if (end.equals("apk"))
        {
            /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        }
        else
        {
            type = "*";
        }
        if (end.equals("apk"))
        {
        }
        else
        {
            type += "/*";
        }
        return type;
    }

}
