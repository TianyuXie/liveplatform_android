package com.pplive.liveplatform.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pplive.liveplatform.Extra;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.live.model.Subject;
import com.pplive.liveplatform.core.api.live.model.Tag;
import com.pplive.liveplatform.ui.ChannelActivity;

public class DiscoveryAdapter extends BaseExpandableListAdapter {

    static final String TAG = DiscoveryAdapter.class.getSimpleName();

    private Context mContext;

    private LayoutInflater mInflater;

    private List<Subject> mSubjects = new ArrayList<Subject>();

    private List<Tag> mTags = new ArrayList<Tag>();

    public DiscoveryAdapter(Context context) {

        mContext = context;
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
        return 2;
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

            convertView = mInflater.inflate(R.layout.item_discovery_group, parent, false);

            holder = new GroupViewHolder();
            holder.textGroup = (TextView) convertView.findViewById(R.id.group_text);

            convertView.setTag(holder);
        }

        holder = (GroupViewHolder) convertView.getTag();

        if (0 == groupPosition) {
            holder.textGroup.setText(R.string.channel);
        } else if (1 == groupPosition) {
            holder.textGroup.setText(R.string.tag);
        }

        return convertView;
    }

    @Override
    public int getChildTypeCount() {
        return 2;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (0 == groupPosition) {
            convertView = getChannelView(childPosition, convertView, parent);
        } else if (1 == groupPosition) {
            convertView = getTagsView(childPosition, convertView, parent);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return 0 == groupPosition;
    }

    private int mapToDrawableByChannelId(int channelId) {
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

    private int mapToStringByChannelId(int channelId) {
        switch (channelId) {
        case ORIGIN:
            return R.string.channel_origin;
        case TV:
            return R.string.channel_tv;
        case GAME:
            return R.string.channel_game;
        case SPORTS:
            return R.string.channel_sports;
        case STOCKS:
            return R.string.channel_stocks;
        case ACTIVITY:
            return R.string.channel_activity;
        }

        return -1;
    }

    private View getChannelView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {

            convertView = mInflater.inflate(R.layout.item_channel_list, parent, false);

            ChannelViewHolder holder = new ChannelViewHolder();

            holder.textChannel = (TextView) convertView.findViewById(R.id.channel_icon);

            convertView.setTag(holder);
        }

        ChannelViewHolder holder = (ChannelViewHolder) convertView.getTag();

        Subject subject = mSubjects.get(position);
        holder.textChannel.setBackgroundResource(mapToDrawableByChannelId(subject.getId()));
        holder.textChannel.setText(mapToStringByChannelId(subject.getId()));

        return convertView;
    }

    private View getTagsView(int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_tags, parent, false);

            TagsViewHolder holder = new TagsViewHolder();

            holder.textLeftTag = (TextView) convertView.findViewById(R.id.text_left_tag);
            holder.textRightTag = (TextView) convertView.findViewById(R.id.text_right_tag);

            convertView.setTag(holder);
        }

        TagsViewHolder holder = (TagsViewHolder) convertView.getTag();

        updateTag(position * 2, holder.textLeftTag);
        updateTag(position * 2 + 1, holder.textRightTag);

        return convertView;
    }

    private void updateTag(final int position, TextView text) {
        if (position < mTags.size()) {
            text.setText(mTags.get(position).getTagName());

            text.setBackgroundResource(mapToResId(position));

            text.setVisibility(View.VISIBLE);
        } else {
            text.setVisibility(View.INVISIBLE);
        }

        text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");

                Intent intent = new Intent(mContext, ChannelActivity.class);
                intent.putExtra(Extra.KEY_TAG, mTags.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    private int mapToResId(int position) {
        int pos = position % 6;

        switch (pos) {
        case 0:
            return R.drawable.tag_bg_1;
        case 1:
            return R.drawable.tag_bg_2;
        case 2:
            return R.drawable.tag_bg_3;
        case 3:
            return R.drawable.tag_bg_4;
        case 4:
            return R.drawable.tag_bg_5;
        case 5:
            return R.drawable.tag_bg_6;
        }

        return -1;
    }

    static class GroupViewHolder {
        TextView textGroup;
    }

    static class ChannelViewHolder {
        TextView textChannel;
    }

    static class TagsViewHolder {
        TextView textLeftTag;
        TextView textRightTag;
    }

    static final int ORIGIN = 1;

    static final int TV = 2;

    static final int GAME = 3;

    static final int SPORTS = 4;

    static final int STOCKS = 5;

    static final int ACTIVITY = 6;
}
