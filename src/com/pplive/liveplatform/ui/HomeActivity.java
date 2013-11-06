package com.pplive.liveplatform.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.home.HomeFragment;
import com.pplive.liveplatform.ui.widget.SideBar;

public class HomeActivity extends FragmentActivity {
    static final String TAG = "HomepageActivity";

    private Animation scaleAnimation;

    private Animation scalebackAnimation;

    private View mFragmentContainer;

    private SideBar mSideBar;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_home);

        scalebackAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_anim_back);

        scaleAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_anim);

        mFragmentContainer = findViewById(R.id.layout_home_fragment_container);

        mSideBar = (SideBar) findViewById(R.id.sidebar_home);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment homepageFragment = new HomeFragment();
        fragmentTransaction.add(R.id.layout_home_fragment_container, homepageFragment);
        fragmentTransaction.commit();
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
        Log.d("Tween", "onKeyDown");
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("Tween", "onKeyUp");
        switch (keyCode) {
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            Log.d("Tween", "onKeyDown - KEYCODE_DPAD_DOWN");
            mFragmentContainer.startAnimation(scaleAnimation);
            mFragmentContainer.setEnabled(false);
            mSideBar.show();
            break;
        case KeyEvent.KEYCODE_VOLUME_UP:
            Log.d("Tween", "onKeyDown - KEYCODE_DPAD_LEFT");
            mFragmentContainer.startAnimation(scalebackAnimation);
            mFragmentContainer.setEnabled(true);
            mSideBar.hide(true);
            break;
        default:
            break;
        }
        return true;
    }

}
