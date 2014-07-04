package com.pplive.liveplatform.widget.chat;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.comment.model.FeedItem;
import com.pplive.liveplatform.ui.player.Emoji;
import com.pplive.liveplatform.util.TimeHelper;

public class ChatContentAdapter extends BaseAdapter {

    static final String TAG = ChatContentAdapter.class.getSimpleName();

    private Context mContext;

    private List<FeedItem> mFeedItems;

    private LayoutInflater mInflater;

    private float mTextSize;

    private Html.ImageGetter mImageGetter = new Html.ImageGetter() {

        @Override
        public Drawable getDrawable(String source) {

            Log.d(TAG, "source: " + source);

            Drawable drawable = mContext.getResources().getDrawable(Emoji.EMOJI_ICON_MAP.get(source));
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            return drawable;
        }
    };

    public ChatContentAdapter(Context context, List<FeedItem> mFeedItems) {
        super();

        this.mContext = context;
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
    public Object getItem(int position) {
        return mFeedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_chat, parent, false);
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
        holder.contentTextView.setText(Html.fromHtml(data.formatedContent, mImageGetter, null));
        holder.timeTextView.setText(TimeHelper.getAboutStartTime(mContext.getResources(), data.time));
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
