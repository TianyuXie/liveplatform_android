package com.pplive.liveplatform.ui.player;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.pplive.media.player.MediaController;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.util.TimeUtil;

public class LivePlayerController extends MediaController {

    private boolean mShowing;

    private boolean mDragging;

    private ProgressBar mPlayerProgressBar;

    private TextView mEndTime, mCurrentTime;

    private ImageButton mPlayPauseButton;

    private static final int sDefaultTimeout = 30000;

    private static final int FADE_OUT = 1;

    private static final int SHOW_PROGRESS = 2;

    public LivePlayerController(Context context) {
        super(context);
    }

    public LivePlayerController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        initControllerView(mRoot);
    }

    private void initControllerView(View v) {
        // TODO
        mPlayPauseButton = (ImageButton) v.findViewById(R.id.play_pause_button);
        if (mPlayPauseButton != null) {
            mPlayPauseButton.requestFocus();
            mPlayPauseButton.setOnClickListener(mPauseListener);
        }

        mPlayerProgressBar = (ProgressBar) v.findViewById(R.id.player_seekbar);
        if (mPlayerProgressBar != null) {
            if (mPlayerProgressBar instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mPlayerProgressBar;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mPlayerProgressBar.setMax(1000);
        }
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        super.setMediaPlayer(player);
        updatePausePlay();
    }

    @Override
    protected void doPauseResume() {
        super.doPauseResume();
        updatePausePlay();
    }

    public boolean switchVisibility() {
        if (mShowing) {
            hide();
        } else {
            show();
        }
        return mShowing;
    }

    /**
     * Show the controller on screen. It will go away automatically after 3 seconds of inactivity.
     */
    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Show the controller on screen. It will go away automatically after 'timeout' milliseconds of inactivity.
     * 
     * @param timeout
     *            The timeout in milliseconds. Use 0 to show the controller until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing) {
            setProgress();
            if (mPlayPauseButton != null) {
                mPlayPauseButton.requestFocus();
            }
            disableUnsupportedButtons();
            mRoot.setVisibility(VISIBLE);
            mShowing = true;
        }
        updatePausePlay();

        // cause the progress bar to be updated even if mShowing
        // was already true. This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (mShowing) {
            mRoot.setVisibility(View.GONE);
            mHandler.removeMessages(SHOW_PROGRESS);
            mShowing = false;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
            case FADE_OUT:
                hide();
                break;
            case SHOW_PROGRESS:
                pos = setProgress();
                if (!mDragging && mShowing && mPlayer.isPlaying()) {
                    msg = obtainMessage(SHOW_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                }
                break;
            }
        }
    };

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        Log.d("Controller", "duration:" + duration);
        Log.d("Controller", "position:" + position);

        if (mPlayerProgressBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mPlayerProgressBar.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mPlayerProgressBar.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null)
            mEndTime.setText(TimeUtil.stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(TimeUtil.stringForTime(position));

        return position;
    }

    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked. This requires the control interface to be
     * a MediaPlayerControlExt
     */
    private void disableUnsupportedButtons() {
        try {
            if (mPlayPauseButton != null && mPlayer != null && !mPlayer.canPause()) {
                mPlayPauseButton.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ex) {
            // We were given an old version of the interface, that doesn't
            // have
            // the canPause/canSeekXYZ methods. This is OK, it just means we
            // assume the media can be paused and seeked, and so we don't
            // disable the buttons.
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPlayPauseButton != null) {
            mPlayPauseButton.setEnabled(enabled);
        }
        if (mPlayerProgressBar != null) {
            mPlayerProgressBar.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                doPauseResume();
                show();
                if (mPlayPauseButton != null) {
                    mPlayPauseButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !mPlayer.isPlaying()) {
                mPlayer.start();
                updatePausePlay();
                show();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (uniqueDown && mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
                show();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE
                || keyCode == KeyEvent.KEYCODE_CAMERA) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide();
            }
            return true;
        }

        show();
        return super.dispatchKeyEvent(event);
    }

    private void updatePausePlay() {
        if (mRoot == null || mPlayPauseButton == null)
            return;
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayPauseButton.setImageResource(R.drawable.tmp_player_pause);
        } else {
            mPlayPauseButton.setImageResource(R.drawable.tmp_player_play);
        }
    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show();
        }
    };

    // There are two scenarios that can trigger the seekbar listener to
    // trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed
    // by
    // a number of onProgressChanged notifications, concluded by
    // onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the
    // dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in
    // this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch
    // notifications,
    // we will simply apply the updated position without suspending regular
    // updates.
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);

            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                // We're not interested in programmatically generated changes
                // to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / 1000L;
            Log.d("Controller", "duration:" + duration);
            Log.d("Controller", "newposition:" + newposition);
            mPlayer.seekTo((int) newposition);
            if (mCurrentTime != null)
                mCurrentTime.setText(TimeUtil.stringForTime((int) newposition));
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show();

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };
}
