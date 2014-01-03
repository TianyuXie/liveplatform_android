
package com.pplive.liveplatform.update;
import java.util.ArrayList;
import android.app.Activity;
import android.os.AsyncTask;
/**
 * 手动更新异步任务
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  javonewang
 * @version  [版本号, 2013-5-22]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class UpdateAsyncTask extends AsyncTask<Activity, Integer, ArrayList<UpdateInfo>>
{
    UpdateListener listener;

    public UpdateAsyncTask(UpdateListener _listener)
    {
        listener = _listener;
    }

    @Override
    protected ArrayList<UpdateInfo> doInBackground(Activity... params)
    {
        // TODO Auto-generated method stub
              Update.deleteLastUpdateApk();
        return Update.getUpdateInfos(params[0]);
    }

    @Override
    protected void onCancelled()
    {
        if (listener != null)
        {
            listener.onFailure();
        }
    }

    @Override
    protected void onPostExecute(ArrayList<UpdateInfo> result)
    {
        if (listener != null)
        {
            listener.onCompleted(result);
        }
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);
    }
}
