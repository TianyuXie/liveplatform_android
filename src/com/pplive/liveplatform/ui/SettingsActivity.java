package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.settings.AppPrefs;
import com.pplive.liveplatform.core.settings.SettingsProvider;

public class SettingsActivity extends Activity {
    static final String TAG = "_SettingsActivity";

    public static final int FROM_USERPAGE = 6001;

    public static final int LOGOUT = 5801;

    private AppPrefs mUserPrefs;

    private TextView mNicknameText;

    private TextView mPPTVUserText;

    private TextView mThirdpartyText;

    private ToggleButton mContentButton;

    private ToggleButton mPreliveButton;

    private View mPPTVUserView;

    private View mThirdpartyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.btn_settings_back).setOnClickListener(onBackBtnClickListener);
        findViewById(R.id.btn_settings_logout).setOnClickListener(onLogoutBtnClickListener);
        findViewById(R.id.layout_settings_nickname).setOnClickListener(onNicknameClickListener);

        mNicknameText = (TextView) findViewById(R.id.text_settings_nickname);
        mPPTVUserText = (TextView) findViewById(R.id.text_settings_user);
        mThirdpartyText = (TextView) findViewById(R.id.text_settings_thirdparty);
        mPreliveButton = (ToggleButton) findViewById(R.id.btn_settings_prelive);
        mContentButton = (ToggleButton) findViewById(R.id.btn_settings_content);
        mPPTVUserView = findViewById(R.id.layout_settings_user);
        mThirdpartyView = findViewById(R.id.layout_settings_thirdparty);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserPrefs = SettingsProvider.getInstance(this).getAppPrefs();
        mPreliveButton.setChecked(mUserPrefs.isPreliveNotify());
        mContentButton.setChecked(mUserPrefs.isContentNotify());
        mPPTVUserText.setText(UserManager.getInstance(this).getUsernamePlain());
        mNicknameText.setText(UserManager.getInstance(this).getNickname());
        if (UserManager.getInstance(this).isThirdPartyLogin()) {
            mPPTVUserView.setVisibility(View.GONE);
            mThirdpartyView.setVisibility(View.VISIBLE);
            if (UserManager.getInstance(this).isSinaLogin()) {
                mThirdpartyText.setText(R.string.settings_login_weibo);
            } else if (UserManager.getInstance(this).isTencentLogin()) {
                mThirdpartyText.setText(R.string.settings_login_qq);
            }
        } else {
            mPPTVUserView.setVisibility(View.VISIBLE);
            mThirdpartyView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        mUserPrefs.setContentNotify(mContentButton.isChecked());
        mUserPrefs.setPreliveNotify(mPreliveButton.isChecked());
        SettingsProvider.getInstance(this).setAppPrefs(mUserPrefs);
        super.onStop();
    }

    private View.OnClickListener onBackBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener onLogoutBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserManager.getInstance(SettingsActivity.this).logout();
            setResult(LOGOUT);
            finish();
        }
    };

    private View.OnClickListener onNicknameClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (UserManager.getInstance(SettingsActivity.this).isPPTVLogin()) {
                Intent intent = new Intent(SettingsActivity.this, NicknameActivity.class);
                startActivity(intent);
            }
        }
    };

}
