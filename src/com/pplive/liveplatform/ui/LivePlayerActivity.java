package com.pplive.liveplatform.ui;

import java.util.Collection;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.comment.model.FeedDetailList;
import com.pplive.liveplatform.core.service.live.model.Watch;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.player.GetFeedTask;
import com.pplive.liveplatform.core.task.player.GetMediaTask;
import com.pplive.liveplatform.core.task.player.PutFeedTask;
import com.pplive.liveplatform.ui.player.LivePlayerFragment;
import com.pplive.liveplatform.ui.widget.DetectableRelativeLayout;
import com.pplive.liveplatform.ui.widget.EnterSendEditText;
import com.pplive.liveplatform.ui.widget.dialog.LoadingDialog;
import com.pplive.liveplatform.ui.widget.dialog.ShareDialog;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.ViewUtil;

public class LivePlayerActivity extends FragmentActivity implements SensorEventListener, LivePlayerFragment.Callback {
    static final String TAG = "_LivePlayerActivity";

    private static final int SCREEN_ORIENTATION_INVALID = -1;

    private final static int MSG_LOADING_DELAY = 2000;

    private final static int MSG_MEDIA_FINISH = 2001;

    private final static int MSG_GET_FEED = 2500;

    private final static int LOADING_DELAY_TIME = 3000;

    private TaskContext mFeedContext;

    private DetectableRelativeLayout mRootLayout;

    private LivePlayerFragment mLivePlayerFragment;

    private View mFragmentContainer;

    private View mChatView;

    private View mCommentView;

    private View mLoadingView;

    private TextView mChatTextView;

    private Dialog mShareDialog;

    private Dialog mLoadingDialog;

    private Button mWriteBtn;

    private EnterSendEditText mCommentEditText;

    private SensorManager mSensorManager;

    private Sensor mSensor;

    private int mCurrentOrient;

    private int mUserOrient;

    private boolean mIsFull;

    private boolean mWriting;

    private boolean mFirstLoading;

    private boolean mLoadingFinish;

    private boolean mLoadingDelayed;

    private int mHalfScreenHeight;

    private String mUrl;

    private Context mContext;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Log.d(TAG, "onCreate");
        this.mContext = this;

        /* init window */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_player);

        /* init fragment */
        mLivePlayerFragment = new LivePlayerFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.layout_player_fragment, mLivePlayerFragment).commit();
        mShareDialog = new ShareDialog(this, R.style.share_dialog, getString(R.string.share_dialog_title));
        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.setOnKeyListener(onLoadingKeyListener);

        /* init values */
        mUserOrient = SCREEN_ORIENTATION_INVALID;
        mCurrentOrient = getRequestedOrientation();
        mHalfScreenHeight = (int) (DisplayUtil.getWidthPx(this) * 3.0f / 4.0f);

        /* init views */
        mRootLayout = (DetectableRelativeLayout) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        mRootLayout.setOnSoftInputListener(onSoftInputListener);
        mCommentEditText = (EnterSendEditText) findViewById(R.id.edit_player_comment);
        mCommentEditText.setOnEnterListener(onCommentEnterListener);
        mCommentView = findViewById(R.id.layout_player_comment);
        mFragmentContainer = findViewById(R.id.layout_player_fragment);
        mChatView = findViewById(R.id.layout_player_dialog);
        mLoadingView = findViewById(R.id.layout_player_loading);
        mChatTextView = (TextView) findViewById(R.id.text_player_dialog);
        mWriteBtn = (Button) findViewById(R.id.btn_player_write);
        mWriteBtn.setOnClickListener(onWriteBtnClickListener);
        mChatView.setOnTouchListener(onDialogTouchListener);
        setLayout(DisplayUtil.isLandscape(this), true);

        /* init others */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mFeedContext = new TaskContext();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mLivePlayerFragment.setLayout(mIsFull);
        mLivePlayerFragment.setCallbackListener(this);
        mLivePlayerFragment.setTitle(getIntent().getStringExtra("title"));
        if (mUrl == null) {
            String username = UserManager.getInstance(this).getActiveUserPlain();
            String token = UserManager.getInstance(this).getToken();
            long pid = getIntent().getLongExtra("pid", -1);
            if (pid != -1) {
                Log.d(TAG, "pid: " + pid);
                showLoading();
                mLoadingHandler.sendEmptyMessageDelayed(MSG_LOADING_DELAY, LOADING_DELAY_TIME);
                GetMediaTask mediaTask = new GetMediaTask();
                mediaTask.addTaskListener(onGetMediaListener);
                GetFeedTask feedTask = new GetFeedTask();
                feedTask.addTaskListener(onGetFeedListener);
                TaskContext taskContext = new TaskContext();
                taskContext.set(Task.KEY_PID, pid);
                taskContext.set(Task.KEY_USERNAME, username);
                taskContext.set(Task.KEY_TOKEN, token);
                mFeedContext.set(Task.KEY_PID, pid);
                mFeedContext.set(Task.KEY_TOKEN, token);
                mediaTask.execute(taskContext);
                feedTask.execute(mFeedContext);
            }
        } else {
            Log.d(TAG, "onStart mUrl:" + mUrl);
            mLivePlayerFragment.setupPlayer(mUrl);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mFeedHandler.removeMessages(MSG_GET_FEED);
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
            mChatView.setVisibility(View.GONE);
            mCommentView.setVisibility(View.GONE);
            mWriteBtn.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            RelativeLayout.LayoutParams dialogLp = (RelativeLayout.LayoutParams) mChatView.getLayoutParams();
            containerLp.height = mHalfScreenHeight;
            dialogLp.topMargin = mHalfScreenHeight;
            mChatView.setVisibility(View.VISIBLE);
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

    private View.OnClickListener onWriteBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (UserManager.getInstance(mContext).isLogin()) {
                startWriting();
            } else {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        }
    };

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (mCurrentOrient != requestedOrientation) {
            Log.d(TAG, "setRequestedOrientation");
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

    private EnterSendEditText.OnEnterListener onCommentEnterListener = new EnterSendEditText.OnEnterListener() {

        @Override
        public boolean onEnter(View v) {
            long pid = getIntent().getLongExtra("pid", -1);
            if (pid != -1) {
                String content = mCommentEditText.getText().toString();
                String token = UserManager.getInstance(mContext).getToken();
                TaskContext taskContext = new TaskContext();
                taskContext.set(Task.KEY_PID, pid);
                taskContext.set(PutFeedTask.KEY_CONTENT, content);
                taskContext.set(Task.KEY_TOKEN, token);
                postFeed(taskContext);
            }
            stopWriting();
            return true;
        }
    };

    private DialogInterface.OnKeyListener onLoadingKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                finish();
                return true;
            }
            return false;
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
        mChatView.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mChatView.getLayoutParams();
        lp.topMargin = mRootLayout.getHalfHeight() - DisplayUtil.dp2px(this, 170);
        ViewUtil.showLayoutDelay(mChatView, 100);
    }

    private void popdownDialog() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mChatView.getLayoutParams();
        lp.topMargin = mHalfScreenHeight;
        ViewUtil.requestLayoutDelay(mChatView, 100);
    }

    @Override
    public void onTouch() {
        pauseWriting();
    }

    @Override
    public void onModeClick() {
        if (DisplayUtil.isLandscape(getApplicationContext())) {
            mUserOrient = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mUserOrient = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onShareClick() {
        mShareDialog.show();
    }

    @Override
    public void onBackClick() {
        finish();
    }

    private void postFeed(TaskContext taskContext) {
        PutFeedTask feedTask = new PutFeedTask();
        feedTask.setBackContext(taskContext);
        feedTask.addTaskListener(onPutFeedListener);
        feedTask.execute(taskContext);
    }

    private Task.OnTaskListener onPutFeedListener = new Task.OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "onPutFeedTaskListener onTimeout");
        }

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            Log.d(TAG, "onPutFeedTaskListener onTaskFinished");
            mFeedHandler.removeMessages(MSG_GET_FEED);
            mFeedHandler.sendEmptyMessage(MSG_GET_FEED);
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "onPutFeedTaskListener onTaskFailed:" + event.getMessage());
            //            postFeed(event.getContext());
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "onPutFeedTaskListener onTaskCancel");
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

    private Task.OnTaskListener onGetFeedListener = new Task.OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "FeedTask: onTimeout");
            mFeedHandler.removeMessages(MSG_GET_FEED);
            mFeedHandler.sendEmptyMessage(MSG_GET_FEED);
        }

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            Log.d(TAG, "FeedTask: onTaskFinished");
            FeedDetailList feeds = (FeedDetailList) event.getContext().get(GetFeedTask.KEY_RESULT);
            if (feeds != null) {
                mChatTextView.setText("");
                Collection<String> contents = feeds.getFeedStrings(getResources().getColor(R.color.player_dialog_nickname),
                        getResources().getColor(R.color.player_dialog_content));
                if (contents.size() != 0) {
                    findViewById(R.id.text_player_no_chat).setVisibility(View.GONE);
                    for (String content : contents) {
                        mChatTextView.append(Html.fromHtml(content.toString()));
                    }
                } else {
                    findViewById(R.id.text_player_no_chat).setVisibility(View.VISIBLE);
                }
            }
            mFeedHandler.removeMessages(MSG_GET_FEED);
            mFeedHandler.sendEmptyMessageDelayed(MSG_GET_FEED, GetFeedTask.DELAY_TIME_SHORT);
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "FeedTask: onTaskFailed");
            mFeedHandler.removeMessages(MSG_GET_FEED);
            mFeedHandler.sendEmptyMessageDelayed(MSG_GET_FEED, GetFeedTask.DELAY_TIME_SHORT * 2);
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "FeedTask: onTaskCancel");
            mFeedHandler.removeMessages(MSG_GET_FEED);
            mFeedHandler.sendEmptyMessageDelayed(MSG_GET_FEED, GetFeedTask.DELAY_TIME_SHORT * 2);
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

    private Task.OnTaskListener onGetMediaListener = new Task.OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            //TODO timeout
            Log.d(TAG, "MediaTask onTimeout");
            Toast.makeText(mContext, R.string.toast_timeout, Toast.LENGTH_SHORT).show();
            mLoadingHandler.sendEmptyMessage(MSG_MEDIA_FINISH);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            // TODO rtmp or live2
            List<Watch> watchs = (List<Watch>) event.getContext().get(GetMediaTask.KEY_RESULT);
            for (Watch watch : watchs) {
                Log.d(TAG, "Protocol:" + watch.getProtocol());
            }
            for (Watch watch : watchs) {
                if ("rtmp".equals(watch.getProtocol())) {
                    mUrl = watch.getWatchStringList().get(0);
                    break;
                }
            }
            if (TextUtils.isEmpty(mUrl)) {
                mUrl = watchs.get(0).getWatchStringList().get(0);
            }
            if (!TextUtils.isEmpty(mUrl)) {
                Log.d(TAG, "MediaTask onTaskFinished:" + mUrl);
                mLivePlayerFragment.setupPlayer(mUrl);
            } else {
                finish();
            }
            mLoadingHandler.sendEmptyMessage(MSG_MEDIA_FINISH);
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            //TODO failed
            Log.d(TAG, "MediaTask onTaskFailed");
            Toast.makeText(mContext, R.string.toast_failed, Toast.LENGTH_SHORT).show();
            mLoadingHandler.sendEmptyMessage(MSG_MEDIA_FINISH);
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "MediaTask onTaskCancel");
            finish();
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

    private View.OnTouchListener onDialogTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            onDialogGestureDetector.onTouchEvent(event);
            return false;
        }
    };

    private GestureDetector onDialogGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            pauseWriting();
            return true;
        }
    });

    public void showLoading() {
        if (!mFirstLoading) {
            mFirstLoading = true;
            mLoadingView.setVisibility(View.VISIBLE);
            mLoadingDialog.show();
        }
    }

    public void hideLoading() {
        if (mFirstLoading) {
            mFirstLoading = false;
            mLoadingView.setVisibility(View.GONE);
            mLoadingDialog.dismiss();
        }
    }

    private Handler mLoadingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_LOADING_DELAY:
                mLoadingDelayed = true;
                break;
            case MSG_MEDIA_FINISH:
                mLoadingFinish = true;
                break;
            }
            if (mLoadingFinish && mLoadingDelayed && !isFinishing() && mFirstLoading) {
                hideLoading();
            }
        }
    };

    private Handler mFeedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_GET_FEED:
                GetFeedTask feedTask = new GetFeedTask();
                feedTask.addTaskListener(onGetFeedListener);
                feedTask.execute(mFeedContext);
                mFeedHandler.removeMessages(MSG_GET_FEED);
                break;
            }
        }
    };

    @Override
    public void onStartPlay() {
        //TODO
    }
}
