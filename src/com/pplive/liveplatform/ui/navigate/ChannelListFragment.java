package com.pplive.liveplatform.ui.navigate;

import android.app.Service;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.model.Subject;
import com.pplive.liveplatform.ui.widget.SearchTopBarView;

public class ChannelListFragment extends Fragment {

    static final String TAG = ChannelListFragment.class.getSimpleName();

    private SearchTopBarView mSearchTopBarView;

    private PullToRefreshListView mListViewChannel;

    private CallbackListener mCallbackListener;

    private BaseAdapter mAdapter = new BaseAdapter() {

        final int[] icons = new int[] { R.drawable.channel_list_tvbar, R.drawable.channel_list_gamebar, R.drawable.channel_list_sportbar,
                R.drawable.channel_list_financebar, R.drawable.channel_list_squarebar };

        final String[] names = new String[] { "电视台", "游戏", "体育", "财经", "公共摄像头" };

        final int[] subjectIds = new int[] { 2, 3, 4, 5, 6 };

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (null == convertView) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.layout_channel_list_item, null);

                holder = new ViewHolder();

                holder.icon = (ImageView) convertView.findViewById(R.id.channel_icon);
                holder.name = (TextView) convertView.findViewById(R.id.channel_name);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.icon.setImageResource(icons[position]);
            holder.name.setText(names[position]);

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Subject getItem(int position) {
            return new Subject(subjectIds[position - 1], names[position - 1], icons[position - 1]);
        }

        @Override
        public int getCount() {
            return icons.length;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_channel_list, container, false);

        mSearchTopBarView = (SearchTopBarView) layout.findViewById(R.id.search_top_bar);
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
                    mCallbackListener.onSubjectSelected(((Subject) mAdapter.getItem(position)));
                }
            }
        });

        mListViewChannel.setAdapter(mAdapter);

        return layout;
    }

    public void setCallbackListener(CallbackListener listener) {
        mCallbackListener = listener;
    }

    public interface CallbackListener {
        void onSubjectSelected(Subject subject);
    }
}

class ViewHolder {
    ImageView icon;
    TextView name;
}
