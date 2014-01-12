package com.pplive.liveplatform.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.pplive.liveplatform.R;

public class DialogManager {
    public static Dialog alertPlayEndDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.player_end_title);
        builder.setMessage(R.string.toast_player_complete);
        builder.setPositiveButton(R.string.player_end_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.setNegativeButton(R.string.player_end_cancel, null);
        return builder.create();
    }

    public static Dialog alertLogoutDialog(final Activity playerActivity, DialogInterface.OnClickListener startListener) {
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
        builder.setTitle(R.string.mobile_alert_network_title);
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
        builder.setMessage(R.string.no_network_message);
        builder.setPositiveButton(R.string.no_network_confirm, startListener);
        builder.setNegativeButton(R.string.no_network_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playerActivity.finish();
            }
        });
        return builder.create();
    }

    public static Dialog alertLivingPaused(final Context context, DialogInterface.OnClickListener positiveListener,
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

    // Alert Mobile 2G
    public static Dialog alertMobile2GLive(final Context context, DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.alert_mobile_title);
        builder.setMessage(R.string.alert_mobile_2g_live_message);
        builder.setPositiveButton(R.string.alert_mobile_positive, positiveListener);
        builder.setNegativeButton(R.string.alert_mobile_2g_negative, positiveListener);

        return builder.create();
    }

    public static Dialog alertMobile2GPlay(final Context context, DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.alert_mobile_title);
        builder.setMessage(R.string.alert_mobile_2g_play_message);
        builder.setPositiveButton(R.string.alert_mobile_positive, positiveListener);
        builder.setNegativeButton(R.string.alert_mobile_2g_negative, positiveListener);

        return builder.create();
    }

    // Alert Mobile 3G
    public static Dialog alertMobile3GLive(final Context context, DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.alert_mobile_title);
        builder.setMessage(R.string.alert_mobile_3g_live_message);
        builder.setPositiveButton(R.string.alert_mobile_positive, positiveListener);
        builder.setNegativeButton(R.string.alert_mobile_3g_live_negative, positiveListener);

        return builder.create();
    }

    public static Dialog alertMobile3GPlay(final Context context, DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.alert_mobile_title);
        builder.setMessage(R.string.alert_mobile_3g_play_message);
        builder.setPositiveButton(R.string.alert_mobile_positive, positiveListener);
        builder.setNegativeButton(R.string.alert_mobile_3g_play_negative, positiveListener);

        return builder.create();
    }
}
