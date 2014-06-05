package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.service.passport.model.LoginResult;
import com.pplive.liveplatform.core.service.passport.thirdparty.TencentPassport;
import com.pplive.liveplatform.core.service.passport.thirdparty.ThirdpartyLoginListener;
import com.pplive.liveplatform.core.service.passport.thirdparty.WeiboPassport;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.LoginTask;
import com.pplive.liveplatform.ui.widget.TopBarView;
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;

public class LoginActivity extends Activity implements Handler.Callback, ThirdpartyLoginListener {

    static final String TAG = LoginActivity.class.getSimpleName();

    public static final String EXTRA_TAGET = "target";

    public static final String EXTRA_USERNAME = "username";

    public static final String EXTRA_PASSWORD = "password";

    private static final int MSG_THIRDPARTY_ERROR = 2301;

    private static final int DELAY_LOGIN = 3000;

    private Handler mHandler = new Handler(this);

    private TopBarView mTopBarView;

    private EditText mEditUsername;

    private EditText mEditPassword;

    private Button mBtnLogin;

    private TextView mTextError;

    private Dialog mRefreshDialog;

    private Context mContext;

    private UserManager mUserManager;

    private View.OnKeyListener mOnFinalEnterListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // TODO
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                mBtnLogin.performClick();
                return true;
            }

            return false;
        }
    };

    private View.OnClickListener mOnClickBtnLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            login(mEditUsername.getText().toString(), mEditPassword.getText().toString(), 0);
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            hideErrorMsg();

            if (!TextUtils.isEmpty(mEditUsername.getText()) && !TextUtils.isEmpty(mEditPassword.getText())) {
                mBtnLogin.setEnabled(true);
            } else {
                mBtnLogin.setEnabled(false);
            }
        }
    };

    private Task.OnTaskListener mOnLoginTaskListener = new Task.OnTaskListener() {

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {

            mRefreshDialog.dismiss();

            String plainUsername = event.getContext().getString(LoginTask.KEY_USERNAME);
            String plainPassword = event.getContext().getString(LoginTask.KEY_PASSWORD);
            String token = event.getContext().getString(LoginTask.KEY_TOKEN);
            mUserManager.login(plainUsername, plainPassword, token);

            User userinfo = (User) event.getContext().get(LoginTask.KEY_USERINFO);
            mUserManager.setUserinfo(userinfo);
            String targetClass = getIntent().getStringExtra(EXTRA_TAGET);
            if (!TextUtils.isEmpty(targetClass)) {
                try {
                    Intent intent = new Intent(mContext, Class.forName(targetClass));
                    intent.putExtra(UserpageActivity.EXTRA_USER, UserManager.getInstance(mContext).getUsernamePlain());
                    intent.putExtra(UserpageActivity.EXTRA_ICON, UserManager.getInstance(mContext).getIcon());
                    intent.putExtra(UserpageActivity.EXTRA_NICKNAME, UserManager.getInstance(mContext).getNickname());

                    setResult(999, intent);

                    mContext.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    Log.w(TAG, e.toString());
                }
            }

            finish();
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "LoginTask onTaskFailed: " + event.getMessage());

            mRefreshDialog.dismiss();

            String msg = event.getMessage();
            if (TextUtils.isEmpty(msg)) {
                msg = getString(R.string.login_failed);
            }

            showErrorMsg(msg);
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {

            Log.d(TAG, "LoginTask onTimeout");
            mRefreshDialog.dismiss();

            showErrorMsg(getString(R.string.toast_login_timeout));
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "LoginTask onTaskCancel");
            mRefreshDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        mContext = this;

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTopBarView.setRightBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, RegisterActivity.FROM_LOGIN);
            }
        });

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(mOnClickBtnLoginListener);

        mEditUsername = (EditText) findViewById(R.id.edit_login_username);
        mEditUsername.addTextChangedListener(mTextWatcher);

        mEditPassword = (EditText) findViewById(R.id.edit_login_password);
        mEditPassword.setOnKeyListener(mOnFinalEnterListener);
        mEditPassword.addTextChangedListener(mTextWatcher);

        mTextError = (TextView) findViewById(R.id.text_error);

        mUserManager = UserManager.getInstance(this);
        mRefreshDialog = new RefreshDialog(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();

        if (null != intent) {
            String username = intent.getStringExtra(EXTRA_USERNAME);
            String password = intent.getStringExtra(EXTRA_PASSWORD);

            login(username, password, DELAY_LOGIN);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RegisterActivity.FROM_LOGIN && resultCode == RegisterActivity.REGISTER_SUCCESS) {
            String username = data.getStringExtra(EXTRA_USERNAME);
            String password = data.getStringExtra(EXTRA_PASSWORD);

            login(username, password, DELAY_LOGIN);
        } else if (WeiboPassport.getInstance().mSsoHandler != null) {
            WeiboPassport.getInstance().mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case MSG_THIRDPARTY_ERROR:
            showErrorMsg(msg.obj.toString());
            break;

        default:
            break;
        }

        return false;
    }

    public void loginByQQ(View v) {
        mRefreshDialog.show();
        TencentPassport.getInstance().init(this);
        TencentPassport.getInstance().setLoginListener(this);
        TencentPassport.getInstance().login(this);
    }

    public void loginByWeibo(View v) {
        mRefreshDialog.show();
        WeiboPassport.getInstance().setLoginListener(this);
        WeiboPassport.getInstance().login(this);
    }

    private void login(String username, String password, int dalay) {

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return;
        }

        mRefreshDialog.show();

        LoginTask task = new LoginTask();
        task.addTaskListener(mOnLoginTaskListener);
        task.setDelay(dalay);
        TaskContext taskContext = new TaskContext();
        taskContext.set(LoginTask.KEY_USERNAME, username);
        taskContext.set(LoginTask.KEY_PASSWORD, password);
        task.execute(taskContext);
    }

    @Override
    public void onLoginSuccess(LoginResult res) {
        Log.d(TAG, res.getUsername() + " | " + res.getToken() + " | " + res.getThirdPartyNickName() + " | " + res.getThirdPartyFaceUrl());
        mUserManager.login(res.getUsername(), "", res.getToken());
        mUserManager.setThirdParty(res.getThirdPartySource());
        mUserManager.setUserinfo(res.getNickName(), res.getFaceUrl());

        String targetClass = getIntent().getStringExtra(EXTRA_TAGET);

        mRefreshDialog.dismiss();

        if (!TextUtils.isEmpty(targetClass)) {
            try {
                Intent intent = new Intent(mContext, Class.forName(targetClass));
                intent.putExtra(UserpageActivity.EXTRA_USER, UserManager.getInstance(this).getUsernamePlain());
                intent.putExtra(UserpageActivity.EXTRA_ICON, UserManager.getInstance(this).getIcon());
                intent.putExtra(UserpageActivity.EXTRA_NICKNAME, UserManager.getInstance(this).getNickname());
                mContext.startActivity(intent);
            } catch (ClassNotFoundException e) {
                Log.w(TAG, e.toString());
            }
        }

        finish();
    }

    @Override
    public void onLoginFailed(String info) {
        mRefreshDialog.dismiss();
        Message msg = mHandler.obtainMessage(MSG_THIRDPARTY_ERROR);
        msg.obj = TextUtils.isEmpty(info) ? getString(R.string.login_failed) : info;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onLoginCanceled() {
        mRefreshDialog.dismiss();
    }

    private void showErrorMsg(String msg) {
        mTextError.setText(msg);
    }

    private void hideErrorMsg() {
        mTextError.setText("");
    }

}
