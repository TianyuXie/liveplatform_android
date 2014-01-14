package com.pplive.liveplatform.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.TokenTask;
import com.pplive.liveplatform.dac.info.LocationInfo;
import com.pplive.liveplatform.dac.info.SessionInfo;
import com.pplive.liveplatform.location.Locator.LocationData;
import com.pplive.liveplatform.location.LocatorActivity;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation.RotateListener;
import com.pplive.liveplatform.ui.home.HomeFragment;
import com.pplive.liveplatform.ui.widget.AnimDoor;
import com.pplive.liveplatform.ui.widget.LoadingButton;
import com.pplive.liveplatform.ui.widget.SideBar;
import com.pplive.liveplatform.ui.widget.slide.SlidableContainer;
import com.pplive.liveplatform.update.Update;
import com.pplive.liveplatform.util.DisplayUtil;

public class HomeActivity extends LocatorActivity implements HomeFragment.Callback, SlidableContainer.OnSlideListener {

    static final String TAG = "_HomeActivity";

    private static final int TIME_BUTTON_UP = 400;

    private static final int TIME_BUTTON_SHOW_RESULT = 3000;

    private final static int MSG_RETRY_TOKEN = 2001;

    private final static int MSG_FINISH_LOADING = 2002;

    private long mExitTime;

    private Context mContext;

    private AnimDoor mAnimDoor;

    private View mStatusButtonWrapper;

    private Animation mStatusUpAnimation;

    private LoadingButton mStatusButton;

    private SlidableContainer mFragmentContainer;

    private SideBar mSideBar;

    private GestureDetector mGlobalDetector;

    private HomeFragment mHomeFragment;

    private View mHelpView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        mContext = this;

        mFragmentContainer = (SlidableContainer) findViewById(R.id.layout_home_fragment_container);
        mSideBar = (SideBar) findViewById(R.id.home_sidebar);
        mSideBar.setOnTypeChangeListener(onTypeChangeListener);
        mAnimDoor = (AnimDoor) findViewById(R.id.home_animdoor);
        mAnimDoor.setShutDoorListener(shutAnimationListener);
        mHelpView = findViewById(R.id.layout_home_help);

        mStatusButtonWrapper = findViewById(R.id.wrapper_home_status);
        mStatusButton = (LoadingButton) findViewById(R.id.btn_home_status);
        mStatusButton.setOnClickListener(onStatusClickListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mHomeFragment = new HomeFragment();
        mHomeFragment.setCallbackListener(this);
        fragmentTransaction.add(R.id.layout_home_fragment_container, mHomeFragment);
        fragmentTransaction.commit();

        mFragmentContainer.attachOnSlideListener(mSideBar);
        mFragmentContainer.attachOnSlideListener(mHomeFragment);
        mFragmentContainer.attachOnSlideListener(this);

        mGlobalDetector = new GestureDetector(getApplicationContext(), onGestureListener);

        float upPx = DisplayUtil.getHeightPx(this) / 2.0f - DisplayUtil.dp2px(this, 67.5f);
        mStatusUpAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -upPx);
        mStatusUpAnimation.setFillAfter(true);
        mStatusUpAnimation.setDuration(TIME_BUTTON_UP);
        mStatusUpAnimation.setAnimationListener(upAnimationListener);

        if (SettingsProvider.getInstance(this).isFirstHome()) {
            mHelpView.setVisibility(View.VISIBLE);
        } else {
            mHelpView.setVisibility(View.GONE);
        }
        Update.doUpdateAPP(this);
        if (UserManager.getInstance(mContext).isLogin()) {
            checkToken();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.app_exit, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                SessionInfo.reset();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        mSideBar.release();
        mFragmentContainer.clearOnSlideListeners();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        mStatusButton.init(R.drawable.home_status_btn_bg, R.drawable.home_status_btn_loading);
        mStatusButtonWrapper.clearAnimation();
        mAnimDoor.hide();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        mSideBar.updateUsername();
        if (!LocationInfo.isUpdated()) {
            startLocator();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        ImageLoader.getInstance().clearMemoryCache();
        super.onStop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGlobalDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {

        public boolean onDown(MotionEvent e) {
            if (mHelpView.getVisibility() == View.VISIBLE) {
                mHelpView.setVisibility(View.GONE);
                return true;
            } else {
                return false;
            }
        };

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //            Log.d(TAG, "onScroll");

            float absDistanceX = Math.abs(distanceX);
            float absDistanceY = Math.abs(distanceY);

            if (absDistanceX > absDistanceY) {
                if (distanceX > 10.0f) {
                    if (mFragmentContainer.slideBack()) {
                        return true;
                    }
                } else if (distanceX < -10.0f) {
                    if (mFragmentContainer.slide()) {
                        return true;
                    }
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    private View.OnClickListener onStatusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Constants.LARGER_THAN_OR_EQUAL_JELLY_BEAN) {
                if (UserManager.getInstance(mContext).isLoginSafely()) {
                    mStatusButton.setClickable(false);
                    rotateButton();
                    mAnimDoor.shut();
                } else {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.putExtra(LoginActivity.EXTRA_TAGET, LiveRecordActivity.class.getName());
                    startActivity(intent);
                }
            } else {
                Toast.makeText(mContext, R.string.toast_version_low, Toast.LENGTH_LONG).show();
            }
        }
    };

    private AnimationListener upAnimationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            mStatusButton.setBackgroundResource(R.drawable.home_status_btn_rotate);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(mContext, LiveRecordActivity.class);
            startActivity(intent);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

    };

    private RotateListener rotateListener = new RotateListener() {
        @Override
        public void onRotateMiddle() {
            mStatusButton.setBackgroundResource(R.drawable.home_status_btn_rotate_mirror);
        }
    };

    private AnimationListener shutAnimationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mStatusButtonWrapper.startAnimation(mStatusUpAnimation);
        }
    };

    @Override
    public void doSlide() {
        mFragmentContainer.slide();
    }

    @Override
    public void doSlideBack() {
        mFragmentContainer.slideBack();
    }

    @Override
    public void doLoadMore() {
        mStatusButton.startLoading(getString(R.string.home_loading));
    }

    @Override
    public void doLoadResult(String text) {
        mStatusButton.showLoadingResult(text);
        mHandler.sendEmptyMessageDelayed(MSG_FINISH_LOADING, TIME_BUTTON_SHOW_RESULT);
    }

    @Override
    public void doLoadFinish() {
        mStatusButton.finishLoading();
    }

    @Override
    public void doScrollDown(boolean isDown) {
        if (isDown) {
            mStatusButton.setBackgroundResource(R.drawable.home_status_btn_slide);
        } else {
            mStatusButton.setBackgroundResource(R.drawable.home_status_btn_bg);
        }
    }

    private void rotateButton() {
        final float centerX = mStatusButton.getWidth() / 2.0f;
        final float centerY = mStatusButton.getHeight() / 2.0f;

        final Rotate3dAnimation rotation = new Rotate3dAnimation(0, 180, centerX, centerY, 1.0f, true);
        rotation.setDuration(350);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setRotateListener(rotateListener);
        mStatusButtonWrapper.startAnimation(rotation);
    }

    private RadioGroup.OnCheckedChangeListener onTypeChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Log.d(TAG, "onCheckedChanged");
            doSlideBack();
            switch (checkedId) {
            case R.id.btn_sidebar_original:
                mHomeFragment.switchSubject(1);
                break;
            case R.id.btn_sidebar_tv:
                mHomeFragment.switchSubject(2);
                break;
            case R.id.btn_sidebar_game:
                mHomeFragment.switchSubject(3);
                break;
            case R.id.btn_sidebar_sport:
                mHomeFragment.switchSubject(4);
                break;
            case R.id.btn_sidebar_finance:
                mHomeFragment.switchSubject(5);
                break;
            }
        }
    };

    @Override
    public void onSlide() {
        mStatusButtonWrapper.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loadbtn_hide));
        mStatusButtonWrapper.setVisibility(View.GONE);
    }

    @Override
    public void onSlideBack() {
        mStatusButtonWrapper.setVisibility(View.VISIBLE);
        mStatusButtonWrapper.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loadbtn_show));
    }

    @Override
    public void onLocationUpdate(LocationData location) {
        if (location == null) {
            return;
        }
        LocationInfo.updateData(location);
    }

    @Override
    public void onLocationError(String message) {
    }

    private void checkToken() {
        if (!isFinishing()) {
            Log.d(TAG, "start to checkToken...");
            TokenTask task = new TokenTask();
            task.addTaskListener(onTokenTaskListener);
            TaskContext taskContext = new TaskContext();
            taskContext.set(TokenTask.KEY_USERNAME, UserManager.getInstance(this).getUsernamePlain());
            taskContext.set(TokenTask.KEY_PASSWORD, UserManager.getInstance(this).getPasswordPlain());
            taskContext.set(TokenTask.KEY_TOKEN, UserManager.getInstance(this).getToken());
            taskContext.set(TokenTask.KEY_THIRDPARTY, UserManager.getInstance(this).isThirdPartyLogin());
            taskContext.set(TokenTask.KEY_NEED_UPDATE, UserManager.getInstance(this).shouldUpdateToken());
            task.execute(taskContext);
        }
    }

    private Task.OnTaskListener onTokenTaskListener = new Task.OnTaskListener() {

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            Log.d(TAG, "checkToken: finished!!");
            String usrPlain = event.getContext().getString(TokenTask.KEY_USERNAME);
            String pwdPlain = event.getContext().getString(TokenTask.KEY_PASSWORD);
            String token = event.getContext().getString(TokenTask.KEY_TOKEN);
            UserManager.getInstance(mContext).login(usrPlain, pwdPlain, token);
            if ((Boolean) event.getContext().get(TokenTask.KEY_NEED_UPDATE, true)) {
                mHandler.removeMessages(MSG_RETRY_TOKEN);
                mHandler.sendEmptyMessageDelayed(MSG_RETRY_TOKEN, 10000);
            }
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "checkToken: failed!!");
            UserManager.getInstance(mContext).resetToken();
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "checkToken: timeout... Will retry immediately!");
            mHandler.removeMessages(MSG_RETRY_TOKEN);
            mHandler.sendEmptyMessage(MSG_RETRY_TOKEN);
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "checkToken: canceled!! Will retry after 30s...");
            mHandler.removeMessages(MSG_RETRY_TOKEN);
            mHandler.sendEmptyMessageDelayed(MSG_RETRY_TOKEN, 30000);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_RETRY_TOKEN:
                checkToken();
                break;
            case MSG_FINISH_LOADING:
                mStatusButton.finishLoading();
                break;
            }
        }
    };
}
