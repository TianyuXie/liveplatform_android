package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.passport.PassportService.CheckCodeType;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.Task.BaseTaskListener;
import com.pplive.liveplatform.core.task.Task.TaskListener;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskSucceedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.GetCheckCodeTask;
import com.pplive.liveplatform.core.task.user.RegisterTask;
import com.pplive.liveplatform.widget.TopBarView;
import com.pplive.liveplatform.widget.dialog.RefreshDialog;

public class RegisterActivity extends Activity {

    final static String TAG = RegisterActivity.class.getSimpleName();

    public static final int REGISTER_SUCCESS = 8201;

    private TopBarView mTopBarView;

    private EditText mEditPhoneNumber;

    private EditText mEditPassword;

    private EditText mEditCheckCode;

    private Button mBtnPhoneCheckCode;

    private Button mBtnRegister;

    private TextView mTextError;

    private Dialog mRefreshDialog;

    private BaseTaskListener mOnCheckcodeTaskListener = new BaseTaskListener() {

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            showErrorMsg(event.getMessage());
        }
    };

    private View.OnKeyListener mOnFinalEnterListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (mBtnRegister.isEnabled() && KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {

                mBtnRegister.performClick();

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

            if (!TextUtils.isEmpty(mEditPhoneNumber.getText()) && !TextUtils.isEmpty(mEditPassword.getText()) && !TextUtils.isEmpty(mEditCheckCode.getText())) {
                mBtnRegister.setEnabled(true);
            } else {
                mBtnRegister.setEnabled(false);
            }
        }
    };

    private View.OnClickListener mOnClickBtnRegisterListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mRefreshDialog.show();

            RegisterTask registerTask = new RegisterTask();

            TaskContext taskContext = new TaskContext();
            taskContext.set(RegisterTask.KEY_PHONE_NUMBER, mEditPhoneNumber.getText().toString());
            taskContext.set(RegisterTask.KEY_PASSWORD, mEditPassword.getText().toString());
            taskContext.set(RegisterTask.KEY_CHECK_CODE, mEditCheckCode.getText().toString());
            registerTask.addTaskListener(mOnRegisterListener);
            registerTask.execute(taskContext);
        }
    };

    private TaskListener mOnRegisterListener = new BaseTaskListener() {

        @Override
        public void onTimeout(Task sender, TaskTimeoutEvent event) {
            mRefreshDialog.dismiss();

            showErrorMsg(getString(R.string.toast_timeout));
        }

        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            mRefreshDialog.dismiss();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

            intent.putExtra(LoginActivity.EXTRA_USERNAME, event.getContext().getString(RegisterTask.KEY_PHONE_NUMBER));
            intent.putExtra(LoginActivity.EXTRA_PASSWORD, event.getContext().getString(RegisterTask.KEY_PASSWORD));
            intent.putExtra(LoginActivity.EXTRA_FROM_REGISTER, true);

            setResult(REGISTER_SUCCESS, intent);

            startActivity(intent);

            finish();
        }

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {

            mRefreshDialog.dismiss();

            String message = event.getMessage();
            if (TextUtils.isEmpty(message)) {
                message = getString(R.string.register_failed);
            }

            showErrorMsg(message);

        }

        @Override
        public void onTaskCancel(Task sender, TaskCancelEvent event) {
            mRefreshDialog.dismiss();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEditPhoneNumber = (EditText) findViewById(R.id.edit_phone_number);
        mEditPhoneNumber.addTextChangedListener(mTextWatcher);

        mEditPassword = (EditText) findViewById(R.id.edit_register_password);
        mEditPassword.addTextChangedListener(mTextWatcher);

        mEditCheckCode = (EditText) findViewById(R.id.edit_checkcode);
        mEditCheckCode.addTextChangedListener(mTextWatcher);
        mEditCheckCode.setOnKeyListener(mOnFinalEnterListener);

        mBtnPhoneCheckCode = (Button) findViewById(R.id.btn_send_phone_checkcode);
        mBtnPhoneCheckCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String number = mEditPhoneNumber.getText().toString();

                sendCheckCode(number);
            }
        });

        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(mOnClickBtnRegisterListener);

        mTextError = (TextView) findViewById(R.id.text_error);

        mRefreshDialog = new RefreshDialog(this);

    }

    private void sendCheckCode(String phone) {
        GetCheckCodeTask checkCodeTask = new GetCheckCodeTask();
        checkCodeTask.addTaskListener(mOnCheckcodeTaskListener);

        TaskContext context = new TaskContext();
        context.set(Extra.KEY_PHONE_NUMBER, phone);
        context.set(Extra.KEY_CODE_TYPE, CheckCodeType.REGISTER);

        checkCodeTask.execute(context);
    }

    private void showErrorMsg(String msg) {
        mTextError.setText(msg);
    }

    private void hideErrorMsg() {
        mTextError.setText("");
    }
}
