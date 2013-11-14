package com.pplive.liveplatform.ui;

import java.io.IOException;

import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.recorder.CameraManager;
import com.pplive.liveplatform.ui.recorder.LiveMediaRecoder;
import com.pplive.liveplatform.ui.widget.AnimDoor;
import com.pplive.liveplatform.ui.widget.DateTimePicker;
import com.pplive.liveplatform.ui.widget.DateTimePicker.OnDateTimeChangedListener;
import com.pplive.liveplatform.ui.widget.HorizontalListView;

public class LiveRecorderActivity extends FragmentActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = LiveRecorderActivity.class.getSimpleName();

    private SurfaceView mPreview;
    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera;
    private int mCurrentCameraId = CameraManager.CAMERA_FACING_BACK;
    private int mNumberofCameras = CameraManager.getInstance().getNumberOfCameras();
    private boolean mPreviewing = false;
    private boolean mConfigured = false;

    private LiveMediaRecoder mMediaRecorder;
    private boolean mRecording = false;

    private ToggleButton mBtnLiveRecord;
    private ToggleButton mBtnFlashLight;

    private EditText mEditLiveSchedule;
    private EditText mEditLiveTitle;

    private Button mBtnPrelive;

    private DateTimePicker mDateTimePacker;

    private HorizontalListView mHorizontalListView;

    private AnimDoor mAnimDoor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_live_recorder);

        mPreview = (SurfaceView) findViewById(R.id.preview_view);
        mSurfaceHolder = mPreview.getHolder();
        mSurfaceHolder.addCallback(this);

        mBtnLiveRecord = (ToggleButton) findViewById(R.id.btn_live_record);
        mBtnFlashLight = (ToggleButton) findViewById(R.id.btn_flash_light);

        mAnimDoor = (AnimDoor) findViewById(R.id.live_animdoor);
        mAnimDoor.setOpenDoorListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                mAnimDoor.hide();
            }
        });

        mEditLiveSchedule = (EditText) findViewById(R.id.edit_live_schedule);
        mEditLiveSchedule.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: " + (event.getAction() & MotionEvent.ACTION_MASK));

                int action = event.getAction() & MotionEvent.ACTION_MASK;
                if (MotionEvent.ACTION_UP != action) {
                    return true;
                }

                mDateTimePacker.showOrHide();

                return true;
            }
        });

        mEditLiveTitle = (EditText) findViewById(R.id.edit_live_title);

        mBtnPrelive = (Button) findViewById(R.id.btn_live_prelive);

        mDateTimePacker = (DateTimePicker) findViewById(R.id.calendar_pick_container);
        mDateTimePacker.setOnDateTimeChanged(new OnDateTimeChangedListener() {

            @Override
            public void onDateTimeChanged(int year, int month, int day, int hour, int minute) {
                mEditLiveSchedule.setText(String.format("%d/%d/%d %d:%d", year, month, day, hour, minute));
            }
        });

        mHorizontalListView = (HorizontalListView) findViewById(R.id.live_list_view);
        mHorizontalListView.setAdapter(new BaseAdapter() {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_record_program_itemview, null);

                return view;
            }

            @Override
            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return position;
            }

            @Override
            public Object getItem(int position) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return 40;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCamera = CameraManager.getInstance().open(mCurrentCameraId);

        startPreview();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Log.d(TAG, "open");
            mAnimDoor.open();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mRecording) {
            stopRecording();
        }

        stopPreview();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;

        initCamera();
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void initCamera() {
        if (null != mCamera) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);

                Parameters params = mCamera.getParameters();

                Camera.Size size = CameraManager.getInstance().getMiniSize(params);

                params.setPreviewSize(size.width, size.height);
                params.setPreviewFormat(ImageFormat.NV21);

                mCamera.setParameters(params);
                mConfigured = true;
            } catch (IOException e) {
                Log.w(TAG, "Init camera failed. ", e);
            }
        }
    }

    private boolean setFlashMode(boolean isFlashOn) {
        Log.d(TAG, "isFlashOn: " + isFlashOn + "; Status: " + mBtnFlashLight.getText());

        if (null != mCamera) {
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(isFlashOn ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);

            mCamera.setParameters(params);
        }

        return isFlashOn;
    }

    private void startPreview() {
        if (mConfigured && !mPreviewing && null != mCamera) {
            mCamera.startPreview();
            mPreviewing = true;
        }
    }

    private void stopPreview() {
        if (null != mCamera) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;

            mPreviewing = false;
        }
    }

    private void startRecording() {
        if (!mRecording) {
            mMediaRecorder = new LiveMediaRecoder(getApplicationContext(), mCamera);

            mMediaRecorder.start();

            mRecording = true;
        }
    }

    private void stopRecording() {
        if (mRecording) {
            mMediaRecorder.stop();

            mRecording = false;
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        switch (v.getId()) {
        case R.id.btn_camera_change:
            onClickBtnCameraChange(v);
            break;
        case R.id.btn_live_record:
            onClickBtnLiveRecord(v);
            break;
        case R.id.btn_flash_light:
            onClickBtnFlashLight(v);
            break;
        case R.id.btn_live_prelive:
            onClickBtnPrelive(v);
            break;
        default:
            break;
        }
    }

    private void onClickBtnCameraChange(View v) {
        if (mRecording) {
            stopRecording();
        }

        stopPreview();

        mCurrentCameraId = (mCurrentCameraId + 1) % mNumberofCameras;
        mCamera = CameraManager.getInstance().open(mCurrentCameraId);

        initCamera();
        startPreview();
    }

    private void onClickBtnLiveRecord(View v) {
        if (null != mCamera) {
            if (!mRecording) {
                startRecording();
            } else {
                stopRecording();
            }

            mBtnLiveRecord.setChecked(mRecording);
        }
    }

    private void onClickBtnFlashLight(View v) {
        boolean isFlashOn = mBtnFlashLight.isChecked();

        isFlashOn = setFlashMode(isFlashOn);
        mBtnFlashLight.setChecked(isFlashOn);
    }

    private void onClickBtnPrelive(View v) {
        if (View.VISIBLE != mEditLiveSchedule.getVisibility()) {
            mEditLiveSchedule.setVisibility(View.VISIBLE);
            mEditLiveTitle.setFocusable(true);
            mEditLiveTitle.setFocusableInTouchMode(true);

            mDateTimePacker.showOrHide();
        } else {

        }
    }

}
