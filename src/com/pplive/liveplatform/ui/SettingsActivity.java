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
import com.pplive.liveplatform.core.settings.AppPrefs;
import com.pplive.liveplatform.core.settings.SettingsProvider;
import com.pplive.liveplatform.ui.dialog.DialogManager;
import com.pplive.liveplatform.update.Update;

public class SettingsActivity extends Activity {
    static final String TAG = "_SettingsActivity";

    public static final int RESULT_LOGOUT = 5801;

    public static final int RESULT_NICK_CHANGED = 5802;

    private static final int REQUEST_NICK = 9801;

    private AppPrefs mUserPrefs;

    private TextView mNicknameText;

    private TextView mPPTVUserText;

    private TextView mThirdpartyText;

    private ToggleButton mContentButton;

    private ToggleButton mPreliveButton;

    private View mPPTVUserView;

    private View mThirdpartyView;

    private int mResultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.btn_settings_back).setOnClickListener(onBackBtnClickListener);
        findViewById(R.id.btn_settings_logout).setOnClickListener(onLogoutBtnClickListener);
        findViewById(R.id.btn_settings_login).setOnClickListener(onLoginBtnClickListener);
        findViewById(R.id.layout_settings_nickname).setOnClickListener(onNicknameClickListener);
        findViewById(R.id.layout_settings_about).setOnClickListener(onAboutClickListener);
        findViewById(R.id.layout_settings_update).setOnClickListener(onUpdateClickListener);

        mNicknameText = (TextView) findViewById(R.id.text_settings_nickname);
        mPPTVUserText = (TextView) findViewById(R.id.text_settings_user);
        mThirdpartyText = (TextView) findViewById(R.id.text_settings_thirdparty);
        mPreliveButton = (ToggleButton) findViewById(R.id.btn_settings_prelive);
        mContentButton = (ToggleButton) findViewById(R.id.btn_settings_content);
        mPPTVUserView = findViewById(R.id.row_settings_user);
        mThirdpartyView = findViewById(R.id.layout_settings_thirdparty);

        mResultCode = -1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserPrefs = SettingsProvider.getInstance(this).getAppPrefs();
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
            preliveView.setVisibility(View.VISIBLE);
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
            Dialog dialog = DialogManager.logoutAlertDialog(SettingsActivity.this, new DialogInterface.OnClickListener() {
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

    private View.OnClickListener onLoginBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_TAGET, UserpageActivity.class.getName());
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener onNicknameClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SettingsActivity.this, NicknameActivity.class);
            startActivityForResult(intent, REQUEST_NICK);
        }
    };

    private View.OnClickListener onAboutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onUpdateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Update.updateManual(SettingsActivity.this);
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
