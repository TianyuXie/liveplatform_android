
package com.pplive.liveplatform.update;
import java.util.ArrayList;
import android.app.Activity;
import android.os.AsyncTask;

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
