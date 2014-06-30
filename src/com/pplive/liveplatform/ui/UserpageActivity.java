package com.pplive.liveplatform.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.fragment.PersonalFragment;
import com.pplive.liveplatform.fragment.PersonalFragment.UserType;

public class UserpageActivity extends FragmentActivity {

    static final String TAG = UserpageActivity.class.getSimpleName();

    private FragmentManager mFragmentManager;

    private PersonalFragment mPersonalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_userpage);

        mFragmentManager = getSupportFragmentManager();

        mPersonalFragment = new PersonalFragment();
        Bundle args = new Bundle();
        args.putSerializable(Extra.KEY_USER_TYPE, UserType.USER);
        mPersonalFragment.setArguments(args);

        mFragmentManager.beginTransaction().add(R.id.layout_fragment_container, mPersonalFragment).commit();
    }

}
