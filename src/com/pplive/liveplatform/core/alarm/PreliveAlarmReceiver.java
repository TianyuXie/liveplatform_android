package com.pplive.liveplatform.core.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.ui.AlarmActivity;

public class PreliveAlarmReceiver extends BroadcastReceiver {
    static final String TAG = "_PreliveAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (SettingsProvider.getInstance(context).getAppPrefs().isPreliveNotify()) {
            Intent newIntent = new Intent(context, AlarmActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.putExtra(AlarmActivity.EXTRA_TITLE, context.getString(R.string.prelive_title));
            newIntent.putExtra(AlarmActivity.EXTRA_MESSAGE, context.getString(R.string.prelive_default_message));
            newIntent.putExtra(AlarmActivity.EXTRA_PROGRAM, intent.getSerializableExtra(AlarmCenter.EXTRA_PROGRAM));
            context.startActivity(newIntent);
        }
    }

}
