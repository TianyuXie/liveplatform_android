package com.pplive.liveplatform.ui.player;

import java.io.File;

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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.util.ViewUtil;
import com.pplive.sdk.MediaSDK;
import com.pplive.thirdparty.BreakpadUtil;

public class LivePlayerFragment extends Fragment implements OnTouchListener, View.OnClickListener {
    static final String TAG = "_LivePlayerFragment";

    private static final int HIDE = 301;

    private static final int FLAG_TITLE_BAR = 0x1;

    private static final int FLAG_BOTTOM_BAR = 0x2;

    private static final int FLAG_USER_VIEW = 0x4;

    private MeetVideoView mVideoView;

    private View mTitleBarView;

    private View mBottomBarView;

    private View mUserView;

    private TextView mTitleTextView;

    private ToggleButton mModeBtn;

    private boolean mShowBar;

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
        Button shareBtn = (Button) layout.findViewById(R.id.btn_player_share);
        mBottomBarView = layout.findViewById(R.id.layout_player_bottombar);
        mTitleBarView = layout.findViewById(R.id.layout_player_titlebar);
        mUserView = layout.findViewById(R.id.layout_player_user);
        layout.setOnTouchListener(this);
        mModeBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        return layout;
    }

    public void setupPlayer(String url) {
        if (getActivity() != null) {
            // TODO: test code
            File cacheDirFile = getActivity().getCacheDir();
            String dataDir = cacheDirFile.getParentFile().getAbsolutePath();
            String libDir = dataDir + "/lib";
            String tmpDir = cacheDirFile.getAbsolutePath();
            File tmpDirFile = new File(tmpDir);
            tmpDirFile.mkdir();

            MediaSDK.libPath = libDir;
            MediaSDK.logPath = tmpDir;
            MediaSDK.logLevel = MediaSDK.LEVEL_EVENT;
            MediaSDK.startP2PEngine("161", "12", "111");

            Uri uri = Uri.parse(url);
            //        uri = Uri.parse(ConfigUtil.getString(Keys.PLAY_TEST_URL));
            mVideoView.setDecodeMode(DecodeMode.HW_SYSTEM);
            mVideoView.setVideoURI(uri);
            mVideoView.setOnPreparedListener(mPreparedListener);
            mVideoView.setOnCompletionListener(mCompletionListener);
            mVideoView.setOnErrorListener(mErrorListener);
        }
    }

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
        mVideoView.stopPlayback();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case HIDE:
                hideBars();
                break;
            }
        }
    };

    private MeetVideoView.OnPreparedListener mPreparedListener = new MeetVideoView.OnPreparedListener() {
        @Override
        public void onPrepared() {
            mVideoView.start();
        }
    };

    private MeetVideoView.OnCompletionListener mCompletionListener = new MeetVideoView.OnCompletionListener() {
        public void onCompletion() {
            mVideoView.stopPlayback();
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion();
            }
        }
    };

    private MeetVideoView.OnErrorListener mErrorListener = new MeetVideoView.OnErrorListener() {
        public boolean onError(int what, int extra) {
            mVideoView.stopPlayback();
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
                mCallbackListener.onModeBtnClick();
            }
            break;
        case R.id.btn_player_share:
            if (mCallbackListener != null) {
                mCallbackListener.onShareBtnClick();
            }
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch");
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
        mHandler.removeMessages(HIDE);
        mHandler.sendEmptyMessageDelayed(HIDE, 6000);
    }

    private void setVisibilityByFlags() {
        int flags = mViewFlags & mFlagMask;
        ViewUtil.setVisibility(mTitleBarView, flags & FLAG_TITLE_BAR);
        ViewUtil.setVisibility(mBottomBarView, flags & FLAG_BOTTOM_BAR);
        ViewUtil.setVisibility(mUserView, flags & FLAG_USER_VIEW);
    }

    private void hideBars() {
        if (!mShowBar) {
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
                showBars(6000);
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

        public void onModeBtnClick();

        public void onShareBtnClick();

    }

    public void setCallbackListener(Callback listener) {
        this.mCallbackListener = listener;
    }

}
