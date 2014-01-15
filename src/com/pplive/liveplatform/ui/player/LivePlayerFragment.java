package com.pplive.liveplatform.ui.player;

import java.lang.ref.WeakReference;

import android.app.Service;
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
import com.pplive.liveplatform.ui.LivePlayerActivity;
import com.pplive.liveplatform.ui.UserpageActivity;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation.RotateListener;
import com.pplive.liveplatform.ui.widget.VerticalSeekBar;
import com.pplive.liveplatform.ui.widget.image.CircularImageView;
import com.pplive.liveplatform.util.PPBoxUtil;
import com.pplive.liveplatform.util.TimeUtil;
import com.pplive.liveplatform.util.ViewUtil;

public class LivePlayerFragment extends Fragment implements View.OnTouchListener, View.OnClickListener, LivePlayerController.Callback {
    static final String TAG = "_LivePlayerFragment";

    private static final int TIMER_DELAY = 1000;

    private static final int SHOW_DELAY = 6000;

    private static final int PLAYER_TIMEOUT_DELAY = 15000;

    private static final int MSG_HIDE = 301;

    private static final int MSG_TIMEOUT = 302;

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

    private TextView mBreakView;

    private View mRoot;

    private CircularImageView mUserIcon;

    private ToggleButton mModeBtn;

    private boolean mShowBar;

    private boolean mLoading;

    private int mFlagMask;

    private int mViewFlags;

    private long mStartTime;

    private int mSavedPostion;

    private Program mProgram;
    
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mHandler = new InnerHandler(this);
        mStartTime = -1;
        mSavedPostion = 0;
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
        mBreakView = (TextView) mRoot.findViewById(R.id.text_player_break);

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

        mRoot.setOnTouchListener(this);
        mModeBtn.setOnClickListener(this);
        mRoot.findViewById(R.id.btn_player_share).setOnClickListener(this);
        mRoot.findViewById(R.id.btn_player_back).setOnClickListener(this);
        return mRoot;
    }

    public void syncVolume() {
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mMuted = (volume <= 0);
        mVolumeBar.setProgress((int) (volume * 100.0 / maxVolume));
        updateVolumeIcon();
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
            mMuted = (progress <= 0);
            updateVolumeIcon();
        }
    };

    private void updateVolumeIcon() {
        if (mVolumeIcon != null && mMuted) {
            mVolumeIcon.setImageResource(R.drawable.player_volume_mute_icon_small);
        } else {
            mVolumeIcon.setImageResource(R.drawable.player_volume_icon_small);
        }
    }

    public void setupVideoView(String url) {
        Log.d(TAG, "setupVideoView:" + url);
        Uri uri = Uri.parse(url);
        mVideoView.setDecodeMode(DecodeMode.SW);
        mVideoView.setVideoURI(uri);
        mVideoView.setOnPreparedListener(mPreparedListener);
        mVideoView.setOnCompletionListener(mCompletionListener);
        mVideoView.setOnInfoListener(mInfoListener);
        mVideoView.setOnErrorListener(mErrorListener);
        mController.setMediaPlayer(mVideoView);
        mHandler.removeMessages(MSG_TIMEOUT);
        mHandler.sendEmptyMessageDelayed(MSG_TIMEOUT, PLAYER_TIMEOUT_DELAY);
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

    public void setProgram(Program program) {
        mProgram = program;
        ((TextView) mRoot.findViewById(R.id.text_player_title)).setText(program.getTitle());
        mIconWrapper.setVisibility(View.INVISIBLE);
        mUserIcon.setImageAsync(program.getOwnerIcon(), R.drawable.user_icon_default, imageLoadingListener);
        if (program.isOriginal()) {
            mRoot.findViewById(R.id.image_player_pptv_icon).setVisibility(View.VISIBLE);
        } else {
            mRoot.findViewById(R.id.image_player_pptv_icon).setVisibility(View.GONE);
        }
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
        if (mSavedPostion > 0 && mProgram.isVOD()) {
            mVideoView.seekTo(mSavedPostion);
            mSavedPostion = 0;
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        if (mVideoView.isPlaying() && mProgram.isVOD()) {
            mSavedPostion = mVideoView.getCurrentPosition();
        } else {
            mSavedPostion = 0;
        }
        mVideoView.pause();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        stopPlayback();
        super.onStop();
    }

    public void stopPlayback() {
        PPBoxUtil.closeM3U8();
        mVideoView.stopPlayback();
        mController.stop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mUserIcon.release();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private MeetVideoView.OnPreparedListener mPreparedListener = new MeetVideoView.OnPreparedListener() {

        @Override
        public void onPrepared() {
            Log.d(TAG, "MeetVideoView: onPrepared");
            mHandler.removeMessages(MSG_TIMEOUT);
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
            if (mProgram.isVOD()) {
                if (what == MeetVideoView.MEDIA_INFO_BUFFERING_START) {
                    mRoot.findViewById(R.id.layout_player_buffering).setVisibility(View.VISIBLE);
                    if (mCallbackListener != null) {
                        mCallbackListener.onBufferStart();
                    }
                    return true;
                } else if (what == MeetVideoView.MEDIA_INFO_BUFFERING_END) {
                    mRoot.findViewById(R.id.layout_player_buffering).setVisibility(View.GONE);
                    if (mCallbackListener != null) {
                        mCallbackListener.onBufferEnd();
                    }
                    return true;
                }
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
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mCallbackListener != null) {
            mCallbackListener.onTouchPlayer();
        }
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    public void setLayout(boolean isFull) {
        mModeBtn.setChecked(isFull);
        if (isFull) {
            mFlagMask = FLAG_TITLE_BAR | FLAG_VOLUME_BAR;
            if (mProgram.isVOD()) {
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
        ViewUtil.setVisibility(mIconWrapper, flags & FLAG_USER_VIEW);
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
        mHandler.removeMessages(MSG_HIDE);
    }

    public void showBars(int timeout) {
        if (!mShowBar) {
            mShowBar = true;
            setViewFlags(FLAG_TITLE_BAR | FLAG_BOTTOM_BAR | FLAG_USER_VIEW | FLAG_TIME_BAR | FLAG_VOLUME_BAR);
            setVisibilityByFlags();
            syncVolume();
        }
        mHandler.removeMessages(MSG_HIDE);
        if (timeout != 0) {
            mHandler.sendEmptyMessageDelayed(MSG_HIDE, timeout);
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
        public void onModeClick();

        public void onBackClick();

        public void onShareClick();

        public void onReplay();

        public void onPrepare();

        public void onTouchPlayer();

        public void onCompletion();

        public void onSeek();

        public void onBufferStart();

        public void onBufferEnd();

        public boolean onError(int what, int extra);

        public void onTimeout();
    }

    private Callback mCallbackListener;

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
//            mUserIcon.setRounded(false);
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
            Log.d(TAG, "onLoadingComplete");
//            mUserIcon.setRounded(arg2 != null);
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
            Log.d(TAG, "onLoadingCancelled");
//            mUserIcon.setRounded(false);
        }
    };

    public void onStartPlay() {
        mUserIcon.setLocalImage(R.drawable.home_status_btn_loading, false);
        mIconWrapper.setVisibility(View.VISIBLE);
        mFinishText.setText(R.string.player_finish);
        mRoot.findViewById(R.id.image_player_loading).setVisibility(View.GONE);
        rotateIcon();
    }

    public void onStartPrelive() {
        mUserIcon.setLocalImage(R.drawable.home_status_btn_loading, false);
        mIconWrapper.setVisibility(View.VISIBLE);
        mFinishText.setText(R.string.player_prelive);
        rotateIcon();
    }

    public void initIcon() {
        mIconWrapper.setVisibility(View.INVISIBLE);
        mUserIcon.clearAnimation();
        mUserIcon.setLocalImage(R.drawable.home_status_btn_loading, false);
        showBars(0);
    }

    public void rotateIcon() {
        final float centerX = mUserIcon.getWidth() / 2.0f;
        final float centerY = mUserIcon.getHeight() / 2.0f;

        final Rotate3dAnimation rotation = new Rotate3dAnimation(180, 360, centerX, centerY, 1.0f, true);
        rotation.setStartOffset(1500);
        rotation.setDuration(350);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setRotateListener(rotateButtonListener);
        rotation.setAnimationListener(animationListener);
        mUserIcon.startAnimation(rotation);
    }

    private RotateListener rotateButtonListener = new RotateListener() {
        @Override
        public void onRotateMiddle() {
            mFinishText.setText("");
            mUserIcon.setImageAsync(mProgram.getOwnerIcon(), R.drawable.user_icon_default, imageLoadingListener);
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

    private Handler mCountHandler = new Handler();

    private Runnable runnable = new Runnable() {
        public void run() {
            Log.d(TAG, "Timer update");
            if (mStartTime - System.currentTimeMillis() > 15 * TimeUtil.MS_OF_MIN) {
                mCountTextView.setText("节目尚未开始");
                mCountHandler.postDelayed(this, 20 * TIMER_DELAY);
            } else {
                mCountTextView.setText("距离开播还有\n" + TimeUtil.stringForTimeHour(mStartTime - System.currentTimeMillis()));
                mCountHandler.postDelayed(this, TIMER_DELAY);
            }
        }
    };

    public void startTimer() {
        Log.d(TAG, "start timer");
        mStartTime = mProgram.getStartTime();
        mCountTextView.setVisibility(View.VISIBLE);
        mCountHandler.post(runnable);
    }

    public void stopTimer() {
        Log.d(TAG, "stop timer");
        mStartTime = -1;
        mCountTextView.setVisibility(View.GONE);
        mCountHandler.removeCallbacks(runnable);
    }

    @Override
    public void onReplay() {
        if (mCallbackListener != null) {
            mCallbackListener.onReplay();
        }
    }

    public void hideBreakInfo() {
        mBreakView.setVisibility(View.GONE);
    }

    public void showBreakInfo(String message) {
        mBreakView.setVisibility(View.VISIBLE);
        mBreakView.setText(message);
    }

    @Override
    public void onShow(int timeout) {
        showBars(timeout);
    }

    @Override
    public void onSeek() {
        if (mCallbackListener != null) {
            mCallbackListener.onSeek();
        }
    }

    static class InnerHandler extends Handler {
        private WeakReference<LivePlayerFragment> mOuter;

        public InnerHandler(LivePlayerFragment outer) {
            mOuter = new WeakReference<LivePlayerFragment>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            LivePlayerFragment outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                case MSG_HIDE:
                    outer.hideBars();
                    break;
                case MSG_TIMEOUT:
                    if (outer.mCallbackListener != null) {
                        outer.mCallbackListener.onTimeout();
                    }
                    break;
                }
            }
        }
    }

}
