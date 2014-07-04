package com.pplive.liveplatform.ui;

import android.app.Activity;
import android.app.Dialog;
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

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.api.live.model.User;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskSucceedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.UpdateInfoTask;
import com.pplive.liveplatform.widget.TopBarView;
import com.pplive.liveplatform.widget.dialog.RefreshDialog;

public class NicknameActivity extends Activity {

    static final String TAG = NicknameActivity.class.getSimpleName();

    public static final int RESULT_NICK_CHANGED = 5802;

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
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            Log.d(TAG, "onTaskFinished");
            mRefreshDialog.dismiss();
            mUserManager.setUserinfo((User) event.getContext().get(UpdateInfoTask.KEY_USERINFO));

            //            Toast.makeText(mContext, R.string.toast_nickname_changed, Toast.LENGTH_SHORT).show();

            setResult(RESULT_NICK_CHANGED);
            finish();
        }

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            Log.d(TAG, "LoginTask onTaskFailed: " + event.getMessage());
            mRefreshDialog.dismiss();

            String message = event.getMessage();

            if (TextUtils.isEmpty(message)) {
                message = getString(R.string.toast_nickname_failed);
            }

            mTextError.setText(message);
        }

        @Override
        public void onTimeout(Task sender, TaskTimeoutEvent event) {
            Log.d(TAG, "LoginTask onTimeout");
            mRefreshDialog.dismiss();

            mTextError.setText(R.string.toast_timeout);
        }

        @Override
        public void onTaskCancel(Task sender, TaskCancelEvent event) {
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
            String oldNickName = mUserManager.getNickname().trim();
            String newNickName = mEditNickname.getText().toString().trim();

            if (!TextUtils.isEmpty(newNickName)) {
                mBtnConfirm.setEnabled(!newNickName.equals(oldNickName));
            } else {
                mBtnConfirm.setEnabled(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nickname);

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(mOnClickBtnConfirmListener);

        mEditNickname = (EditText) findViewById(R.id.edit_nickname);
        mEditNickname.setText(mUserManager.getNickname());
        mEditNickname.setSelection(mEditNickname.length());

        mEditNickname.addTextChangedListener(mTextWatcher);
        mEditNickname.setOnKeyListener(mOnKeyEnterListener);

        mTextError = (TextView) findViewById(R.id.text_error);

        mRefreshDialog = new RefreshDialog(this);
    }

}
