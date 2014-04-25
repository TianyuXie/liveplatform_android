package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.network.QualityPreferences;
import com.pplive.liveplatform.core.record.Quality;
import com.pplive.liveplatform.core.settings.AppPrefs;
import com.pplive.liveplatform.core.settings.SettingsPreferences;
import com.pplive.liveplatform.core.update.Update;
import com.pplive.liveplatform.ui.dialog.DialogManager;
import com.pplive.liveplatform.ui.widget.TopBarView;
import com.umeng.fb.FeedbackAgent;

public class SettingsActivity extends Activity {
    static final String TAG = "_SettingsActivity";

    public static final int RESULT_LOGOUT = 5801;

    public static final int RESULT_NICK_CHANGED = 5802;

    private static final int REQUEST_NICK = 9801;

    private TopBarView mTopBarView;

    private AppPrefs mUserPrefs;

    private TextView mNicknameText;

    private TextView mPPTVUserText;

    private TextView mThirdpartyText;

    private TextView mQualityText;

    private ToggleButton mContentButton;

    private ToggleButton mPreliveButton;

    private View mPPTVUserView;

    private View mThirdpartyView;

    private int mResultCode;

    private FeedbackAgent mFeedbackAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(mOnBackBtnClickListener);
        mTopBarView.showLeftBtn();

        findViewById(R.id.btn_settings_logout).setOnClickListener(mOnLogoutBtnClickListener);
        findViewById(R.id.btn_settings_login).setOnClickListener(mOnLoginBtnClickListener);
        findViewById(R.id.layout_settings_nickname).setOnClickListener(mOnNicknameClickListener);
        findViewById(R.id.layout_settings_about).setOnClickListener(mOnAboutClickListener);
        findViewById(R.id.layout_settings_update).setOnClickListener(mOnUpdateClickListener);
        findViewById(R.id.layout_settings_feedback).setOnClickListener(mOnFeedbackClickListener);
        findViewById(R.id.layout_settings_quality).setOnClickListener(mOnQualitySettingClickListener);

        mNicknameText = (TextView) findViewById(R.id.text_settings_nickname);
        mPPTVUserText = (TextView) findViewById(R.id.text_settings_user);
        mThirdpartyText = (TextView) findViewById(R.id.text_settings_thirdparty);
        mQualityText = (TextView) findViewById(R.id.text_quality);

        mPreliveButton = (ToggleButton) findViewById(R.id.btn_settings_prelive);
        mContentButton = (ToggleButton) findViewById(R.id.btn_settings_content);
        mPPTVUserView = findViewById(R.id.row_settings_user);
        mThirdpartyView = findViewById(R.id.layout_settings_thirdparty);

        mFeedbackAgent = new FeedbackAgent(this);
        mFeedbackAgent.sync();

        mResultCode = -1;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUserPrefs = SettingsPreferences.getInstance(this).getAppPrefs();
        mPreliveButton.setChecked(mUserPrefs.isPreliveNotify());
        mContentButton.setChecked(mUserPrefs.isContentNotify());
        mPPTVUserText.setText(UserManager.getInstance(this).getUsernamePlain());
        mNicknameText.setText(UserManager.getInstance(this).getNickname());
        mNicknameText.requestLayout();
        View nickView = findViewById(R.id.layout_settings_nickname);
        View userView = findViewById(R.id.layout_settings_user);
        View logoutBtn = findViewById(R.id.btn_settings_logout);
        View loginBtn = findViewById(R.id.btn_settings_login);
        View preliveView = findViewById(R.id.layout_settings_prelive);
        if (UserManager.getInstance(this).isLogin()) {
            nickView.setVisibility(View.VISIBLE);
            userView.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
            preliveView.setVisibility(View.GONE);
            loginBtn.setVisibility(View.GONE);
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
        } else {
            logoutBtn.setVisibility(View.GONE);
            preliveView.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
            nickView.setVisibility(View.GONE);
            userView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int resId = R.string.settings_quality_normal;
        Quality quality = QualityPreferences.getInstance(getApplicationContext()).getQuality();
        if (null != quality) {
            switch (quality) {
            case High:
                resId = R.string.settings_quality_high;
                break;
            case Normal:
                resId = R.string.settings_quality_normal;
                break;
            case Low:
                resId = R.string.settings_quality_low;
                break;
            }
        }

        mQualityText.setText(resId);
    }

    @Override
    protected void onStop() {
        mUserPrefs.setContentNotify(mContentButton.isChecked());
        mUserPrefs.setPreliveNotify(mPreliveButton.isChecked());
        SettingsPreferences.getInstance(this).setAppPrefs(mUserPrefs);
        super.onStop();
    }

    private View.OnClickListener mOnBackBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener mOnLogoutBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Dialog dialog = DialogManager.alertLogoutDialog(SettingsActivity.this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserManager.getInstance(SettingsActivity.this).logout();
                    mResultCode = RESULT_LOGOUT;
                    finish();
                }
            });
            dialog.show();
        }
    };

    private View.OnClickListener mOnLoginBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_TAGET, UserpageActivity.class.getName());
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener mOnNicknameClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SettingsActivity.this, NicknameActivity.class);
            startActivityForResult(intent, REQUEST_NICK);
        }
    };

    private View.OnClickListener mOnAboutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener mOnUpdateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Update.updateManual(SettingsActivity.this);
        }
    };

    private View.OnClickListener mOnFeedbackClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mFeedbackAgent.startFeedbackActivity();
        }
    };

    private View.OnClickListener mOnQualitySettingClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SettingsActivity.this, QualitySettingActivity.class);
            startActivity(intent);
        }
    };

    public void finish() {
        setResult(mResultCode);
        super.finish();
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NICK && resultCode == NicknameActivity.RESULT_NICK_CHANGED) {
            mResultCode = RESULT_NICK_CHANGED;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
