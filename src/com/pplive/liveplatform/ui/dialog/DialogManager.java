package com.pplive.liveplatform.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

import com.pplive.liveplatform.R;

public class DialogManager {
    public static Dialog logoutAlertDialog(final Activity playerActivity, DialogInterface.OnClickListener startListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(playerActivity);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.settings_logout_title);
        builder.setMessage(R.string.settings_logout_text);
        builder.setPositiveButton(R.string.btn_confirm, startListener);
        builder.setNegativeButton(R.string.btn_cancel, null);
        return builder.create();
    }

    public static Dialog alertMobileDialog(final Activity playerActivity, DialogInterface.OnClickListener startListener) {
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

    public static Dialog alertNoNetworkDialog(final Activity playerActivity, DialogInterface.OnClickListener startListener) {
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

    public static Dialog alertLivingTerminated(final Context context, DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.mobile_alert_title);
        builder.setMessage(R.string.alert_message_living_terminated);
        builder.setPositiveButton(R.string.alert_positive_living_terminated, positiveListener);
        builder.setNegativeButton(R.string.alert_negative, negativeListener);

        return builder.create();
    }

    public static Dialog alertPrelive(final Context context, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.alert_title_prelive);
        builder.setMessage(R.string.alert_message_prelive);
        builder.setPositiveButton(R.string.alert_positive_prelive, positiveListener);
        builder.setNegativeButton(R.string.alert_negative, negativeListener);

        return builder.create();
    }
}
