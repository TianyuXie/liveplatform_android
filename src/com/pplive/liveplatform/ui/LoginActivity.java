package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.pplive.liveplatform.core.passport.service.TencentPassport;
import com.pplive.liveplatform.core.passport.service.WeiboPassport;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.service.passport.model.LoginResult;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.LoginTask;
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;

public class LoginActivity extends Activity implements TencentPassport.ThirdpartyLoginListener {
    static final String TAG = "_LoginActivity";

    public static final String EXTRA_TAGET = "target";

    private EditText mUsrEditText;

    private EditText mPwdEditText;

    private Button mConfirmButton;

    private Dialog mRefreshDialog;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        findViewById(R.id.btn_login_back).setOnClickListener(onBackBtnClickListener);
        mRefreshDialog = new RefreshDialog(this);
        mUsrEditText = (EditText) findViewById(R.id.edit_login_username);
        mPwdEditText = (EditText) findViewById(R.id.edit_login_password);
        mPwdEditText.setOnKeyListener(onPwdEditEnterListener);
        mConfirmButton = (Button) findViewById(R.id.btn_login_confirm);
        mConfirmButton.setOnClickListener(onConfirmBtnClickListener);
        mUsrEditText.addTextChangedListener(textWatcher);
        mPwdEditText.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (WeiboPassport.getInstance().mSsoHandler != null) {
            WeiboPassport.getInstance().mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public void qqlogin(View v) {
        mRefreshDialog.show();
        TencentPassport.getInstance().init(this);
        TencentPassport.getInstance().setActivity(this);
        TencentPassport.getInstance().setLoginListener(this);
        TencentPassport.getInstance().login();
    }

    public void weiboLogin(View v) {
        mRefreshDialog.show();
        WeiboPassport.getInstance().setActivity(this);
        WeiboPassport.getInstance().init(this);
        WeiboPassport.getInstance().setLoginListener(this);
        WeiboPassport.getInstance().login();
    }

    private View.OnKeyListener onPwdEditEnterListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
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

    private View.OnClickListener onConfirmBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRefreshDialog.show();
            LoginTask task = new LoginTask();
            task.addTaskListener(onTaskListener);
            TaskContext taskContext = new TaskContext();
            taskContext.set(LoginTask.KEY_USR, mUsrEditText.getText().toString());
            taskContext.set(LoginTask.KEY_PWD, mPwdEditText.getText().toString());
            task.execute(taskContext);
        }
    };

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

    private Task.OnTaskListener onTaskListener = new Task.OnTaskListener() {

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            Toast.makeText(mContext, R.string.toast_sucess, Toast.LENGTH_SHORT).show();
            String usrPlain = (String) event.getContext().get(LoginTask.KEY_USR);
            String pwdPlain = (String) event.getContext().get(LoginTask.KEY_PWD);
            String token = (String) event.getContext().get(LoginTask.KEY_TOKEN);
            UserManager.getInstance(mContext).login(usrPlain, pwdPlain, token);
            User userinfo = (User) event.getContext().get(LoginTask.KEY_USERINFO);
            UserManager.getInstance(mContext).setUserinfo(userinfo);
            String targetClass = getIntent().getStringExtra(EXTRA_TAGET);
            mRefreshDialog.dismiss();
            if (!TextUtils.isEmpty(targetClass)) {
                try {
                    Intent intent2 = new Intent(mContext, Class.forName(targetClass));
                    mContext.startActivity(intent2);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            finish();
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_failed, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_timeout, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_cancel, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void LoginSuccess(LoginResult res) {
        Log.d(TAG, res.getUsername() + " | " + res.getToken());
        Log.d(TAG, res.getThirdPartyNickName() + " | " + res.getThirdPartyFaceUrl());
        UserManager.getInstance(mContext).login(res.getUsername(), "", res.getToken());
        UserManager.getInstance(mContext).setUserinfo(res.getThirdPartyNickName(), res.getThirdPartyFaceUrl());
        String targetClass = getIntent().getStringExtra(EXTRA_TAGET);
        mRefreshDialog.dismiss();
        if (!TextUtils.isEmpty(targetClass)) {
            try {
                Intent intent2 = new Intent(mContext, Class.forName(targetClass));
                mContext.startActivity(intent2);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        finish();
    }

    @Override
    public void LoginFailed() {
        mRefreshDialog.dismiss();
        Log.w(TAG, "Login failed");
        Toast.makeText(mContext, R.string.toast_failed, Toast.LENGTH_SHORT).show();
    }

}
