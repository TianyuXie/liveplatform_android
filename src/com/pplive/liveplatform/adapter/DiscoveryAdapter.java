package com.pplive.liveplatform.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pplive.android.image.AsyncImageView;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.live.model.Subject;

public class DiscoveryAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mInflater;

    private List<Subject> mSubjects = new ArrayList<Subject>();

    private List<Tag> mTags = new ArrayList<Tag>();

    public DiscoveryAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Subject> subjects, List<Tag> tags) {
        if (null != subjects) {
            mSubjects.clear();
            mSubjects.addAll(subjects);
        }

        if (null != tags) {
            mTags.clear();
            mTags.addAll(tags);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (0 == groupPosition) {
            return mSubjects.size();
        } else if (1 == groupPosition) {
            return mTags.size() / 2 + 1;
        }

        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        if (0 == groupPosition) {
            return mSubjects.get(childPosition);
        } else if (1 == groupPosition) {
            return mTags.get(childPosition);
        }

        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 1000 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_search_group, parent, false);

            holder = new GroupViewHolder();
            holder.textGroup = (TextView) convertView.findViewById(R.id.group_text);

            convertView.setTag(holder);
        }

        holder = (GroupViewHolder) convertView.getTag();

        holder.textGroup.setText("频道");

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_channel_list, parent, false);

            ViewHolder holder = new ViewHolder();

            holder.icon = (AsyncImageView) convertView.findViewById(R.id.channel_icon);

            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.icon.setImageResource(mapToResId(mSubjects.get(childPosition).getId()));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private int mapToResId(int channelId) {
        switch (channelId) {
        case ORIGIN:
            return R.drawable.discovery_origin;
        case TV:
            return R.drawable.discovery_tv;
        case GAME:
            return R.drawable.discovery_game;
        case SPORTS:
            return R.drawable.discovery_sports;
        case STOCKS:
            return R.drawable.discovery_stocks;
        case ACTIVITY:
            return R.drawable.discovery_activity;
        }

        return -1;
    }

    static class GroupViewHolder {
        TextView textGroup;
    }

    static class ViewHolder {
        AsyncImageView icon;
    }

    static final int ORIGIN = 1;

    static final int TV = 2;

    static final int GAME = 3;

    static final int SPORTS = 4;

    static final int STOCKS = 5;

    static final int ACTIVITY = 6;
}
