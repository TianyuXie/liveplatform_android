package com.pplive.liveplatform.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.home.HomeFragment;
import com.pplive.liveplatform.ui.widget.FragmentContainer;
import com.pplive.liveplatform.ui.widget.SideBar;

public class HomeActivity extends FragmentActivity {
    static final String TAG = "HomepageActivity";

    private FragmentContainer mFragmentContainer;

    private SideBar mSideBar;

    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_home);

        mFragmentContainer = (FragmentContainer) findViewById(R.id.layout_home_fragment_container);
        mSideBar = (SideBar) findViewById(R.id.sidebar_home);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment homepageFragment = new HomeFragment();
        fragmentTransaction.add(R.id.layout_home_fragment, homepageFragment);
        fragmentTransaction.commit();

        mGestureDetector = new GestureDetector(getApplicationContext(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
                        if (mFragmentContainer != null && mSideBar != null) {
                            if (!mFragmentContainer.isSlided()) {
                                mFragmentContainer.slide();
                                mSideBar.show();
                            } else {
                                mFragmentContainer.slideBack();
                                mSideBar.hide(true);
                            }
                            return true;
                        }
                        return super.onScroll(e1, e2, distanceX, distanceY);
                    }

                });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
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
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            return true;
        case KeyEvent.KEYCODE_VOLUME_UP:
            return true;
        default:
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            mFragmentContainer.slide();
            mSideBar.show();
            return true;
        case KeyEvent.KEYCODE_VOLUME_UP:
            mFragmentContainer.slideBack();
            mSideBar.hide(true);
            return true;
        default:
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGestureDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

}
