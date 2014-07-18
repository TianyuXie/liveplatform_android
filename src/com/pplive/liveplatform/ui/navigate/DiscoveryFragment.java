package com.pplive.liveplatform.ui.navigate;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.adapter.DiscoveryAdapter;
import com.pplive.liveplatform.core.api.live.ProgramAPI;
import com.pplive.liveplatform.core.api.live.model.Subject;
import com.pplive.liveplatform.ui.ChannelActivity;
import com.pplive.liveplatform.ui.SearchActivity;

public class DiscoveryFragment extends Fragment {

    static final String TAG = DiscoveryFragment.class.getSimpleName();

    private Activity mActivity;

    private PullToRefreshExpandableListView mListViewChannel;

    private DiscoveryAdapter mAdapter;

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

        mListViewChannel.setOnPullEventListener(new OnPullEventListener<ExpandableListView>() {

            @Override
            public void onPullEvent(PullToRefreshBase<ExpandableListView> refreshView, State state, Mode direction) {
                Log.d(TAG, "direction: " + direction);
            }

        });

        mListViewChannel.getRefreshableView().setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d(TAG, "onChildClick");

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
    }

    @Override
    public void onStart() {
        super.onStart();

        AsyncTaskGetSubjects task = new AsyncTaskGetSubjects();
        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    class AsyncTaskGetSubjects extends AsyncTask<Void, Void, List<Subject>> {

        @Override
        protected List<Subject> doInBackground(Void... params) {
            List<Subject> list = null;

            try {
                list = ProgramAPI.getInstance().getSubjects();
            } catch (Exception e) {
                Log.w(TAG, e.toString());
            }

            return list;
        }

        @Override
        protected void onPostExecute(List<Subject> result) {

            if (null != result) {
                mAdapter.setData(result, null);
            }

        }
    }
}
