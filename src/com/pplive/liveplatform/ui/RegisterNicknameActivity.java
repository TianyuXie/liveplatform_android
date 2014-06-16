package com.pplive.liveplatform.ui;

import java.io.Serializable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskSucceedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.UpdateInfoTask;
import com.pplive.liveplatform.ui.widget.TopBarView;
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;

public class RegisterNicknameActivity extends Activity {

    static final String TAG = RegisterNicknameActivity.class.getSimpleName();

    private TopBarView mTopBarView;

    private EditText mEditNickname;

    private Button mBtnConfirm;

    private TextView mTextError;

    private Dialog mRefreshDialog;

    private UserManager mUserManager = UserManager.getInstance(this);

    private View.OnClickListener mOnClickBtnConfirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRefreshDialog.show();
            UpdateInfoTask task = new UpdateInfoTask();
            task.addTaskListener(onTaskListener);
            TaskContext taskContext = new TaskContext();
            taskContext.set(UpdateInfoTask.KEY_USERNAME, mUserManager.getUsernamePlain());
            taskContext.set(UpdateInfoTask.KEY_NICKNAME, mEditNickname.getText().toString());
            taskContext.set(UpdateInfoTask.KEY_TOKEN, mUserManager.getToken());
            task.execute(taskContext);
        }
    };

    private View.OnKeyListener mOnKeyEnterListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (mBtnConfirm.isEnabled() && KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
                mBtnConfirm.performClick();
                return true;
            }

            return false;
        }
    };

    private Task.TaskListener onTaskListener = new Task.BaseTaskListener() {

        @Override
        public void onTaskSucceed(Object sender, TaskSucceedEvent event) {
            Log.d(TAG, "onTaskFinished");

            mRefreshDialog.dismiss();
            mUserManager.setUserinfo((User) event.getContext().get(UpdateInfoTask.KEY_USERINFO));

            //            Toast.makeText(mContext, R.string.toast_nickname_changed, Toast.LENGTH_SHORT).show();

            redirect();
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "LoginTask onTaskFailed: " + event.getMessage());
            mRefreshDialog.dismiss();

            String message = event.getMessage();

            if (TextUtils.isEmpty(message)) {
                message = getString(R.string.toast_nickname_failed);
            }

            mTextError.setText(message);
        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            Log.d(TAG, "LoginTask onTimeout");
            mRefreshDialog.dismiss();

            mTextError.setText(R.string.toast_timeout);
        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            Log.d(TAG, "LoginTask onTaskCancel");
            mRefreshDialog.dismiss();
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
            String newNickName = mEditNickname.getText().toString().trim();

            if (!TextUtils.isEmpty(newNickName)) {
                mBtnConfirm.setEnabled(true);
            } else {
                mBtnConfirm.setEnabled(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_nickname);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setRightBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                redirect();
            }
        });

        mEditNickname = (EditText) findViewById(R.id.edit_nickname);
        mEditNickname.addTextChangedListener(mTextWatcher);
        mEditNickname.setOnKeyListener(mOnKeyEnterListener);

        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(mOnClickBtnConfirmListener);

        mTextError = (TextView) findViewById(R.id.text_error);

        mRefreshDialog = new RefreshDialog(this);
    }

    @Override
    public void onBackPressed() {
        redirect();
    }

    @SuppressWarnings("unchecked")
    private void redirect() {

        Intent intent = getIntent();
        if (null != intent) {
            Class<? extends Activity> target = null;
            Serializable obj = intent.getSerializableExtra(Extra.KEY_REDIRECT);
            if (null != obj && obj instanceof Class<?>) {
                target = (Class<? extends Activity>) obj;
            }

            if (null != target) {
                Intent newIntent = new Intent(this, target);
                startActivity(newIntent);
            }
        }

        finish();
    }

}
