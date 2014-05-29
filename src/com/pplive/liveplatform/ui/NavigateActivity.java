package com.pplive.liveplatform.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.dac.info.LocationInfo;
import com.pplive.liveplatform.core.dac.info.SessionInfo;
import com.pplive.liveplatform.core.location.Locator.LocationData;
import com.pplive.liveplatform.core.location.LocatorActivity;
import com.pplive.liveplatform.core.service.live.model.Subject;
import com.pplive.liveplatform.ui.navigate.BlankUserPageFragment;
import com.pplive.liveplatform.ui.navigate.ChannelFragment;
import com.pplive.liveplatform.ui.navigate.ChannelListFragment;
import com.pplive.liveplatform.ui.navigate.HomeFragment;
import com.pplive.liveplatform.ui.navigate.UserPageFragment;
import com.tencent.mm.sdk.platformtools.BackwardSupportUtil.AnimationHelper;

public class NavigateActivity extends LocatorActivity {

    static final String TAG = NavigateActivity.class.getSimpleName();

    private FragmentManager mFragmentManager;

    private Fragment mCurrentFragment;

    private HomeFragment mHomeFragment;

    private ChannelFragment mChannelFragment;

    private UserPageFragment mUserPageFragment;

    private BlankUserPageFragment mBlankUserPageFragment;

    private ChannelListFragment mChannelListFragment;

    private ImageButton mBtnLiveRecord;

    private RadioGroup mNavigateBar;

    private long mExitTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_navigate);

        mNavigateBar = (RadioGroup) findViewById(R.id.nav_bar);
        mBtnLiveRecord = (ImageButton) findViewById(R.id.navbar_btn_createlive);
        mBtnLiveRecord.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Constants.LARGER_THAN_OR_EQUAL_JELLY_BEAN) {
                    if (UserManager.getInstance(NavigateActivity.this).isLoginSafely()) {
                        Intent intent = new Intent(NavigateActivity.this, LiveRecordActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(NavigateActivity.this, LoginActivity.class);
                        intent.putExtra(LoginActivity.EXTRA_TAGET, LiveRecordActivity.class.getName());
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(NavigateActivity.this, R.string.toast_version_low, Toast.LENGTH_LONG).show();
                }

            }
        });

        mFragmentManager = getSupportFragmentManager();

        mHomeFragment = (HomeFragment) mFragmentManager.findFragmentById(R.id.fragment_home);

        mChannelFragment = new ChannelFragment();
        mChannelFragment.setBackBtnOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(mChannelListFragment);
            }
        });

        mChannelListFragment = new ChannelListFragment();
        mChannelListFragment.setCallbackListener(new ChannelListFragment.CallbackListener() {

            @Override
            public void onSubjectSelected(Subject subject) {
                switchFragment(mChannelFragment);
                mChannelFragment.switchSubject(subject);
                mChannelFragment.showBackBtn();
            }
        });

        mUserPageFragment = new UserPageFragment();

        mBlankUserPageFragment = new BlankUserPageFragment();

        mCurrentFragment = mHomeFragment;
    }

    @Override
    protected void onStart() {
        super.onStart();

        onCheckedChanged(mNavigateBar.getCheckedRadioButtonId());
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - mExitTime > 2000) {
            Toast.makeText(this, R.string.app_exit, Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            SessionInfo.reset();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "resultCode: " + resultCode);

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void switchFragment(Fragment to) {
        switchFragment(mCurrentFragment, to);
    }

    private void switchFragment(Fragment from, Fragment to) {
        if (from == to) {
            return;
        }

        FragmentTransaction transcation = mFragmentManager.beginTransaction();

        if (!to.isAdded()) {
            transcation.hide(mCurrentFragment).add(R.id.layout_fragment_container, to);
        } else {
            transcation.hide(mCurrentFragment).show(to);
        }

        transcation.commit();

        mCurrentFragment = to;
    }

    public void onRadioButtonChecked(View view) {
        if (view instanceof RadioButton) {
            boolean checked = ((RadioButton) view).isChecked();

            if (checked) {
                onCheckedChanged(view.getId());
            }
        }
    }

    private void onCheckedChanged(int checkId) {

        switch (checkId) {
        case R.id.navbar_home:
            switchFragment(mHomeFragment);
            break;
        case R.id.navbar_channel_list:
            switchFragment(mChannelListFragment);
            break;
        case R.id.navbar_personal:
            onCheckedNavBarPersonal();
            break;
        default:
            break;
        }
    }

    private void onCheckedNavBarPersonal() {
        Context context = this;

        if (UserManager.getInstance(context).isLoginSafely()) {

            switchFragment(mUserPageFragment);
        } else {

            switchFragment(mBlankUserPageFragment);
        }
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
        // TODO Auto-generated method stub

    }

}
