package com.pplive.liveplatform.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.pplive.liveplatform.R;

public class DialogManager {
    public static Dialog mobileAlertDialog(final Activity playerActivity, DialogInterface.OnClickListener startListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(playerActivity);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.mobile_alert_title);
        builder.setMessage(R.string.mobile_not_unicom);
        builder.setPositiveButton(R.string.mobile_play_start, startListener);
        builder.setNegativeButton(R.string.mobile_play_stop, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playerActivity.finish();
            }
        });
        return builder.create();
    }

    public static Dialog noNetworkAlertDialog(final Activity playerActivity, DialogInterface.OnClickListener startListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(playerActivity);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.mobile_alert_title);
        builder.setMessage(R.string.mobile_not_unicom);
        builder.setPositiveButton(R.string.mobile_play_start, startListener);
        builder.setNegativeButton(R.string.mobile_play_stop, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playerActivity.finish();
            }
        });
        return builder.create();
    }
}
