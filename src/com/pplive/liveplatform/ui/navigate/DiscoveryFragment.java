package com.pplive.liveplatform.ui.navigate;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.adapter.DiscoveryAdapter;
import com.pplive.liveplatform.core.api.live.model.Subject;
import com.pplive.liveplatform.core.api.live.model.Tag;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.search.DiscoveryPageInitTask;
import com.pplive.liveplatform.ui.ChannelActivity;
import com.pplive.liveplatform.ui.SearchActivity;

public class DiscoveryFragment extends Fragment {

    static final String TAG = DiscoveryFragment.class.getSimpleName();

    private Activity mActivity;

    private PullToRefreshExpandableListView mListViewChannel;

    private DiscoveryAdapter mAdapter;

    private Task.TaskListener mTaskListener = new Task.BaseTaskListener() {

        @SuppressWarnings("unchecked")
        public void onTaskSucceed(Task sender, com.pplive.liveplatform.task.TaskSucceedEvent event) {
            mListViewChannel.onRefreshComplete();

            TaskContext context = event.getContext();

            List<Subject> subjects = (List<Subject>) context.get(Extra.KEY_RESULT_SUBJECTS);
            List<Tag> tags = (List<Tag>) context.get(Extra.KEY_RESULT_TAGS);

            mAdapter.setData(subjects, tags);
        };

        public void onTaskFailed(Task sender, com.pplive.liveplatform.task.TaskFailedEvent event) {
            mListViewChannel.onRefreshComplete();

        };
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_discovery, container, false);

        mListViewChannel = (PullToRefreshExpandableListView) layout.findViewById(R.id.expandable_listview);

        mListViewChannel.setOnRefreshListener(new OnRefreshListener<ExpandableListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                load();
            }

        });

        mListViewChannel.getRefreshableView().setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (0 == groupPosition) {
                    Intent intent = new Intent(mActivity, ChannelActivity.class);
                    intent.putExtra(Extra.KEY_SUBJECT, (Subject) mAdapter.getChild(groupPosition, childPosition));
                    startActivity(intent);

                    return true;
                }

                return false;
            }
        });

        mListViewChannel.getRefreshableView().setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        EditText searchInput = (EditText) layout.findViewById(R.id.search_input_bar_edit_view);
        searchInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SearchActivity.class);
                startActivity(intent);
            }
        });

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new DiscoveryAdapter(mActivity);
        mListViewChannel.getRefreshableView().setAdapter(mAdapter);
        mListViewChannel.getRefreshableView().expandGroup(0);
        mListViewChannel.getRefreshableView().expandGroup(1);
    }

    @Override
    public void onStart() {
        super.onStart();

        load();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void load() {
        DiscoveryPageInitTask task = new DiscoveryPageInitTask();
        task.addTaskListener(mTaskListener);
        task.execute();
    }

}
