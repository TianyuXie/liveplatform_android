package com.pplive.liveplatform.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.home.HomeFragment;
import com.pplive.liveplatform.ui.widget.AnimDoor;
import com.pplive.liveplatform.ui.widget.LoadingButton;
import com.pplive.liveplatform.ui.widget.SideBar;
import com.pplive.liveplatform.ui.widget.slide.SlidableContainer;

public class HomeActivity extends FragmentActivity implements HomeFragment.Callback {
    static final String TAG = "HomepageActivity";

    private AnimDoor mAnimDoor;

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
    }

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
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        //        mStatusButton.startLoading("正在加载");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
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
            mAnimDoor.shut();
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
