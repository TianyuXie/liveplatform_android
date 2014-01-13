package com.pplive.liveplatform.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.dac.DacSender;
import com.pplive.liveplatform.ui.widget.viewpager.CustomerViewPager;

public class IntroActivity extends Activity {
    static final String TAG = "_IntroActivity";

    private static final int MSG_GO_HOME = 4000;

    private static final int MSG_GO_INTRO = 4001;

    private boolean mStarted;

    private boolean mFirstTime;

    private RadioGroup mRadioGroup;

    private CustomerViewPager mViewPager;

    private List<View> mImageViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mViewPager = (CustomerViewPager) findViewById(R.id.viewpager_intro);
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup_intro);

        mImageViewList = new ArrayList<View>();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        int images[] = { R.drawable.intro_image0, R.drawable.intro_image1, R.drawable.intro_image2, R.drawable.intro_image3, R.drawable.intro_image0 };
        for (int image : images) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(lp);
            imageView.setBackgroundColor(getResources().getColor(R.color.intro_bg));
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setImageResource(image);
            mImageViewList.add(imageView);
        }

        mViewPager.setScrollable(true);
        mViewPager.setSwitchDuration(500);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(onPageChangeListener);
        mViewPager.setCurrentItem(1);

        mFirstTime = SettingsProvider.getInstance(this).isFirstLaunch();
        DacSender.sendAppStartDac(getApplicationContext(), mFirstTime);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!mFirstTime) {
            mHandler.sendEmptyMessageDelayed(MSG_GO_HOME, 3000);
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_GO_INTRO, 3000);
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        mHandler.removeMessages(MSG_GO_HOME);
        mHandler.removeMessages(MSG_GO_INTRO);
        super.onPause();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_GO_HOME:
                startHomeActivity();
                break;
            case MSG_GO_INTRO:
                findViewById(R.id.image_intro_welcome).setVisibility(View.GONE);
            default:
                break;
            }
        }
    };

    private PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageViewList.get(position));
            return mImageViewList.get(position);
        }
    };

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            switch (position) {
            case 1:
                mRadioGroup.check(R.id.btn_intro1);
                break;
            case 2:
                mRadioGroup.check(R.id.btn_intro2);
                break;
            case 3:
                mRadioGroup.check(R.id.btn_intro3);
                break;
            default:
                break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position <= 0) {
                mViewPager.setCurrentItem(1);
            } else if (position >= mImageViewList.size() - 2) {
                if (positionOffset < 0.2f) {
                    mViewPager.setCurrentItem(mImageViewList.size() - 2);
                } else {
                    startHomeActivity();
                }
            }
        }
    };

    private void startHomeActivity() {
        if (!mStarted) {
            mStarted = true;
            Intent intent = new Intent(IntroActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
