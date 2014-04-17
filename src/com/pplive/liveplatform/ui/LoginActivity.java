package com.pplive.liveplatform.ui;

import java.lang.ref.WeakReference;

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
import android.widget.Toast;

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
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;

public class LoginActivity extends Activity implements ThirdpartyLoginListener {
    static final String TAG = "_LoginActivity";

    public static final String EXTRA_TAGET = "target";

    public static final String EXTRA_USERNAME = "username";

    public static final String EXTRA_PASSWORD = "password";

    private static final int MSG_THIRDPARTY_ERROR = 2301;

    private static final int DELAY_LOGIN = 3000;

    private EditText mUsrEditText;

    private EditText mPwdEditText;

    private Button mConfirmButton;

    private Dialog mRefreshDialog;

    private Context mContext;

    private UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mErrorHandler = new ErrorHandler(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        findViewById(R.id.btn_login_back).setOnClickListener(onBackBtnClickListener);
        findViewById(R.id.text_login_register).setOnClickListener(onRegisterClickListener);

        mUsrEditText = (EditText) findViewById(R.id.edit_login_username);
        mPwdEditText = (EditText) findViewById(R.id.edit_login_password);
        mPwdEditText.setOnKeyListener(onFinalEnterListener);
        mConfirmButton = (Button) findViewById(R.id.btn_login_confirm);
        mConfirmButton.setOnClickListener(onConfirmBtnClickListener);
        mUsrEditText.addTextChangedListener(textWatcher);
        mPwdEditText.addTextChangedListener(textWatcher);

        mUserManager = UserManager.getInstance(this);
        mRefreshDialog = new RefreshDialog(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mErrorHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RegisterActivity.FROM_LOGIN && resultCode == RegisterActivity.REGISTER_SUCCESS) {
            String username = data.getStringExtra(EXTRA_USERNAME);
            String password = data.getStringExtra(EXTRA_PASSWORD);
            mRefreshDialog.show();
            startLogin(username, password, DELAY_LOGIN);
        } else if (WeiboPassport.getInstance().mSsoHandler != null) {
            WeiboPassport.getInstance().mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public void qqlogin(View v) {
        mRefreshDialog.show();
        TencentPassport.getInstance().init(this);
        TencentPassport.getInstance().setLoginListener(this);
        TencentPassport.getInstance().login(this);
    }

    public void weiboLogin(View v) {
        mRefreshDialog.show();
        WeiboPassport.getInstance().setLoginListener(this);
        WeiboPassport.getInstance().login(this);
    }

    private View.OnKeyListener onFinalEnterListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && mConfirmButton.isEnabled()) {
                mConfirmButton.performClick();
                return true;
            }
            return false;
        }
    };

    private View.OnClickListener onBackBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener onRegisterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivityForResult(intent, RegisterActivity.FROM_LOGIN);
        }
    };

    private View.OnClickListener onConfirmBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRefreshDialog.show();
            startLogin(mUsrEditText.getText().toString(), mPwdEditText.getText().toString(), 0);
        }
    };

    private void startLogin(String username, String password, int dalay) {
        LoginTask task = new LoginTask();
        task.addTaskListener(onLoginTaskListener);
        task.setDelay(dalay);
        TaskContext taskContext = new TaskContext();
        taskContext.set(LoginTask.KEY_USERNAME, username);
        taskContext.set(LoginTask.KEY_PASSWORD, password);
        task.execute(taskContext);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(mUsrEditText.getText()) && !TextUtils.isEmpty(mPwdEditText.getText())) {
                mConfirmButton.setEnabled(true);
            } else {
                mConfirmButton.setEnabled(false);
            }
        }
    };

    private Task.OnTaskListener onLoginTaskListener = new Task.OnTaskListener() {

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            String usrPlain = event.getContext().getString(LoginTask.KEY_USERNAME);
            String pwdPlain = event.getContext().getString(LoginTask.KEY_PASSWORD);
            String token = event.getContext().getString(LoginTask.KEY_TOKEN);
            mUserManager.login(usrPlain, pwdPlain, token);
            User userinfo = (User) event.getContext().get(LoginTask.KEY_USERINFO);
            mUserManager.setUserinfo(userinfo);
            mRefreshDialog.dismiss();
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
                    e.printStackTrace();
                }
            }
            finish();
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "LoginTask onTaskFailed: " + event.getMessage());
            mRefreshDialog.dismiss();
            String message = event.getMessage();
            if (TextUtils.isEmpty(message)) {
                message = getString(R.string.login_failed);
            }
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "LoginTask onTimeout");
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_login_timeout, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "LoginTask onTaskCancel");
            mRefreshDialog.dismiss();
        }
    };

    @Override
    public void loginSuccess(LoginResult res) {
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
                e.printStackTrace();
            }
        }
        finish();
    }

    @Override
    public void loginFailed(String info) {
        mRefreshDialog.dismiss();
        Message msg = new Message();
        msg.what = MSG_THIRDPARTY_ERROR;
        msg.obj = TextUtils.isEmpty(info) ? getString(R.string.login_failed) : info;
        mErrorHandler.sendMessage(msg);
    }

    private Handler mErrorHandler;

    static class ErrorHandler extends Handler {
        private WeakReference<LoginActivity> mOuter;

        public ErrorHandler(LoginActivity activity) {
            mOuter = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                case MSG_THIRDPARTY_ERROR:
                    Toast.makeText(outer, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    @Override
    public void loginCanceled() {
        mRefreshDialog.dismiss();
    }

}
