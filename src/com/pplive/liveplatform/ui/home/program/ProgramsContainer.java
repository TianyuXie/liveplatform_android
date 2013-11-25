package com.pplive.liveplatform.ui.home.program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.rest.model.Program;
import com.pplive.liveplatform.core.rest.model.Watch;
import com.pplive.liveplatform.core.task.Task;
import com.pplive.liveplatform.core.task.TaskCancelEvent;
import com.pplive.liveplatform.core.task.TaskContext;
import com.pplive.liveplatform.core.task.TaskFailedEvent;
import com.pplive.liveplatform.core.task.TaskFinishedEvent;
import com.pplive.liveplatform.core.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.core.task.TaskTimeoutEvent;
import com.pplive.liveplatform.core.task.home.GetMediaTask;
import com.pplive.liveplatform.ui.widget.RefreshGridView;

public class ProgramsContainer extends RelativeLayout {
    static final String TAG = "ProgramsContainer";

    private List<Program> mPrograms;
    private ProgramAdapter mAdapter;
    private RefreshGridView mGridView;

    private boolean mItemClickable;

    public ProgramsContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPrograms = new ArrayList<Program>();
        mAdapter = new ProgramAdapter(context, mPrograms);
        mItemClickable = true;

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.layout_home_container, this);
        mGridView = (RefreshGridView) root.findViewById(R.id.gridview_home_results);
        LinearLayout head = (LinearLayout) root.findViewById(R.id.layout_pull_header);
        head.addView(mGridView.getHeader(), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        mGridView.setAdapter(mAdapter);
        mGridView.setOnRefreshListener(onRefreshListener);
        mGridView.setOnItemClickListener(onItemClickListener);
    }

    public ProgramsContainer(Context context) {
        this(context, null);
    }

    @SuppressWarnings("unchecked")
    public void refreshData(Object data) {
        mPrograms.clear();
        mPrograms.addAll((Collection<Program>) data);
        mAdapter.notifyDataSetChanged();
    }

    private RefreshGridView.OnRefreshListener onRefreshListener = new RefreshGridView.OnRefreshListener() {

        @Override
        public void onRefresh() {
            new AsyncTask<Void, Void, Void>() {
                protected Void doInBackground(Void... params) {
                    //TODO
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    mGridView.onRefreshComplete();
                }
            }.execute();
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!mGridView.isBusy() && mItemClickable) {
                Log.d(TAG, "onItemClick");
                long pid = mPrograms.get(position).getId();
                String username = "xiety0001";
                GetMediaTask task = new GetMediaTask();
                task.addTaskListener(onTaskListener);
                TaskContext taskContext = new TaskContext();
                taskContext.set(GetMediaTask.KEY_PID, pid);
                taskContext.set(GetMediaTask.KEY_USERNAME, username);
                task.execute(taskContext);
            }
        }
    };

    private Task.OnTaskListener onTaskListener = new Task.OnTaskListener() {

        @Override
        public void onTimeout(Object sender, TaskTimeoutEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        @SuppressWarnings("unchecked")
        public void onTaskFinished(Object sender, TaskFinishedEvent event) {
            Toast.makeText(getContext(), R.string.toast_sucess, Toast.LENGTH_SHORT).show();
            List<Watch> watchs = (List<Watch>) event.getContext().get(GetMediaTask.KEY_RESULT);
            Log.d(TAG, watchs.get(0).getWatchStringList().get(0));
        }

        @Override
        public void onTaskFailed(Object sender, TaskFailedEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTaskCancel(Object sender, TaskCancelEvent event) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(Object sender, TaskProgressChangedEvent event) {
            // TODO Auto-generated method stub

        }
    };

    public void setItemClickable(boolean clickable) {
        this.mItemClickable = clickable;
    }

}
