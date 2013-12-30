package com.pplive.liveplatform.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
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

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation;
import com.pplive.liveplatform.ui.anim.Rotate3dAnimation.RotateListener;
import com.pplive.liveplatform.ui.home.HomeFragment;
import com.pplive.liveplatform.ui.widget.AnimDoor;
import com.pplive.liveplatform.ui.widget.LoadingButton;
import com.pplive.liveplatform.ui.widget.SideBar;
import com.pplive.liveplatform.ui.widget.slide.SlidableContainer;
import com.pplive.liveplatform.util.DisplayUtil;

public class HomeActivity extends FragmentActivity implements HomeFragment.Callback, SlidableContainer.OnSlideListener {
    static final String TAG = "_HomeActivity";

    private static final int TIME_BUTTON_UP = 400;

    private static final int TIME_BUTTON_SHOW_RESULT = 3000;

    private long mExitTime;

    private AnimDoor mAnimDoor;

    private View mStatusButtonWrapper;

    private Animation mStatusUpAnimation;

    private LoadingButton mStatusButton;

    private SlidableContainer mFragmentContainer;

    private SideBar mSideBar;

    private GestureDetector mGlobalDetector;

    private HomeFragment mHomeFragment;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        mFragmentContainer = (SlidableContainer) findViewById(R.id.layout_home_fragment_container);
        mSideBar = (SideBar) findViewById(R.id.home_sidebar);
        mSideBar.setOnTypeChangeListener(onTypeChangeListener);
        mAnimDoor = (AnimDoor) findViewById(R.id.home_animdoor);
        mAnimDoor.setShutDoorListener(shutAnimationListener);

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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.app_exit, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
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
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
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

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll");
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
            if (UserManager.getInstance(HomeActivity.this).isLogin()) {
                mStatusButton.setClickable(false);
                rotateButton();
                mAnimDoor.shut();
            } else {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.putExtra(LoginActivity.EXTRA_TAGET, LiveRecordActivity.class.getName());
                startActivity(intent);
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
            Intent intent = new Intent(HomeActivity.this, LiveRecordActivity.class);
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
        mStatusButtonHandler.sendEmptyMessageDelayed(0, TIME_BUTTON_SHOW_RESULT);
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

    private Handler mStatusButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mStatusButton.finishLoading();
        }
    };

    private RadioGroup.OnCheckedChangeListener onTypeChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Log.d(TAG, "onCheckedChanged");
            doSlideBack();
            switch (checkedId) {
            case R.id.btn_sidebar_original:
                Log.d(TAG, "btn_sidebar_original");
                mHomeFragment.switchSubject(1);
                break;
            case R.id.btn_sidebar_tv:
                Log.d(TAG, "btn_sidebar_tv");
                mHomeFragment.switchSubject(2);
                break;
            case R.id.btn_sidebar_game:
                Log.d(TAG, "btn_sidebar_game");
                mHomeFragment.switchSubject(3);
                break;
            case R.id.btn_sidebar_sport:
                Log.d(TAG, "btn_sidebar_sport");
                mHomeFragment.switchSubject(4);
                break;
            case R.id.btn_sidebar_finance:
                Log.d(TAG, "btn_sidebar_finance");
                mHomeFragment.switchSubject(5);
                break;
            default:
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

}
