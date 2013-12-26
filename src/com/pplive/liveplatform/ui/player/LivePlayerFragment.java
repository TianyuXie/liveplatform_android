package com.pplive.liveplatform.ui.player;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
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
import com.pplive.liveplatform.ui.widget.image.CircularImageView;
import com.pplive.liveplatform.util.PPBoxUtil;
import com.pplive.liveplatform.util.ViewUtil;
import com.pplive.thirdparty.BreakpadUtil;

public class LivePlayerFragment extends Fragment implements View.OnTouchListener, View.OnClickListener, android.os.Handler.Callback {
    static final String TAG = "_LivePlayerFragment";

    private static final int HIDE = 301;

    private static final int SHOW_DELAY = 6000;

    private static final int FLAG_TITLE_BAR = 0x1;

    private static final int FLAG_BOTTOM_BAR = 0x2;

    private static final int FLAG_USER_VIEW = 0x4;

    private MeetVideoView mVideoView;

    private View mTitleBarView;

    private View mBottomBarView;

    private View mIconWrapper;

    private TextView mTitleTextView;

    private View mFinishText;

    private View mLoadingImage;

    private CircularImageView mUserIcon;

    private ToggleButton mModeBtn;

    private boolean mShowBar;

    private boolean mLoading;

    private String mIconUrl;

    private OnCompletionListener mOnCompletionListener;

    private OnErrorListener mOnErrorListener;

    private Callback mCallbackListener;

    private int mFlagMask;

    private int mViewFlags;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mShowBar = true;
        mLoading = true;
        mFlagMask = 0xffffffff;
        mViewFlags = 0xffffffff;
        BreakpadUtil.registerBreakpad(getActivity().getCacheDir());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View layout = inflater.inflate(R.layout.layout_player_fragment, container, false);
        mVideoView = (MeetVideoView) layout.findViewById(R.id.live_player_videoview);
        mModeBtn = (ToggleButton) layout.findViewById(R.id.btn_player_mode);
        mTitleTextView = (TextView) layout.findViewById(R.id.text_player_title);
        mLoadingImage = layout.findViewById(R.id.image_player_loading);
        Button shareBtn = (Button) layout.findViewById(R.id.btn_player_share);
        Button backBtn = (Button) layout.findViewById(R.id.btn_player_back);
        mBottomBarView = layout.findViewById(R.id.layout_player_bottombar);
        mTitleBarView = layout.findViewById(R.id.layout_player_titlebar);
        mFinishText = layout.findViewById(R.id.text_loading_finish);
        mIconWrapper = layout.findViewById(R.id.wrapper_player_user_icon);
        mUserIcon = (CircularImageView) layout.findViewById(R.id.btn_player_user_icon);
        mUserIcon.setOnClickListener(onUserBtnClickListener);
        layout.setOnTouchListener(this);
        mModeBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        return layout;
    }

    public void initUserIcon(String url) {
        if (!TextUtils.isEmpty(url)) {
            mIconUrl = url;
            mUserIcon.setImageAsync(url, R.drawable.user_icon_default, imageLoadingListener);
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

    private void setupVideoView(String url) {
        Log.d(TAG, "setupVideoView:" + url);
        Uri uri = Uri.parse(url);
        mVideoView.setDecodeMode(DecodeMode.SW);
        mVideoView.setVideoURI(uri);
        mVideoView.setOnPreparedListener(mPreparedListener);
        mVideoView.setOnCompletionListener(mCompletionListener);
        mVideoView.setOnErrorListener(mErrorListener);
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

    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
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
                mCallbackListener.onStartPlay();
            }
            mVideoView.start();
        }
    };

    private MeetVideoView.OnCompletionListener mCompletionListener = new MeetVideoView.OnCompletionListener() {

        @Override
        public void onCompletion() {
            Log.d(TAG, "MeetVideoView: onCompletion");
            stopPlayback();
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion();
            }
        }
    };

    private MeetVideoView.OnErrorListener mErrorListener = new MeetVideoView.OnErrorListener() {

        @Override
        public boolean onError(int what, int extra) {
            Log.d(TAG, "MeetVideoView: onError");
            stopPlayback();
            if (mOnErrorListener != null) {
                return mOnErrorListener.onError(what, extra);
            }
            return false;
        }
    };

    public interface OnCompletionListener {
        void onCompletion();
    }

    public interface OnErrorListener {
        boolean onError(int what, int extra);
    }

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

    public void setLayout(boolean isFull) {
        mModeBtn.setChecked(isFull);
        if (isFull) {
            mFlagMask = FLAG_TITLE_BAR;
        } else {
            mFlagMask = FLAG_TITLE_BAR | FLAG_BOTTOM_BAR | FLAG_USER_VIEW;
        }
        setVisibilityByFlags();
    }

    private void setVisibilityByFlags() {
        int flags = mViewFlags & mFlagMask;
        ViewUtil.setVisibility(mTitleBarView, flags & FLAG_TITLE_BAR);
        ViewUtil.setVisibility(mBottomBarView, flags & FLAG_BOTTOM_BAR);
        ViewUtil.setVisibility(mUserIcon, flags & FLAG_USER_VIEW);
    }

    private void hideBars() {
        if (!mShowBar || mLoading) {
            return;
        }
        mShowBar = false;
        clearViewFlags(FLAG_TITLE_BAR | FLAG_BOTTOM_BAR | FLAG_USER_VIEW);
        setVisibilityByFlags();
        mHandler.removeMessages(HIDE);
    }

    public void showBars(int timeout) {
        if (mShowBar) {
            return;
        }
        mShowBar = true;
        setViewFlags(FLAG_TITLE_BAR | FLAG_BOTTOM_BAR | FLAG_USER_VIEW);
        setVisibilityByFlags();
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
        public void onStartPlay();

        public void onTouch();

        public void onModeClick();

        public void onBackClick();

        public void onShareClick();

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
        mLoadingImage.setVisibility(View.GONE);
        rotateButton();
    }

    public void rotateButton() {
        // Find the center of the container
        final float centerX = mIconWrapper.getWidth() / 2.0f;
        final float centerY = mIconWrapper.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
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
            mFinishText.setVisibility(View.GONE);
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
            mHandler.removeMessages(HIDE);
            mHandler.sendEmptyMessageDelayed(HIDE, SHOW_DELAY);
            mModeBtn.setEnabled(true);
        }
    };
}
