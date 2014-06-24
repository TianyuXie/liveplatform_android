package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.Task.BaseTaskListener;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskSucceedEvent;
import com.pplive.liveplatform.core.task.user.ResetPasswordTask;
import com.pplive.liveplatform.widget.TopBarView;

public class PasswordResetStep2Activity extends Activity {

    private TopBarView mTopBarView;

    private EditText mEditPassword;

    private EditText mEditRepeatPassword;

    private TextView mTextError;

    private Button mBtnConfirm;

    private String mLoginName;

    private BaseTaskListener mRestPasswordTaskListener = new BaseTaskListener() {

        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {

            finish();
        };

        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            showErrorMsg(event.getMessage());
        };
    };

    private View.OnKeyListener mOnFinalEnterListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (mBtnConfirm.isEnabled() && KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {

                mBtnConfirm.performClick();

                return true;
            }

            return false;
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

            if (!TextUtils.isEmpty(mEditPassword.getText()) && !TextUtils.isEmpty(mEditRepeatPassword.getText())) {
                mBtnConfirm.setEnabled(true);
            } else {
                mBtnConfirm.setEnabled(false);
            }
        }
    };

    private View.OnClickListener mOnClickBtnConfirmListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String loginName = mLoginName;
            String password = mEditPassword.getText().toString();

            if (password.equals(mEditRepeatPassword.getText().toString())) {
                ResetPasswordTask task = new ResetPasswordTask();
                task.addTaskListener(mRestPasswordTaskListener);

                TaskContext context = new TaskContext();
                context.set(Extra.KEY_LOGIN_NAME, loginName);
                context.set(Extra.KEY_PASSWORD, password);

                task.execute(context);
            } else {
                showErrorMsg("密码不一致");
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pwd_reset_step2);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(mOnClickBtnConfirmListener);

        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mEditPassword.addTextChangedListener(mTextWatcher);

        mEditRepeatPassword = (EditText) findViewById(R.id.edit_repeat_password);
        mEditRepeatPassword.addTextChangedListener(mTextWatcher);
        mEditRepeatPassword.setOnKeyListener(mOnFinalEnterListener);

        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(mOnClickBtnConfirmListener);

        mTextError = (TextView) findViewById(R.id.text_error);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mLoginName = getIntent().getStringExtra(Extra.KEY_LOGIN_NAME);
    }

    private void showErrorMsg(String msg) {
        mTextError.setText(msg);
    }

    private void hideErrorMsg() {
        mTextError.setText("");
    }
}
