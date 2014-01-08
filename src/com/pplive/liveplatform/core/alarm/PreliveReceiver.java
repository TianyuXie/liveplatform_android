package com.pplive.liveplatform.core.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.ui.LiveRecordActivity;

public class PreliveReceiver extends BroadcastReceiver {
    static final String TAG = "_PreliveAlarmReceiver";

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (SettingsProvider.getInstance(context).getAppPrefs().isPreliveNotify()) {
            Log.d(TAG, "isPreliveNotify");
            Program program = (Program) intent.getSerializableExtra(AlarmCenter.EXTRA_PROGRAM);
            if (AlarmCenter.getInstance(context).isAvailablePrelive(program.getId(), UserManager.getInstance(context).getUsernamePlain())) {
                Log.d(TAG, "isPreliveAvailable");
                Intent newIntent = new Intent(context, LiveRecordActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newIntent.putExtra(LiveRecordActivity.EXTRA_PROGRAM, program);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_ONE_SHOT);

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = new Notification();
                notification.icon = R.drawable.ic_launcher;
                notification.tickerText = context.getString(R.string.prelive_default_message);
                notification.defaults = Notification.DEFAULT_SOUND;
                notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notification.setLatestEventInfo(context, context.getString(R.string.prelive_title), context.getString(R.string.prelive_default_message),
                        pendingIntent);
                manager.notify(1, notification);
            }
        }
    }
}
