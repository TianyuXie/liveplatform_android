package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pplive.android.view.TopBarView;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.passport.PassportAPI.CheckCodeType;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskFailedEvent;
import com.pplive.liveplatform.task.TaskSucceedEvent;
import com.pplive.liveplatform.task.Task.BaseTaskListener;
import com.pplive.liveplatform.task.passport.CheckCodeTask;
import com.pplive.liveplatform.task.passport.GetCheckCodeTask;

public class PasswordResetStep1Activity extends Activity {

    static final String TAG = PasswordResetStep1Activity.class.getSimpleName();

    private TopBarView mTopBarView;

    private EditText mEditPhoneNumber;

    private EditText mEditCheckCode;

    private Button mBtnPhoneCheckCode;

    private TextView mTextError;

    private Button mBtnNext;

    private BaseTaskListener mOnGetCheckCodeTaskListener = new BaseTaskListener() {

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            showErrorMsg(event.getMessage());
        }
    };

    private BaseTaskListener mOnCheckCodeTaskListener = new BaseTaskListener() {

        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            TaskContext context = event.getContext();

            Intent intent = new Intent(PasswordResetStep1Activity.this, PasswordResetStep2Activity.class);
            intent.putExtra(Extra.KEY_USERNAME, context.getString(Extra.KEY_PHONE_NUMBER));

            startActivity(intent);

            finish();
        };

        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            showErrorMsg(event.getMessage());
        };
    };

    private View.OnKeyListener mOnFinalEnterListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (mBtnNext.isEnabled() && KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {

                mBtnNext.performClick();

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

            if (!TextUtils.isEmpty(mEditPhoneNumber.getText()) && !TextUtils.isEmpty(mEditCheckCode.getText())) {
                mBtnNext.setEnabled(true);
            } else {
                mBtnNext.setEnabled(false);
            }
        }
    };

    private View.OnClickListener mOnClickBtnNextListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            CheckCodeTask checkCodeTask = new CheckCodeTask();
            checkCodeTask.addTaskListener(mOnCheckCodeTaskListener);

            TaskContext context = new TaskContext();
            context.set(Extra.KEY_PHONE_NUMBER, mEditPhoneNumber.getText().toString());
            context.set(Extra.KEY_CHECK_CODE, mEditCheckCode.getText().toString());

            checkCodeTask.execute(context);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pwd_reset_step1);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEditPhoneNumber = (EditText) findViewById(R.id.edit_phone_number);
        mEditPhoneNumber.addTextChangedListener(mTextWatcher);

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

        mBtnNext = (Button) findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(mOnClickBtnNextListener);

        mTextError = (TextView) findViewById(R.id.text_error);
    }

    private void sendCheckCode(String phone) {
        GetCheckCodeTask checkCodeTask = new GetCheckCodeTask();
        checkCodeTask.addTaskListener(mOnGetCheckCodeTaskListener);

        TaskContext context = new TaskContext();
        context.set(Extra.KEY_PHONE_NUMBER, phone);
        context.set(Extra.KEY_CODE_TYPE, CheckCodeType.RESET_PWD);

        checkCodeTask.execute(context);
    }

    private void showErrorMsg(String msg) {
        mTextError.setText(msg);
    }

    private void hideErrorMsg() {
        mTextError.setText("");
    }

}
