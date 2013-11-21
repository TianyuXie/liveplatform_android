package com.pplive.liveplatform.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.player.LivePlayerFragment;
import com.pplive.liveplatform.util.DisplayUtil;

public class LivePlayerActivity extends FragmentActivity implements SensorEventListener {
    static final String TAG = "LivePlayerActivity";

    private static final int SCREEN_ORIENTATION_INVALID = -1;

    private LivePlayerFragment mLivePlayerFragment;

    private View mFragmentContainer;

    private View mDialogView;

    private ToggleButton mModeBtn;

    private SensorManager mSensorManager;

    private Sensor mSensor;

    private int mCurrentOrient;

    private int mUserOrient;

    private boolean mIsFull;

    private int mHalfScreenHeight;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Log.d(TAG, "onCreate");

        /* init window */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_player);

        /* init fragment */
        mLivePlayerFragment = new LivePlayerFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.layout_player_fragment, mLivePlayerFragment).commit();

        /* init values */
        mUserOrient = SCREEN_ORIENTATION_INVALID;
        mCurrentOrient = getRequestedOrientation();
        mHalfScreenHeight = (int) (DisplayUtil.getWidthPx(this) * 3.0f / 4.0f);

        /* init views */
        mFragmentContainer = findViewById(R.id.layout_player_fragment);
        mDialogView = findViewById(R.id.layout_player_dialog);
        mModeBtn = (ToggleButton) findViewById(R.id.btn_player_mode);
        mModeBtn.setOnClickListener(onModeBtnClickListener);
        setLayout(DisplayUtil.isLandscape(this), true);

        /* init others */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mLivePlayerFragment.setLayout(mIsFull);
        mLivePlayerFragment.setupPlayer(getIntent());
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    private void setLayout(boolean isFull, boolean init) {
        if (mIsFull == isFull && !init) {
            return;
        }
        Log.d(TAG, "setLayout");
        mIsFull = isFull;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFragmentContainer.getLayoutParams();
        if (mIsFull) {
            lp.height = LayoutParams.MATCH_PARENT;
            mModeBtn.setChecked(true);
            mDialogView.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            lp.height = mHalfScreenHeight;
            mModeBtn.setChecked(false);
            mDialogView.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        mFragmentContainer.requestLayout();
        if (!init) {
            mLivePlayerFragment.setLayout(mIsFull);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        setLayout(DisplayUtil.isLandscape(this), false);
    }

    View.OnClickListener onModeBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (DisplayUtil.isLandscape(getApplicationContext())) {
                mUserOrient = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                mUserOrient = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    };

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (mCurrentOrient != requestedOrientation) {
            mCurrentOrient = requestedOrientation;
            Log.d(TAG, "Update Orientation");
            super.setRequestedOrientation(requestedOrientation);
        }
    }

    public void sensorOrientation(int requestedOrientation) {
        if (mUserOrient == SCREEN_ORIENTATION_INVALID || mUserOrient == requestedOrientation) {
            mUserOrient = SCREEN_ORIENTATION_INVALID;
            setRequestedOrientation(requestedOrientation);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float ax = event.values[0];
        float ay = event.values[1];
        float az = event.values[2];

        double gxy = Math.sqrt(ax * ax + ay * ay);
        double g = Math.sqrt(ax * ax + ay * ay + az * az);
        double cos = ay / gxy;
        if (cos > 1) {
            cos = 1;
        } else if (cos < -1) {
            cos = -1;
        }
        double rady = (ax >= 0) ? Math.acos(cos) : 2 * Math.PI - Math.acos(cos);
        double radz = Math.asin(az / g);
        double degy = Math.toDegrees(rady);
        double degz = Math.toDegrees(radz);
        if (-60 < degz && degz < 60) {
            if (90 - 25 < degy && degy < 90 + 25) {
                sensorOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (360 - 25 < degy || degy < 0 + 25) {
                sensorOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (270 - 25 < degy && degy < 270 + 25) {
                sensorOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
}
