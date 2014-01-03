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
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.UpdateNickTask;
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;

public class NicknameActivity extends Activity {
    static final String TAG = "_NicknameActivity";

    private EditText mNickEditText;

    private Button mConfirmButton;

    private Dialog mRefreshDialog;

    private Context mContext;

    public static final int RESULT_NICK_CHANGED = 5802;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nickname);
        findViewById(R.id.btn_nickname_back).setOnClickListener(onBackBtnClickListener);
        mConfirmButton = (Button) findViewById(R.id.btn_nickname_confirm);
        mConfirmButton.setOnClickListener(onConfirmBtnClickListener);
        mNickEditText = (EditText) findViewById(R.id.edit_nickname);
        mNickEditText.addTextChangedListener(textWatcher);
        mNickEditText.setOnKeyListener(onNickEditEnterListener);
        mRefreshDialog = new RefreshDialog(this);
    }

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
            UpdateNickTask task = new UpdateNickTask();
            task.addTaskListener(onTaskListener);
            TaskContext taskContext = new TaskContext();
            taskContext.set(UpdateNickTask.KEY_USERNAME, UserManager.getInstance(mContext).getUsernamePlain());
            taskContext.set(UpdateNickTask.KEY_NICKNAME, mNickEditText.getText().toString());
            taskContext.set(UpdateNickTask.KEY_TOKEN, UserManager.getInstance(mContext).getToken());
            task.execute(taskContext);
        }
    };

    private View.OnKeyListener onNickEditEnterListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                mConfirmButton.performClick();
                return true;
            }
            return false;
        }
    };

    private Task.OnTaskListener onTaskListener = new Task.OnTaskListener() {

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mRefreshDialog.dismiss();
            UserManager.getInstance(mContext).setUserinfo((User) event.getContext().get(UpdateNickTask.KEY_USERINFO));
            setResult(RESULT_NICK_CHANGED);
            finish();
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "LoginTask onTaskFailed: " + event.getMessage());
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_failed, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "LoginTask onTimeout");
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_timeout, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "LoginTask onTaskCancel");
            mRefreshDialog.dismiss();
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
            if (!TextUtils.isEmpty(mNickEditText.getText())) {
                mConfirmButton.setEnabled(true);
            } else {
                mConfirmButton.setEnabled(false);
            }
        }
    };

}
