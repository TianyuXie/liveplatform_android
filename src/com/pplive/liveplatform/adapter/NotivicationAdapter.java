package com.pplive.liveplatform.adapter;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pplive.android.image.AsyncImageView;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.live.model.Feed;
import com.pplive.liveplatform.core.api.live.model.Feed.FeedType;
import com.pplive.liveplatform.util.TimeHelper;

public class NotivicationAdapter extends RefreshAdapter<Feed> {

    private Context mContext;

    private LayoutInflater mInflater;

    public NotivicationAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getViewTypeCount() {
        return FeedType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getFeedType().ordinal();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {

            FeedType type = getItem(position).getFeedType();

            int resId = FeedType.FOLLOW_FRIEND == type ? R.layout.item_sns_notification : R.layout.item_ugc_notification;

            convertView = mInflater.inflate(resId, parent, false);

            holder = new ViewHolder();
            holder.imageIcon = (AsyncImageView) convertView.findViewById(R.id.image_icon);
            holder.textSubject = (TextView) convertView.findViewById(R.id.text_subject);
            holder.textAction = (TextView) convertView.findViewById(R.id.text_action);
            holder.textDate = (TextView) convertView.findViewById(R.id.text_date);

            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        updateView(holder, position);

        return convertView;
    }

    public void updateView(ViewHolder holder, int position) {
        Feed feed = getItem(position);

        updateImageIcon(holder, feed);
        updateTextSubject(holder, feed);
        updateTextAction(holder, feed);
        updateTextDate(holder, feed);
    }

    private void updateImageIcon(ViewHolder holder, Feed feed) {
        holder.imageIcon.setImageAsync(FeedType.FOLLOW_FRIEND == feed.getFeedType() ? feed.getFans().getIcon() : feed.getProgram().getRecommendCover());
    }

    private void updateTextSubject(ViewHolder holder, Feed feed) {
        holder.textSubject.setText(FeedType.FOLLOW_FRIEND == feed.getFeedType() ? feed.getFans().getDisplayName() : feed.getProgram().getTitle());
    }

    private void updateTextAction(ViewHolder holder, Feed feed) {
        String action = "";
        switch (feed.getFeedType()) {
        case FOLLOW_FRIEND:
            action = "关注了你";
            break;
        case UPLOAD:
            action = "上传成功";
            break;
        case CREATE_PROGRAM:
            action = "创建成功";
            break;
        case AUDIT_PROGRAM:
            action = "通过审核";
            break;
        }

        holder.textAction.setText(action);
    }

    private void updateTextDate(ViewHolder holder, Feed feed) {
        holder.textDate.setText(TimeHelper.getAboutStartTime(mContext.getResources(), feed.getCreateTime()));
    }

    static class ViewHolder {
        AsyncImageView imageIcon;
        TextView textSubject;
        TextView textAction;
        TextView textDate;
    }

}
