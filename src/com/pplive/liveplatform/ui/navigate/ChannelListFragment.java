package com.pplive.liveplatform.ui.navigate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.ProgramService;
import com.pplive.liveplatform.core.service.live.model.Subject;
import com.pplive.liveplatform.ui.widget.SearchBarView;
import com.pplive.liveplatform.ui.widget.image.AsyncImageView;

public class ChannelListFragment extends Fragment {

    static final String TAG = ChannelListFragment.class.getSimpleName();

    private SearchBarView mSearchTopBarView;

    private PullToRefreshListView mListViewChannel;

    private CallbackListener mCallbackListener;

    private SubjectAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_channel_list, container, false);

        mSearchTopBarView = (SearchBarView) layout.findViewById(R.id.search_top_bar);
        mSearchTopBarView.setTitle(R.string.navbar_channel);

        mListViewChannel = (PullToRefreshListView) layout.findViewById(R.id.listview_channel);

        mListViewChannel.setOnPullEventListener(new OnPullEventListener<ListView>() {

            @Override
            public void onPullEvent(PullToRefreshBase<ListView> refreshView, State state, Mode direction) {
                Log.d(TAG, "direction: " + direction);
            }

        });

        mListViewChannel.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (null != mCallbackListener) {
                    mCallbackListener.onSubjectSelected((mAdapter.getItem(position)));
                }
            }
        });

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new SubjectAdapter(getActivity());
        mListViewChannel.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        AsyncTaskGetSubjects task = new AsyncTaskGetSubjects();
        task.execute();
    }

    public void setCallbackListener(CallbackListener listener) {
        mCallbackListener = listener;
    }

    public interface CallbackListener {
        void onSubjectSelected(Subject subject);
    }

    class AsyncTaskGetSubjects extends AsyncTask<Void, Void, List<Subject>> {

        @Override
        protected List<Subject> doInBackground(Void... params) {
            List<Subject> list = null;

            try {
                list = ProgramService.getInstance().getSubjects();
            } catch (Exception e) {

            }

            return list;
        }

        @Override
        protected void onPostExecute(List<Subject> result) {

            if (null != result) {
                mAdapter.setSubjects(result);
            }

        }
    }
}

class SubjectAdapter extends BaseAdapter {

    static final int CHANNEL_ORIGINAL_ID = 1;

    private LayoutInflater mInflater;

    private List<Subject> mSubjects;

    public SubjectAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    public void setSubjects(List<Subject> list) {
        if (null == list) {
            return;
        }

        for (int index = 0; index < list.size(); ++index) {
            if (CHANNEL_ORIGINAL_ID == list.get(index).getId()) {
                list.remove(index);
            }
        }

        Collections.sort(list, new Comparator<Subject>() {

            @Override
            public int compare(Subject lhs, Subject rhs) {
                return lhs.getSeq() - rhs.getSeq();
            }
        });

        mSubjects = list;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return null != mSubjects ? mSubjects.size() : 0;
    }

    @Override
    public Subject getItem(int position) {
        return null != mSubjects ? mSubjects.get(position - 1) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_channel_list, null, false);

            ViewHolder holder = new ViewHolder();

            holder.icon = (AsyncImageView) convertView.findViewById(R.id.channel_icon);

            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        String imageUrl = mSubjects.get(position).getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            holder.icon.setImageAsync(mSubjects.get(position).getImageUrl());
        }

        return convertView;
    }

    static class ViewHolder {
        AsyncImageView icon;
    }
}
