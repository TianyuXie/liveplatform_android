package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.core.settings.UserPrefs;

public class SettingsActivity extends Activity {
    private UserPrefs mUserPrefs;

    private TextView mUserTextView;

    private ToggleButton mContentButton;

    private ToggleButton mPreliveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.btn_settings_back).setOnClickListener(onBackBtnClickListener);
        findViewById(R.id.btn_settings_logout).setOnClickListener(onLogoutBtnClickListener);

        mUserTextView = (TextView) findViewById(R.id.text_settings_user);
        mPreliveButton = (ToggleButton) findViewById(R.id.btn_settings_prelive);
        mContentButton = (ToggleButton) findViewById(R.id.btn_settings_content);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserPrefs = SettingsProvider.getInstance(this).getPrefs();
        mPreliveButton.setChecked(mUserPrefs.isPreliveNotify());
        mContentButton.setChecked(mUserPrefs.isContentNotify());
        mUserTextView.setText(UserManager.getInstance(getApplicationContext()).getActiveUserPlain());
    }

    @Override
    protected void onStop() {
        mUserPrefs.setContentNotify(mContentButton.isChecked());
        mUserPrefs.setPreliveNotify(mPreliveButton.isChecked());
        SettingsProvider.getInstance(this).setPrefs(mUserPrefs);
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
            UserManager.getInstance(getApplicationContext()).logout();
            mUserTextView.setText("");
        }
    };

}
