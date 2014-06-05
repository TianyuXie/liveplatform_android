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

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.task.Task.OnTaskListener;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.CheckCodeTask;
import com.pplive.liveplatform.core.task.user.RegisterTask;
import com.pplive.liveplatform.ui.widget.TopBarView;
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;

public class RegisterActivity extends Activity {

    final static String TAG = RegisterActivity.class.getSimpleName();

    public static final int FROM_LOGIN = 7101;

    public static final int REGISTER_SUCCESS = 8201;

    private TopBarView mTopBarView;

    private EditText mEditTextPhoneNumber;

    private EditText mEditTextPassword;

    private EditText mEditTextCheckCode;

    private Button mBtnPhoneCheckCode;

    private Button mBtnRegister;

    private TextView mTextError;

    private Dialog mRefreshDialog;

    //    private AsyncImageView mCheckCodeImage;

    private OnTaskListener mOnCheckcodeTaskListener = new OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            //            mGuid = event.getContext().getString(CheckCodeTask.KEY_GUID);
            //            mCheckCodeImage.setImageAsync(event.getContext().getString(CheckCodeTask.KEY_IMAGE));
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            showErrorMsg(event.getMessage());
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

    private View.OnKeyListener mOnFinalEnterListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                //                mConfirmButton.performClick();
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

            if (!TextUtils.isEmpty(mEditTextPhoneNumber.getText()) && !TextUtils.isEmpty(mEditTextPassword.getText())
                    && !TextUtils.isEmpty(mEditTextCheckCode.getText())) {
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
            taskContext.set(RegisterTask.KEY_PHONE_NUMBER, mEditTextPhoneNumber.getText().toString());
            taskContext.set(RegisterTask.KEY_PASSWORD, mEditTextPassword.getText().toString());
            taskContext.set(RegisterTask.KEY_CHECK_CODE, mEditTextCheckCode.getText().toString());
            registerTask.addTaskListener(mOnRegisterListener);
            registerTask.execute(taskContext);
        }
    };

    private OnTaskListener mOnRegisterListener = new OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            mRefreshDialog.dismiss();

            showErrorMsg(getString(R.string.toast_timeout));
        }

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mRefreshDialog.dismiss();

            Intent data = new Intent(getApplicationContext(), LoginActivity.class);

            data.putExtra(LoginActivity.EXTRA_USERNAME, event.getContext().getString(RegisterTask.KEY_PHONE_NUMBER));
            data.putExtra(LoginActivity.EXTRA_PASSWORD, event.getContext().getString(RegisterTask.KEY_PASSWORD));

            setResult(REGISTER_SUCCESS, data);

            RegisterActivity.this.startActivity(data);

            finish();
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            mRefreshDialog.dismiss();
            String message = event.getMessage();
            if (TextUtils.isEmpty(message)) {
                message = getString(R.string.register_failed);
            }

            showErrorMsg(message);
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            mRefreshDialog.dismiss();
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
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

        mEditTextPhoneNumber = (EditText) findViewById(R.id.edit_register_phone_number);
        mEditTextPhoneNumber.addTextChangedListener(mTextWatcher);

        mEditTextPassword = (EditText) findViewById(R.id.edit_register_password);
        mEditTextPassword.addTextChangedListener(mTextWatcher);

        mEditTextCheckCode = (EditText) findViewById(R.id.edit_register_checkcode);
        mEditTextCheckCode.setOnKeyListener(mOnFinalEnterListener);
        mEditTextCheckCode.addTextChangedListener(mTextWatcher);

        mBtnPhoneCheckCode = (Button) findViewById(R.id.btn_send_phone_checkcode);
        mBtnPhoneCheckCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String phone = mEditTextPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    showErrorMsg("手机不能为空");
                } else {
                    sendCheckCode(phone);
                }
            }
        });

        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(mOnClickBtnRegisterListener);

        mTextError = (TextView) findViewById(R.id.text_error);

        mRefreshDialog = new RefreshDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void sendCheckCode(String phone) {
        CheckCodeTask checkCodeTask = new CheckCodeTask();
        checkCodeTask.addTaskListener(mOnCheckcodeTaskListener);

        TaskContext context = new TaskContext();
        context.set(CheckCodeTask.KEY_PHONE_NUMBER, phone);

        checkCodeTask.execute(context);
    }

    private void showErrorMsg(String msg) {
        mTextError.setText(msg);
    }

    private void hideErrorMsg() {
        mTextError.setText("");
    }
}
