package com.pplive.liveplatform.ui.widget.chat;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.comment.model.FeedItem;

public class ChatContentAdapter extends BaseAdapter {

    private List<FeedItem> mFeedItems;

    private LayoutInflater mInflater;

    private float mTextSize;

    public ChatContentAdapter(Context context, List<FeedItem> mFeedItems) {
        super();
        this.mFeedItems = mFeedItems;
        this.mInflater = LayoutInflater.from(context);
        this.mTextSize = 16.0f;
    }

    public void setTextSize(float size) {
        mTextSize = size;
    }

    @Override
    public int getCount() {
        return mFeedItems.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mFeedItems.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_chat_item, parent, false);
            holder = new ViewHolder();
            holder.contentTextView = (TextView) convertView.findViewById(R.id.text_chat_content);
            holder.timeTextView = (TextView) convertView.findViewById(R.id.text_chat_time);
            holder.contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            holder.timeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        updateView(holder, mFeedItems.get(position));
        return convertView;
    }

    private void updateView(ViewHolder holder, FeedItem data) {
        holder.contentTextView.setText(Html.fromHtml(data.formatedContent));
        holder.timeTextView.setText(data.time);
    }

    static class ViewHolder {
        TextView contentTextView;
        TextView timeTextView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
