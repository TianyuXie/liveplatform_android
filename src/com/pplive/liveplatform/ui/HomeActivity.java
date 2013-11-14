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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.home.HomeFragment;
import com.pplive.liveplatform.ui.widget.AnimDoor;
import com.pplive.liveplatform.ui.widget.LoadingButton;
import com.pplive.liveplatform.ui.widget.SideBar;
import com.pplive.liveplatform.ui.widget.slide.SlidableContainer;
import com.pplive.liveplatform.util.DisplayUtil;

public class HomeActivity extends FragmentActivity implements HomeFragment.Callback {
    static final String TAG = "HomepageActivity";

    private AnimDoor mAnimDoor;

    private View mStatusButtonWrapper;

    private Animation mStatusUpAnimation;

    private LoadingButton mStatusButton;

    private SlidableContainer mFragmentContainer;

    private SideBar mSideBar;

    private GestureDetector mGlobalDetector;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_home);

        mFragmentContainer = (SlidableContainer) findViewById(R.id.layout_home_fragment_container);
        mSideBar = (SideBar) findViewById(R.id.home_sidebar);
        mAnimDoor = (AnimDoor) findViewById(R.id.home_animdoor);
        mAnimDoor.setShutDoorListener(shutAnimationListener);

        mStatusButtonWrapper = findViewById(R.id.wrapper_home_status);

        mStatusButton = (LoadingButton) findViewById(R.id.btn_home_status);
        mStatusButton.setOnClickListener(onStatusClickListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomeFragment homepageFragment = new HomeFragment();
        homepageFragment.setCallbackListener(this);
        fragmentTransaction.add(R.id.layout_home_fragment_container, homepageFragment);
        fragmentTransaction.commit();

        mFragmentContainer.attachOnSlideListener(mSideBar);
        mFragmentContainer.attachOnSlideListener(homepageFragment);

        mGlobalDetector = new GestureDetector(getApplicationContext(), onGestureListener);

        float upPx = DisplayUtil.getHeightPx(this) / 2.0f - DisplayUtil.dp2px(this, 90);
        mStatusUpAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -upPx);
        mStatusUpAnimation.setFillAfter(true);
        mStatusUpAnimation.setDuration(700);
        mStatusUpAnimation.setAnimationListener(upAnimationListener);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(HomeActivity.this, LiveRecorderActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onDestroy() {
        mFragmentContainer.clearOnSlideListeners();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
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
        mStatusButton.setBackgroundResource(R.drawable.home_status_btn_bg, R.drawable.home_status_btn_loading);
        // mStatusButton.startLoading("正在加载");
    }

    @Override
    protected void onStop() {
        mStatusButtonWrapper.clearAnimation();
        mStatusButton.finishLoading();
        mAnimDoor.hide();
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
            mStatusButton.setBackgroundResource(R.drawable.home_status_btn_rotate);
            mStatusButton.setClickable(false);
            mAnimDoor.shut();
        }
    };

    private AnimationListener upAnimationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "upAnimationListener: clear");
            mStatusButton.startLoading();
            mHandler.sendEmptyMessageDelayed(0, 1200);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
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
            Log.d(TAG, "shutAnimationListener: clear");
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

}
