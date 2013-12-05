package com.pplive.liveplatform.ui.widget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pplive.liveplatform.R;

public class StatusBar extends RelativeLayout {
    private static final int UPDATE_TIME = 801;

    private ImageView mBatteryImageView;

    private TextView mBatteryTextView;

    private TextView mTimeTextView;

    public StatusBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.widget_statusbar, this);
        mBatteryImageView = (ImageView) root.findViewById(R.id.image_statusbar_battery);
        mBatteryTextView = (TextView) root.findViewById(R.id.text_statusbar_battery);
        mTimeTextView = (TextView) root.findViewById(R.id.text_statusbar_time);
    }

    public StatusBar(Context context) {
        this(context, null);
    }

    public void updateBattery(Intent intent) {
        if (intent == null) {
            return;
        }
        int rawlevel = intent.getIntExtra("level", -1);
        int scale = intent.getIntExtra("scale", -1);
        int status = intent.getIntExtra("status", -1);

        int level = -1; // percentage, or -1 for unknown

        if (rawlevel >= 0 && scale > 0) {
            level = Math.round(rawlevel * 100 / (float) scale);
        }
        mBatteryTextView.setText(level + "%");
        mBatteryImageView.setImageResource(getIcon(status));
        mBatteryImageView.getDrawable().setLevel(level);
    }

    private int getIcon(int status) {
        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            return R.drawable.stat_sys_battery_charge;
        } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING || status == BatteryManager.BATTERY_STATUS_NOT_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL) {
            return R.drawable.stat_sys_battery;
        } else {
            return R.drawable.stat_sys_battery_unknown;
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case UPDATE_TIME:
                updateTime();
                break;
            }
        }
    };

    public void updateTime() {
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.US);
        mTimeTextView.setText(format.format(date));
        Message msg = mHandler.obtainMessage(UPDATE_TIME);
        mHandler.removeMessages(UPDATE_TIME);
        mHandler.sendMessageDelayed(msg, 1000);
    }

    public void stopUpdateTime() {
        mHandler.removeMessages(UPDATE_TIME);
    }
}
