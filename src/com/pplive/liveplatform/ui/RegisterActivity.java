package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import com.pplive.liveplatform.core.task.Task.OnTaskListener;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.CheckCodeTask;
import com.pplive.liveplatform.core.task.user.RegisterTask;
import com.pplive.liveplatform.ui.widget.AsyncImageView;
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;

public class RegisterActivity extends Activity {
    final static String TAG = "_RegisterActivity";

    private EditText mUsrEditText;

    private EditText mPwdEditText;

    private EditText mCheckEditText;

    private Button mConfirmButton;

    private Dialog mRefreshDialog;

    private AsyncImageView mCheckCodeImage;

    private String mGuid;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        findViewById(R.id.btn_register_back).setOnClickListener(onBackBtnClickListener);

        mCheckCodeImage = (AsyncImageView) findViewById(R.id.image_register_checkcode);
        mCheckCodeImage.setOnClickListener(onImageClickListener);
        mUsrEditText = (EditText) findViewById(R.id.edit_register_username);
        mPwdEditText = (EditText) findViewById(R.id.edit_register_password);
        mCheckEditText = (EditText) findViewById(R.id.edit_register_checkcode);
        mCheckEditText.setOnKeyListener(onFinalEnterListener);
        mUsrEditText.addTextChangedListener(textWatcher);
        mPwdEditText.addTextChangedListener(textWatcher);
        mCheckEditText.addTextChangedListener(textWatcher);

        mConfirmButton = (Button) findViewById(R.id.btn_register_confirm);
        mConfirmButton.setOnClickListener(onConfirmBtnClickListener);
        mRefreshDialog = new RefreshDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshCheckCode();
    }

    private void refreshCheckCode() {
        CheckCodeTask checkCodeTask = new CheckCodeTask();
        checkCodeTask.addTaskListener(onCheckcodeTaskListener);
        checkCodeTask.execute();
    }

    private View.OnClickListener onBackBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener onImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            refreshCheckCode();
        }
    };

    private OnTaskListener onCheckcodeTaskListener = new OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mGuid = event.getContext().getString(CheckCodeTask.KEY_GUID);
            String url = event.getContext().getString(CheckCodeTask.KEY_IMAGE);
            mCheckCodeImage.setImageAsync(url);
            Log.d(TAG, String.format("mGuid: %s, url: %s", mGuid, url));
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

    private View.OnKeyListener onFinalEnterListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.d(TAG, "onFinalEnterListener");
                mConfirmButton.performClick();
                return true;
            }
            return false;
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
            if (!TextUtils.isEmpty(mUsrEditText.getText()) && !TextUtils.isEmpty(mPwdEditText.getText()) && !TextUtils.isEmpty(mCheckEditText.getText())) {
                Log.d(TAG, "mConfirmButton.setEnabled(true)");
                mConfirmButton.setEnabled(true);
            } else {
                Log.d(TAG, "mConfirmButton.setEnabled(false)");
                mConfirmButton.setEnabled(false);
            }
        }
    };

    private View.OnClickListener onConfirmBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRefreshDialog.show();
            RegisterTask registerTask = new RegisterTask();
            TaskContext taskContext = new TaskContext();
            taskContext.set(RegisterTask.KEY_USERNAME, mUsrEditText.getText().toString());
            taskContext.set(RegisterTask.KEY_PASSWORD, mPwdEditText.getText().toString());
            taskContext.set(RegisterTask.KEY_CHECKCODE, mCheckEditText.getText().toString());
            taskContext.set(RegisterTask.KEY_GUID, mGuid);
            registerTask.addTaskListener(onRegisterListener);
            registerTask.execute(taskContext);
        }
    };

    private OnTaskListener onRegisterListener = new OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_timeout, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_sucess, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            mRefreshDialog.dismiss();
            String message = event.getMessage();
            if (TextUtils.isEmpty(message)) {
                message = getString(R.string.register_fail);
            }
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            refreshCheckCode();
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_cancel, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }
    };

}
