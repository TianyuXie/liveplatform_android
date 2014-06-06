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
import com.pplive.liveplatform.core.task.user.UpdateInfoTask;
import com.pplive.liveplatform.ui.widget.TopBarView;
import com.pplive.liveplatform.ui.widget.dialog.RefreshDialog;

public class NicknameActivity extends Activity {

    static final String TAG = NicknameActivity.class.getSimpleName();

    public static final int RESULT_NICK_CHANGED = 5802;

    private Context mContext;

    private TopBarView mTopBarView;

    private EditText mEditNickname;

    private Button mConfirmButton;

    private Dialog mRefreshDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nickname);

        mContext = this;

        mTopBarView = (TopBarView) findViewById(R.id.top_bar);
        mTopBarView.setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mConfirmButton = (Button) findViewById(R.id.btn_confirm);
        mConfirmButton.setOnClickListener(mOnConfirmBtnClickListener);

        mEditNickname = (EditText) findViewById(R.id.edit_nickname);
        mEditNickname.setText(UserManager.getInstance(mContext).getNickname());

        mEditNickname.addTextChangedListener(textWatcher);
        mEditNickname.setOnKeyListener(mOnNickEditEnterListener);

        mRefreshDialog = new RefreshDialog(this);
    }

    private View.OnClickListener mOnConfirmBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRefreshDialog.show();
            UpdateInfoTask task = new UpdateInfoTask();
            task.addTaskListener(onTaskListener);
            TaskContext taskContext = new TaskContext();
            taskContext.set(UpdateInfoTask.KEY_USERNAME, UserManager.getInstance(mContext).getUsernamePlain());
            taskContext.set(UpdateInfoTask.KEY_NICKNAME, mEditNickname.getText().toString());
            taskContext.set(UpdateInfoTask.KEY_TOKEN, UserManager.getInstance(mContext).getToken());
            task.execute(taskContext);
        }
    };

    private View.OnKeyListener mOnNickEditEnterListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && mConfirmButton.isEnabled()) {
                mConfirmButton.performClick();
                return true;
            }
            return false;
        }
    };

    private Task.OnTaskListener onTaskListener = new Task.OnTaskListener() {

        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            Log.d(TAG, "onTaskFinished");
            mRefreshDialog.dismiss();
            Toast.makeText(mContext, R.string.toast_nickname_changed, Toast.LENGTH_SHORT).show();
            UserManager.getInstance(mContext).setUserinfo((User) event.getContext().get(UpdateInfoTask.KEY_USERINFO));
            setResult(RESULT_NICK_CHANGED);
            finish();
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            Log.d(TAG, "LoginTask onTaskFailed: " + event.getMessage());
            mRefreshDialog.dismiss();
            String message = event.getMessage();
            if (TextUtils.isEmpty(message)) {
                message = getString(R.string.toast_nickname_failed);
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
            String oldNickName = UserManager.getInstance(mContext).getNickname();
            String newNickName = mEditNickname.getText().toString().trim();

            if (!TextUtils.isEmpty(newNickName)) {
                mConfirmButton.setEnabled(!newNickName.equals(oldNickName));
            } else {
                mConfirmButton.setEnabled(false);
            }
        }
    };

}
