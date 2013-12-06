package com.pplive.liveplatform.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.user.ProgramTask;
import com.pplive.liveplatform.ui.userpage.UserpageProgramAdapter;

public class UserpageActivity extends Activity {

    private List<Program> mPrograms;
    private ListView mListView;
    private UserpageProgramAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userpage);

        mPrograms = new ArrayList<Program>();
        mAdapter = new UserpageProgramAdapter(this, mPrograms);

        findViewById(R.id.btn_userpage_back).setOnClickListener(onBackBtnClickListener);
        findViewById(R.id.btn_userpage_settings).setOnClickListener(onSettingsBtnClickListener);
        mListView = (ListView) findViewById(R.id.list_userpage_program);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgramTask task = new ProgramTask();
        task.addTaskListener(getTaskListener);
        TaskContext taskContext = new TaskContext();
        task.execute(taskContext);
    }

    private View.OnClickListener onBackBtnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener onSettingsBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(UserpageActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    };

    private Task.OnTaskListener getTaskListener = new Task.OnTaskListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            mPrograms.clear();
            mPrograms.addAll((List<Program>) event.getContext().get(ProgramTask.KEY_RESULT));
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            // TODO Auto-generated method stub

        }

    };

}
