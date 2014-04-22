package com.pplive.liveplatform.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.live.model.Subject;
import com.pplive.liveplatform.ui.navigate.BlankUserPageFragment;
import com.pplive.liveplatform.ui.navigate.ChannelFragment;
import com.pplive.liveplatform.ui.navigate.ChannelListFragment;
import com.pplive.liveplatform.ui.navigate.HomeFragment;
import com.pplive.liveplatform.ui.navigate.UserPageFragment;

public class NavigateActivity extends FragmentActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_navigate);

        mNavigateBar = (RadioGroup) findViewById(R.id.nav_bar);
        mNavigateBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                NavigateActivity.this.onCheckedChanged(checkedId);
            }
        });

        mBtnLiveRecord = (ImageButton) findViewById(R.id.navbar_btn_createlive);
        mBtnLiveRecord.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NavigateActivity.this, LiveRecordActivity.class);
                startActivity(intent);
            }
        });

        mFragmentManager = getSupportFragmentManager();

        mHomeFragment = (HomeFragment) mFragmentManager.findFragmentById(R.id.fragment_home);

        mChannelFragment = new ChannelFragment();

        mChannelListFragment = new ChannelListFragment();
        mChannelListFragment.setCallbackListener(new ChannelListFragment.CallbackListener() {

            @Override
            public void onSubjectSelected(Subject subject) {
                switchFragment(mChannelFragment);
                mChannelFragment.switchSubject(subject);
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
        super.onBackPressed();
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

    private void onCheckedChanged(int checkId) {

        switch (checkId) {
        case R.id.navbar_home:
            switchFragment(mHomeFragment);
            break;
        case R.id.navbar_original:
            switchFragment(mChannelFragment);
            mChannelFragment.switchSubject(1);
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
            Intent intent = new Intent();
            intent.putExtra(UserpageActivity.EXTRA_USER, UserManager.getInstance(context).getUsernamePlain());
            intent.putExtra(UserpageActivity.EXTRA_ICON, UserManager.getInstance(context).getIcon());
            intent.putExtra(UserpageActivity.EXTRA_NICKNAME, UserManager.getInstance(context).getNickname());

            mUserPageFragment.setIntent(intent);

            switchFragment(mUserPageFragment);
        } else {

            switchFragment(mBlankUserPageFragment);
        }
    }

}
