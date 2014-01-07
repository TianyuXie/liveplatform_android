package com.pplive.liveplatform.ui.player;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.pplive.media.player.MeetVideoView;
import android.pplive.media.player.MeetVideoView.DecodeMode;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.net.NetworkManager;
import com.pplive.liveplatform.ui.LivePlayerActivity;
import com.pplive.liveplatform.ui.UserpageActivity;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation.RotateListener;
import com.pplive.liveplatform.ui.dialog.DialogManager;
import com.pplive.liveplatform.ui.widget.VerticalSeekBar;
import com.pplive.liveplatform.ui.widget.image.CircularImageView;
import com.pplive.liveplatform.util.PPBoxUtil;
import com.pplive.liveplatform.util.TimeUtil;
import com.pplive.liveplatform.util.ViewUtil;

public class LivePlayerFragment extends Fragment implements View.OnTouchListener, View.OnClickListener, LivePlayerController.Callback,
        android.os.Handler.Callback {
    static final String TAG = "_LivePlayerFragment";

    private static final int TIMER_DELAY = 1000;

    private static final int SHOW_DELAY = 6000;

    private static final int HIDE = 301;

    private static final int FLAG_TITLE_BAR = 0x1;

    private static final int FLAG_BOTTOM_BAR = 0x2;

    private static final int FLAG_USER_VIEW = 0x4;

    private static final int FLAG_TIME_BAR = 0x8;

    private static final int FLAG_VOLUME_BAR = 0x10;

    private boolean mMuted = false;

    private boolean mVolUser = false;

    private int mSavedVolume = 5;

    private VerticalSeekBar mVolumeBar;

    private ImageView mVolumeIcon;

    private AudioManager mAudioManager;

    private MeetVideoView mVideoView;

    private LivePlayerController mController;

    private View mIconWrapper;

    private TextView mCountTextView;

    private TextView mFinishText;

    private View mRoot;

    private CircularImageView mUserIcon;

    private ToggleButton mModeBtn;

    private boolean mShowBar;

    private boolean mLoading;

    private String mIconUrl;

    private Callback mCallbackListener;

    private int mFlagMask;

    private int mViewFlags;

    private long mStartTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mStartTime = -1;
        mShowBar = true;
        mLoading = true;
        mFlagMask = 0xffffffff;
        mViewFlags = 0xffffffff;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mRoot = inflater.inflate(R.layout.layout_player_fragment, container, false);
        mVideoView = (MeetVideoView) mRoot.findViewById(R.id.live_player_videoview);
        mModeBtn = (ToggleButton) mRoot.findViewById(R.id.btn_player_mode);
        mCountTextView = (TextView) mRoot.findViewById(R.id.text_player_countdown);

        mController = (LivePlayerController) mRoot.findViewById(R.id.live_player_controller);
        mController.setCallbackListener(this);

        mVolumeBar = (VerticalSeekBar) mRoot.findViewById(R.id.seekbar_player_volume);
        mVolumeBar.setOnSeekBarChangeListener(mVolumeSeekListener);
        mVolumeIcon = (ImageView) mRoot.findViewById(R.id.image_player_volume);
        mVolumeIcon.setOnClickListener(mVolumeIconClickListener);

        mFinishText = (TextView) mRoot.findViewById(R.id.text_loading_finish);
        mIconWrapper = mRoot.findViewById(R.id.wrapper_player_user_icon);
        mUserIcon = (CircularImageView) mRoot.findViewById(R.id.btn_player_user_icon);
        mUserIcon.setOnClickListener(onUserBtnClickListener);

        mModeBtn.setOnClickListener(this);
        mRoot.setOnTouchListener(this);
        mRoot.findViewById(R.id.btn_player_share).setOnClickListener(this);
        mRoot.findViewById(R.id.btn_player_back).setOnClickListener(this);
        return mRoot;
    }

    public void setUserIcon(String url) {
        mIconWrapper.setVisibility(View.INVISIBLE);
        if (!TextUtils.isEmpty(url)) {
            mIconUrl = url;
            //preload the image
            mUserIcon.setImageAsync(mIconUrl, R.drawable.user_icon_default, imageLoadingListener);
        }
    }

    public void setupPlayerDirect(String url) {
        setupVideoView(url);
    }

    public void setupPlayer(final String url) {
        if (getActivity() != null) {
            switch (NetworkManager.getCurrentNetworkState()) {
            case WIFI:
            case UNKNOWN:
                setupVideoView(url);
                break;
            case THIRD_GENERATION:
            case MOBILE:
                DialogManager.alertMobileDialog(getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setupVideoView(url);
                    }
                }).show();
                break;
            case DISCONNECTED:
                DialogManager.alertNoNetworkDialog(getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setupVideoView(url);
                    }
                }).show();
                break;
            default:
                break;
            }
        }
    }

    public void syncVolume() {
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolumeBar.setProgress((int) (volume * 100.0 / maxVolume));
    }

    private OnClickListener mVolumeIconClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!mMuted) {
                mSavedVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mVolumeBar.setProgress(0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            } else {
                int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                mVolumeBar.setProgress((int) (mSavedVolume * 100.0 / maxVolume));
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mSavedVolume, 0);
            }
        }
    };

    private VerticalSeekBar.OnSeekBarChangeListener mVolumeSeekListener = new VerticalSeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(VerticalSeekBar seekBar) {
            Log.d(TAG, "onStopTrackingTouch");
            mVolUser = false;
            showBars(SHOW_DELAY);
        }

        @Override
        public void onStartTrackingTouch(VerticalSeekBar seekBar) {
            Log.d(TAG, "onStartTrackingTouch");
            mVolUser = true;
            showBars(0);
        }

        @Override
        public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
            mMuted = (progress <= 0);
            if (mVolUser) {
                float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int volume = (int) (maxVolume * progress / 100.0);
                if (volume > 0) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                } else if (progress > 0) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                } else {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                }
            }
            updateVolumeIcon(progress);
        }
    };

    public void updateVolumeIcon(int value) {
        if (mVolumeIcon != null && value > 0) {
            mVolumeIcon.setImageResource(R.drawable.player_volume_icon_small);
        } else {
            mVolumeIcon.setImageResource(R.drawable.player_volume_mute_icon_small);
        }
    }

    private void setupVideoView(String url) {
        Log.d(TAG, "setupVideoView:" + url);
        Uri uri = Uri.parse(url);
        mVideoView.setDecodeMode(DecodeMode.SW);
        mVideoView.setVideoURI(uri);
        mVideoView.setOnPreparedListener(mPreparedListener);
        mVideoView.setOnCompletionListener(mCompletionListener);
        mVideoView.setOnInfoListener(mInfoListener);
        mVideoView.setOnErrorListener(mErrorListener);
        mController.setMediaPlayer(mVideoView);
    }

    private View.OnClickListener onUserBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getActivity() != null) {
                Program program = (Program) getActivity().getIntent().getSerializableExtra(LivePlayerActivity.EXTRA_PROGRAM);
                Intent intent = new Intent(getActivity(), UserpageActivity.class);
                intent.putExtra(UserpageActivity.EXTRA_USER, program.getOwner());
                intent.putExtra(UserpageActivity.EXTRA_ICON, program.getOwnerIcon());
                intent.putExtra(UserpageActivity.EXTRA_NICKNAME, program.getOwnerNickname());
                getActivity().startActivity(intent);
            }
        }
    };

    public void showPPTVIcon(boolean show) {
        if (show) {
            mRoot.findViewById(R.id.image_player_pptv_icon).setVisibility(View.VISIBLE);
        } else {
            mRoot.findViewById(R.id.image_player_pptv_icon).setVisibility(View.GONE);
        }
    }

    public void setTitle(String title) {
        ((TextView) mRoot.findViewById(R.id.text_player_title)).setText(title);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (getActivity() != null) {
            mAudioManager = (AudioManager) getActivity().getSystemService(Service.AUDIO_SERVICE);
            syncVolume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mVideoView.resume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        mVideoView.pause();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        stopPlayback();
        super.onStop();
    }

    private void stopPlayback() {
        PPBoxUtil.closeM3U8();
        mVideoView.stopPlayback();
        mController.stop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mUserIcon.release();
        super.onDestroy();
    }

    private Handler mHandler = new Handler(this);

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
        case HIDE:
            hideBars();
            break;
        default:
            break;
        }

        return true;
    }

    private MeetVideoView.OnPreparedListener mPreparedListener = new MeetVideoView.OnPreparedListener() {

        @Override
        public void onPrepared() {
            Log.d(TAG, "MeetVideoView: onPrepared");
            if (mCallbackListener != null) {
                mCallbackListener.onPrepare();
            }
            mVideoView.start();
            mController.start();
        }
    };

    private MeetVideoView.OnInfoListener mInfoListener = new MeetVideoView.OnInfoListener() {

        @Override
        public boolean onInfo(int what, int extra) {
            if (what == MeetVideoView.MEDIA_INFO_BUFFERING_START) {
                mRoot.findViewById(R.id.layout_player_buffering).setVisibility(View.VISIBLE);
                return true;
            } else if (what == MeetVideoView.MEDIA_INFO_BUFFERING_END) {
                mRoot.findViewById(R.id.layout_player_buffering).setVisibility(View.GONE);
                return true;
            }
            return false;
        }

    };

    private MeetVideoView.OnCompletionListener mCompletionListener = new MeetVideoView.OnCompletionListener() {

        @Override
        public void onCompletion() {
            Log.d(TAG, "MeetVideoView: onCompletion");
            stopPlayback();
            if (mCallbackListener != null) {
                mCallbackListener.onCompletion();
            }
        }
    };

    private MeetVideoView.OnErrorListener mErrorListener = new MeetVideoView.OnErrorListener() {

        @Override
        public boolean onError(int what, int extra) {
            Log.d(TAG, "MeetVideoView: onError");
            stopPlayback();
            if (mCallbackListener != null) {
                return mCallbackListener.onError(what, extra);
            }
            return true;
        }
    };

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        switch (v.getId()) {
        case R.id.btn_player_mode:
            if (mCallbackListener != null) {
                mCallbackListener.onModeClick();
            }
            break;
        case R.id.btn_player_share:
            if (mCallbackListener != null) {
                mCallbackListener.onShareClick();
            }
            break;
        case R.id.btn_player_back:
            if (mCallbackListener != null) {
                mCallbackListener.onBackClick();
            }
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mCallbackListener != null) {
            mCallbackListener.onTouch();
        }
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    public void setLayout(boolean isFull, boolean isVod) {
        mModeBtn.setChecked(isFull);
        if (isFull) {
            mFlagMask = FLAG_TITLE_BAR | FLAG_VOLUME_BAR;
            if (isVod) {
                mFlagMask |= FLAG_TIME_BAR;
            }
        } else {
            mFlagMask = FLAG_TITLE_BAR | FLAG_BOTTOM_BAR | FLAG_USER_VIEW;
        }
        setVisibilityByFlags();
        showBars(SHOW_DELAY);
    }

    private void setVisibilityByFlags() {
        int flags = mViewFlags & mFlagMask;
        ViewUtil.setVisibility(mRoot.findViewById(R.id.layout_player_titlebar), flags & FLAG_TITLE_BAR);
        ViewUtil.setVisibility(mRoot.findViewById(R.id.layout_player_bottombar), flags & FLAG_BOTTOM_BAR);
        ViewUtil.setVisibility(mUserIcon, flags & FLAG_USER_VIEW);
        ViewUtil.setVisibility(mRoot.findViewById(R.id.wrapper_player_controller), flags & FLAG_TIME_BAR);
        ViewUtil.setVisibility(mRoot.findViewById(R.id.layout_player_volume), flags & FLAG_VOLUME_BAR);
    }

    private void hideBars() {
        if (!mShowBar || mLoading || mStartTime > 0) {
            return;
        }
        mShowBar = false;
        clearViewFlags(FLAG_TITLE_BAR | FLAG_BOTTOM_BAR | FLAG_USER_VIEW | FLAG_TIME_BAR | FLAG_VOLUME_BAR);
        setVisibilityByFlags();
        mHandler.removeMessages(HIDE);
    }

    public void showBars(int timeout) {
        if (!mShowBar) {
            mShowBar = true;
            setViewFlags(FLAG_TITLE_BAR | FLAG_BOTTOM_BAR | FLAG_USER_VIEW | FLAG_TIME_BAR | FLAG_VOLUME_BAR);
            setVisibilityByFlags();
            syncVolume();
        }
        mHandler.removeMessages(HIDE);
        if (timeout != 0) {
            mHandler.sendEmptyMessageDelayed(HIDE, timeout);
        }
    }

    private void clearViewFlags(int flags) {
        mViewFlags &= ~flags;
    }

    private void setViewFlags(int flags) {
        mViewFlags |= flags;
    }

    private GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTap");
            if (mShowBar) {
                hideBars();
            } else {
                showBars(SHOW_DELAY);
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(TAG, "onDoubleTap");
            mVideoView.switchDisplayMode();
            return true;
        }
    });

    public interface Callback {
        public void onTouch();

        public void onModeClick();

        public void onBackClick();

        public void onShareClick();

        public void onReplay();

        public void onPrepare();

        public void onCompletion();

        public boolean onError(int what, int extra);
    }

    public void setCallbackListener(Callback listener) {
        this.mCallbackListener = listener;
    }

    private ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {

        @Override
        public void onLoadingStarted(String arg0, View arg1) {
            Log.d(TAG, "onLoadingStarted");
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            Log.d(TAG, "onLoadingFailed");
            mUserIcon.setRounded(false);
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
            Log.d(TAG, "onLoadingComplete");
            mUserIcon.setRounded(arg2 != null);
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
            Log.d(TAG, "onLoadingCancelled");
            mUserIcon.setRounded(false);
        }
    };

    public void onStartPlay() {
        mUserIcon.setRounded(false);
        mUserIcon.setImageResource(R.drawable.home_status_btn_loading);
        mIconWrapper.setVisibility(View.VISIBLE);
        mFinishText.setText(R.string.player_finish);
        mRoot.findViewById(R.id.image_player_loading).setVisibility(View.GONE);
        rotateIcon();
    }

    public void onStartPrelive() {
        mUserIcon.setRounded(false);
        mUserIcon.setImageResource(R.drawable.home_status_btn_loading);
        mIconWrapper.setVisibility(View.VISIBLE);
        mFinishText.setText(R.string.player_prelive);
        rotateIcon();
    }

    public void initIcon() {
        mIconWrapper.clearAnimation();
        mIconWrapper.setVisibility(View.INVISIBLE);
        mUserIcon.setRounded(false);
        mUserIcon.setImageResource(R.drawable.home_status_btn_loading);
        showBars(0);
    }

    public void rotateIcon() {
        final float centerX = mIconWrapper.getWidth() / 2.0f;
        final float centerY = mIconWrapper.getHeight() / 2.0f;

        final Rotate3dAnimation rotation = new Rotate3dAnimation(0, 180, centerX, centerY, 1.0f, true);
        rotation.setStartOffset(1500);
        rotation.setDuration(350);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setRotateListener(rotateButtonListener);
        rotation.setAnimationListener(animationListener);
        mIconWrapper.startAnimation(rotation);
    }

    private RotateListener rotateButtonListener = new RotateListener() {
        @Override
        public void onRotateMiddle() {
            mFinishText.setText("");
            if (!TextUtils.isEmpty(mIconUrl)) {
                mUserIcon.setImageAsync(mIconUrl, R.drawable.user_icon_default, imageLoadingListener);
            } else {
                mUserIcon.setImageResource(R.drawable.user_icon_default);
            }
        }
    };

    private AnimationListener animationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mLoading = false;
            showBars(SHOW_DELAY);
            mModeBtn.setEnabled(true);
        }
    };

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        public void run() {
            Log.d(TAG, "Timer update");
            if (mStartTime - System.currentTimeMillis() > 15 * TimeUtil.MS_OF_MIN) {
                mCountTextView.setText("节目尚未开始");
                handler.postDelayed(this, 20 * TIMER_DELAY);
            } else {
                mCountTextView.setText("距离开播还有\n" + TimeUtil.stringForTimeHour(mStartTime - System.currentTimeMillis()));
                handler.postDelayed(this, TIMER_DELAY);
            }
        }
    };

    public void startTimer(long startTime) {
        Log.d(TAG, "start timer");
        mStartTime = startTime;
        mCountTextView.setVisibility(View.VISIBLE);
        handler.post(runnable);
    }

    public void stopTimer() {
        Log.d(TAG, "stop timer");
        mStartTime = -1;
        mCountTextView.setVisibility(View.GONE);
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onReplay() {
        if (mCallbackListener != null) {
            mCallbackListener.onReplay();
        }
    }

    public void setBreakVisibility(int visibility) {
        mRoot.findViewById(R.id.text_player_break).setVisibility(visibility);
    }

    @Override
    public void onShow(int timeout) {
        showBars(timeout);
    }

}
