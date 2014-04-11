package com.pplive.liveplatform.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.home.HomeFragment;
import com.pplive.liveplatform.ui.player.LivePlayerFragment;

public class MainActivity extends FragmentActivity {

    private FragmentManager mFragmentManager;

    private HomeFragment mHomeFragment;

    private LivePlayerFragment mLivePlayerFragment;

    private ImageButton mBtnLiveRecord;
    
    private boolean mSwitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mBtnLiveRecord = (ImageButton) findViewById(R.id.btn_live_record);
        mBtnLiveRecord.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                if (!mSwitch) {
                    switchFragment(mLivePlayerFragment);
                    
                    mSwitch = true;
                } else {
                    switchFragment(mHomeFragment);
                    
                    mSwitch = false;
                }
            }
        });

        mFragmentManager = getSupportFragmentManager();

        mHomeFragment = new HomeFragment();
        mLivePlayerFragment = new LivePlayerFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();

        switchFragment(mHomeFragment);
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transcation = mFragmentManager.beginTransaction();

        transcation.replace(R.id.layout_fragment_container, fragment);

        transcation.commit();
    }
}
