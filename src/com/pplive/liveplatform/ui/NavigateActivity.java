package com.pplive.liveplatform.ui;

import java.util.Stack;

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
import android.widget.RadioButton;
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

    private Stack<Fragment> mFragmentStack;

    private FragmentManager mFragmentManager;

    private HomeFragment mHomeFragment;

    private ChannelFragment mChannelFragment;

    private UserPageFragment mUserPageFragment;
    
    private BlankUserPageFragment mBlankUserPageFragment;

    private ChannelListFragment mChannelListFragment;

    private ImageButton mBtnLiveRecord;

    private RadioGroup mNavigateBar;

    private int mLastCheckedRadioButtonId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_navigate);

        mFragmentStack = new Stack<Fragment>();

        mNavigateBar = (RadioGroup) findViewById(R.id.nav_bar);

        mBtnLiveRecord = (ImageButton) findViewById(R.id.navbar_btn_createlive);
        mBtnLiveRecord.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NavigateActivity.this, LiveRecordActivity.class);
                startActivity(intent);
            }
        });

        mFragmentManager = getSupportFragmentManager();

        mHomeFragment = new HomeFragment();

        mChannelFragment = new ChannelFragment();

        mChannelListFragment = new ChannelListFragment();
        mChannelListFragment.setCallbackListener(new ChannelListFragment.CallbackListener() {

            @Override
            public void onSubjectSelected(Subject subject) {
                switchFragment(mChannelFragment);
                mChannelFragment.switchSubject(subject.getId());
            }
        });

        mUserPageFragment = new UserPageFragment();
        
        mBlankUserPageFragment = new BlankUserPageFragment();

        switchFragment(mHomeFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    private void switchFragment(Fragment fragment) {
        switchFragment(fragment, true);
    }

    private void switchFragment(Fragment fragment, boolean push) {
        if (fragment.isAdded()) {
            return;
        }

        FragmentTransaction transcation = mFragmentManager.beginTransaction();

        transcation.replace(R.id.layout_fragment_container, fragment);

        transcation.commitAllowingStateLoss();

        if (push) {
            mFragmentStack.remove(fragment);
            mFragmentStack.push(fragment);
        }
    }

    private void popBackFragment() {
        if (!mFragmentStack.isEmpty()) {
            Fragment fragment = mFragmentStack.pop();
            switchFragment(fragment, false);
        }
    }

    public void onRadioButtonClicked(View view) {
        if (view instanceof RadioButton) {
            boolean checked = ((RadioButton) view).isChecked();

            switch (view.getId()) {
            case R.id.navbar_personal:
                if (checked) {
                    onClickNavBarPersonal();
                }
                break;
            case R.id.navbar_home:
                if (checked) {
                    switchFragment(mHomeFragment);
                }
                break;
            case R.id.navbar_original:
                if (checked) {
                    mChannelFragment.switchSubject(1);
                    switchFragment(mChannelFragment);
                }
                break;
            case R.id.navbar_channel:
                if (checked) {
                    switchFragment(mChannelListFragment);
                }
                break;
            default:
                break;
            }

            mLastCheckedRadioButtonId = mNavigateBar.getCheckedRadioButtonId();
        }
    }

    private void onClickNavBarPersonal() {
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
