package com.pplive.liveplatform.ui;

import java.util.List;

import android.app.Dialog;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.rest.model.Watch;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.home.GetMediaTask;
import com.pplive.liveplatform.ui.player.LivePlayerFragment;
import com.pplive.liveplatform.ui.widget.DetectableRelativeLayout;
import com.pplive.liveplatform.ui.widget.EnterSendEditText;
import com.pplive.liveplatform.ui.widget.ShareDialog;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.ViewUtil;

public class LivePlayerActivity extends FragmentActivity implements SensorEventListener, LivePlayerFragment.Callback {
    static final String TAG = "_LivePlayerActivity";

    private static final int SCREEN_ORIENTATION_INVALID = -1;

    private DetectableRelativeLayout mRootLayout;

    private LivePlayerFragment mLivePlayerFragment;

    private View mFragmentContainer;

    private View mDialogView;

    private View mCommentView;

    private Dialog mShareDialog;

    private Button mWriteBtn;

    private EnterSendEditText mCommentEditText;

    private SensorManager mSensorManager;

    private Sensor mSensor;

    private int mCurrentOrient;

    private int mUserOrient;

    private boolean mIsFull;

    private boolean mWriting;

    private int mHalfScreenHeight;

    private String mUrl;

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
        mShareDialog = new ShareDialog(this, R.style.share_dialog, getString(R.string.share_dialog_title));

        /* init values */
        mUserOrient = SCREEN_ORIENTATION_INVALID;
        mCurrentOrient = getRequestedOrientation();
        mHalfScreenHeight = (int) (DisplayUtil.getWidthPx(this) * 3.0f / 4.0f);

        /* init views */
        mRootLayout = (DetectableRelativeLayout) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        mRootLayout.setOnSoftInputListener(onSoftInputListener);
        mCommentEditText = (EnterSendEditText) findViewById(R.id.edit_player_comment);
        mCommentEditText.setOnEnterListener(commentOnEnterListener);
        mCommentView = findViewById(R.id.layout_player_comment);
        mFragmentContainer = findViewById(R.id.layout_player_fragment);
        mDialogView = findViewById(R.id.layout_player_dialog);
        mWriteBtn = (Button) findViewById(R.id.btn_player_write);
        mWriteBtn.setOnClickListener(onWriteBtnClickListener);
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
        mLivePlayerFragment.setCallbackListener(this);
        mLivePlayerFragment.setTitle(getIntent().getStringExtra("title"));
        if (mUrl == null) {
            String username = getIntent().getStringExtra("username");
            long pid = getIntent().getLongExtra("pid", -1);
            Log.d(TAG, "pid:" + pid);
            if (pid != -1) {
                GetMediaTask task = new GetMediaTask();
                task.addTaskListener(onTaskListener);
                TaskContext taskContext = new TaskContext();
                taskContext.set(GetMediaTask.KEY_PID, pid);
                taskContext.set(GetMediaTask.KEY_USERNAME, username);
                task.execute(taskContext);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        pauseWriting();
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
        mLivePlayerFragment.setCallbackListener(null);
        super.onStop();
    }

    private void setLayout(boolean isFull, boolean init) {
        if (mIsFull == isFull && !init) {
            return;
        }
        Log.d(TAG, "setLayout");
        mIsFull = isFull;
        RelativeLayout.LayoutParams containerLp = (RelativeLayout.LayoutParams) mFragmentContainer.getLayoutParams();
        if (mIsFull) {
            containerLp.height = LayoutParams.MATCH_PARENT;
            mDialogView.setVisibility(View.GONE);
            mCommentView.setVisibility(View.GONE);
            mWriteBtn.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            RelativeLayout.LayoutParams dialogLp = (RelativeLayout.LayoutParams) mDialogView.getLayoutParams();
            containerLp.height = mHalfScreenHeight;
            dialogLp.topMargin = mHalfScreenHeight;
            mDialogView.setVisibility(View.VISIBLE);
            if (mWriting) {
                mCommentView.setVisibility(View.VISIBLE);
                mWriteBtn.setVisibility(View.GONE);
            } else {
                mCommentView.setVisibility(View.GONE);
                mWriteBtn.setVisibility(View.VISIBLE);
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
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

    View.OnClickListener onWriteBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startWriting();
        }
    };

    View.OnClickListener onModeBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    View.OnClickListener onShareBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mShareDialog.show();
        }
    };

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (mCurrentOrient != requestedOrientation) {
            Log.d(TAG, "Update Orientation");
            mCurrentOrient = requestedOrientation;
            pauseWriting();
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

    }

    private EnterSendEditText.OnEnterListener commentOnEnterListener = new EnterSendEditText.OnEnterListener() {

        @Override
        public boolean onEnter(View v) {
            //TODO
            String keyword = mCommentEditText.getText().toString();
            Log.d(TAG, "Send: " + keyword);
            stopWriting();
            return true;
        }
    };

    private DetectableRelativeLayout.OnSoftInputListener onSoftInputListener = new DetectableRelativeLayout.OnSoftInputListener() {
        @Override
        public void onSoftInputShow() {
            Log.d(TAG, "onSoftInputShow");
            mCommentView.setVisibility(View.VISIBLE);
            popupDialog();
        }

        @Override
        public void onSoftInputHide() {
            Log.d(TAG, "onSoftInputHide");
            popdownDialog();
            mCommentView.setVisibility(View.GONE);
            if (mCurrentOrient == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mWriteBtn.setVisibility(View.VISIBLE);
            }
            mWriting = false;
            //if (!mWriting) {
            //    mWriteBtn.setVisibility(View.VISIBLE);
            //}
        }
    };

    private void startWriting() {
        if (!mWriting) {
            mWriting = true;
            mWriteBtn.setVisibility(View.GONE);
            mCommentEditText.requestFocus();
        }
    }

    private void pauseWriting() {
        if (mWriting) {
            mWriting = false;
            mCommentEditText.clearFocus();
            mCommentView.setVisibility(View.GONE);
        }
    }

    private void stopWriting() {
        if (mWriting) {
            mWriting = false;
            mCommentEditText.setText("");
            mCommentEditText.clearFocus();
            mCommentView.setVisibility(View.GONE);
        }
    }

    private void popupDialog() {
        mDialogView.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mDialogView.getLayoutParams();
        lp.topMargin = mRootLayout.getHalfHeight() - DisplayUtil.dp2px(this, 170);
        ViewUtil.showLayoutDelay(mDialogView, 100);
    }

    private void popdownDialog() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mDialogView.getLayoutParams();
        lp.topMargin = mHalfScreenHeight;
        ViewUtil.requestLayoutDelay(mDialogView, 100);
    }

    @Override
    public void onTouch() {
        pauseWriting();
    }

    @Override
    public void onModeBtnClick() {
        if (DisplayUtil.isLandscape(getApplicationContext())) {
            mUserOrient = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mUserOrient = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onShareBtnClick() {
        mShareDialog.show();
    }

    @Override
    public void onBackBtnClick() {
        finish();
    }

    private Task.OnTaskListener onTaskListener = new Task.OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        @SuppressWarnings("unchecked")
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            List<Watch> watchs = (List<Watch>) event.getContext().get(GetMediaTask.KEY_RESULT);
            for (Watch watch : watchs) {
                if ("rtmp".equals(watch.getProtocol())) {
                    mUrl = watch.getWatchStringList().get(0);
                    break;
                }
            }
            if (mUrl != null) {
                Log.d(TAG, mUrl);
                mLivePlayerFragment.setupPlayer(mUrl);
            }
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
            // TODO Auto-generated method stub

        }
    };
}
